
# 独立中转适配器服务（Hikvision Adapter Service）完整设计文档

## 1. 业务背景与架构目标

### 1.1 背景问题
上游业务系统存在批量校验人员/人脸信息的场景（每次约 300 人）。下游海康威视（Hikvision）开放平台仅提供单人校验 API（1次/人）。直接在主服务中同步循环或通过无界线程池调用海康 API，会导致主服务 JVM 堆内存积压大量 HTTP 请求对象与未完成的 Future，引发频繁 GC 甚至 OOM（OutOfMemoryError）。

### 1.2 架构目标
* **风险舱壁隔离**：高频 IO 密集型的并发转换逻辑完全剥离至独立中转服务，确保主服务高可用与零 OOM 风险。
* **内存安全控流**：中转服务内部严格控制最大并发度，配合连接池复用，确保内存开销恒定。
* **极速响应**：主服务调用中转服务响应时间控制在 50ms 内（提交即返回），彻底断开长连接依赖。
* **多通道结果解耦**：支持 Webhook、MQ 消息队列、SSE 实时流及轮询等多种结果异步通知机制。

---

## 2. 系统总体交互时序图 (Sequence Diagram)


```

┌───────────┐                     ┌────────────────────────┐                    ┌───────────────────┐
│ 业务主服务 │                     │ 独立中转服务 (Adapter)  │                    │  海康单人校验 API │
└─────┬─────┘                     └───────────┬────────────┘                    └─────────┬─────────┘
│                                       │                                           │
│ 1. POST /async-batch (300人+回调配置)  │                                           │
├──────────────────────────────────────►│                                           │
│                                       │ ──┐ 2. 生成 taskId                        │
│                                       │   │    初始化任务状态 (PROCESSING)         │
│                                       │ ◄─┘                                       │
│ 3. 200 OK (返回 taskId, status)       │                                           │
│◄──────────────────────────────────────┤                                           │
│  (主服务连接立即释放，耗时 < 50ms)     │                                           │
│                                       │                                           │
│                                       │ ════════════ 阶段 B: 后台异步处理 ════════════│
│                                       │                                           │
│                                       │ 4. 受控并发调用 (Semaphore = 15)           │
│                                       ├──────────────────────────────────────────►│
│                                       │◄──────────────────────────────────────────┤
│                                       │    (循环/并发处理，耗时约 5~15秒)          │
│                                       │                                           │
│                                       │ ──┐ 5. 300人全部校验完成                  │
│                                       │   │    更新任务状态 (COMPLETED)           │
│                                       │ ◄─┘                                       │
│                                       │                                           │
│ 6. 根据 callbackConfig 进行结果通知    │                                           │
│    (例如: Webhook POST / MQ 消息)     │                                           │
│◄──────────────────────────────────────┤                                           │
│                                       │                                           │

```

---

## 3. 中转服务（Adapter Service）API 规范与技术实现

### 3.1 统一任务提交接口
* **URL**: `POST /adapter/hikvision/async-batch`
* **Content-Type**: `application/json`
* **Request Body**:
```json
{
  "batchId": "BATCH_20260730_001",
  "persons": [
    {
      "personId": "P001",
      "name": "张三",
      "faceUrl": "[http://img.example.com/face1.jpg](http://img.example.com/face1.jpg)"
    }
  ],
  "callbackConfig": {
    "type": "WEBHOOK", // 可选: WEBHOOK | MQ | SSE | POLLING
    "webhookUrl": "http://main-service/api/v1/hik-callback",
    "mqTopic": "hikvision-verify-topic",
    "mqRoutingKey": "hik.verify.result"
  }
}

```

* **Response (200 OK)**:

```json
{
  "code": 200,
  "message": "任务接收成功",
  "data": {
    "taskId": "TASK_9f8b7a6c",
    "status": "PROCESSING",
    "total": 300
  }
}

```

### 3.2 轮询/兜底状态查询接口

* **URL**: `GET /adapter/hikvision/task-status?taskId=TASK_9f8b7a6c`
* **Response (200 OK)**:

```json
{
  "taskId": "TASK_9f8b7a6c",
  "status": "COMPLETED", // PROCESSING | COMPLETED | FAILED
  "total": 300,
  "processed": 300,
  "details": [
    {
      "personId": "P001",
      "passed": true,
      "errorMsg": null
    },
    {
      "personId": "P002",
      "passed": false,
      "errorMsg": "特征值不匹配"
    }
  ]
}

```

### 3.3 中转服务核心控制代码

```java
@RestController
@RequestMapping("/adapter/hikvision")
@Slf4j
public class HikAdapterController {

    @Autowired
    private OkHttpClient okHttpClient; // 全局复用长连接池
    
    // 严格限制最大并发为 15，防止压垮海康侧或造成中转服务内存爆满
    private final Semaphore semaphore = new Semaphore(15);
    
    private final ExecutorService executor = new ThreadPoolExecutor(
            20, 20, 60L, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(2000),
            new ThreadFactoryBuilder().setNameFormat("hik-async-%d").build(),
            new ThreadPoolExecutor.CallerRunsPolicy() // 背压控制
    );

    @PostMapping("/async-batch")
    public ResponseEntity<Map<String, Object>> submitBatch(@RequestBody BatchVerifyRequest request) {
        String taskId = StringUtils.hasText(request.getBatchId()) ? 
                request.getBatchId() : "TASK_" + UUID.randomUUID().toString().replace("-", "");
        
        // 1. 初始化任务状态
        initTaskState(taskId, request.getPersons().size());

        // 2. 提交异步消费流，非阻塞
        CompletableFuture.runAsync(() -> processBatchAsync(taskId, request), executor);

        // 3. 极速响应主服务
        Map<String, Object> result = new HashMap<>();
        result.put("taskId", taskId);
        result.put("status", "PROCESSING");
        return ResponseEntity.ok(result);
    }

    private void processBatchAsync(String taskId, BatchVerifyRequest request) {
        List<CompletableFuture<PersonResultDetail>> futures = request.getPersons().stream()
            .map(person -> CompletableFuture.supplyAsync(() -> {
                try {
                    semaphore.acquire(); // 背压许可
                    return callHikvisionSingleApi(person);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return PersonResultDetail.fail(person.getPersonId(), "中断");
                } finally {
                    semaphore.release();
                }
            }, executor))
            .collect(Collectors.toList());

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        
        // 汇总结果并触发多通道通知（Webhook / MQ）
        TaskStatusResponse finalResult = buildFinalResult(taskId, futures);
        notifyMainService(finalResult, request.getCallbackConfig());
    }
}

```

---

## 4. 主服务（调用方）集成建议与最佳实践

### 4.1 集成架构设计原则

1. **非阻塞调用**：主服务绝不能在 HTTP 接口中同步等待 300 人的处理结果，必须采用“异步提交 + 异步接收通知”模式。
2. **状态机管理**：主服务本地数据库应建立任务批次状态机（如 `PENDING` -> `VERIFYING` -> `COMPLETED` / `FAILED`）。
3. **幂等性保障**：结果通知（尤其是 Webhook/MQ）可能会因为重试而多次到达，主服务更新接口必须实现幂等。

---

### 4.2 主服务代码实现规范

#### 1. 任务提交服务（MainBusinessService）

```java
@Service
@Slf4j
public class MainBusinessService {

    @Autowired
    private RestTemplate restTemplate;

    @Transactional
    public String submitPersonImport(List<PersonInfo> persons) {
        String batchId = "BATCH_" + System.currentTimeMillis();

        // 1. 本地落库，标记该批次及其人员状态为 VERIFYING
        saveBatchToDb(batchId, persons, "VERIFYING");

        // 2. 组装请求，设置 Webhook 或 MQ 回调配置
        Map<String, Object> request = new HashMap<>();
        request.put("batchId", batchId);
        request.put("persons", persons);

        Map<String, Object> callbackConfig = new HashMap<>();
        callbackConfig.put("type", "WEBHOOK");
        callbackConfig.put("webhookUrl", "http://main-service-internal/api/v1/hik-callback");
        request.put("callbackConfig", callbackConfig);

        // 3. 调用中转服务（耗时 < 50ms，主服务线程快速释放）
        try {
            restTemplate.postForObject("http://hik-adapter-service/adapter/hikvision/async-batch", request, Map.class);
        } catch (Exception e) {
            log.error("调用中转适配服务失败, batchId: {}", batchId, e);
            updateBatchStatus(batchId, "FAILED");
            throw new BusinessException("提交校验任务失败");
        }

        return batchId;
    }
}

```

#### 2. 回调接收控制器（HikCallbackController - 幂等处理）

```java
@RestController
@RequestMapping("/api/v1")
@Slf4j
public class HikCallbackController {

    @Autowired
    private MainBusinessService businessService;

    @PostMapping("/hik-callback")
    public ResponseEntity<String> handleHikCallback(@RequestBody TaskStatusResponse callbackResult) {
        String batchId = callbackResult.getTaskId();
        log.info("收到中转服务校验回调通知, batchId: {}", batchId);

        // 幂等校验：检查数据库中该批次状态，防止重复消费
        if (businessService.isBatchCompleted(batchId)) {
            log.warn("批次 [{}] 已经处理过，忽略重复通知", batchId);
            return ResponseEntity.ok("SUCCESS");
        }

        // 更新数据库中 300 人的校验明细与批次总体状态
        businessService.updateBatchResult(batchId, callbackResult.getDetails());
        return ResponseEntity.ok("SUCCESS");
    }
}

```

#### 3. 主服务降级与自动兜底补偿机制（Scheduler）

网络抖动或主服务重启可能导致 Webhook 回调丢失。主服务须开启定时补偿 Job：

```java
@Component
@Slf4j
public class HikVerifyCompensationJob {

    @Autowired
    private MainBusinessService businessService;
    
    @Autowired
    private RestTemplate restTemplate;

    // 每 5 分钟扫描一次超过 3 分钟仍处于 VERIFYING 状态的批次
    @Scheduled(cron = "0 */5 * * * ?")
    public void compensateUnfinishedTasks() {
        List<String> timeoutBatchIds = businessService.findTimeoutVerifyingBatches(3, TimeUnit.MINUTES);
        
        for (String batchId : timeoutBatchIds) {
            try {
                // 主动查询中转服务状态
                TaskStatusResponse response = restTemplate.getForObject(
                    "http://hik-adapter-service/adapter/hikvision/task-status?taskId=" + batchId,
                    TaskStatusResponse.class
                );

                if (response != null && "COMPLETED".equals(response.getStatus())) {
                    log.info("兜底 Job 成功拉取到批次 [{}] 的结果，进行补更新", batchId);
                    businessService.updateBatchResult(batchId, response.getDetails());
                }
            } catch (Exception e) {
                log.error("兜底查询批次 [{}] 异常", batchId, e);
            }
        }
    }
}

```

---

## 5. 运维配置与部署建议

| 节点分类 | 关键配置项 | 参数建议 | 说明 |
| --- | --- | --- | --- |
| **中转适配服务** | JVM 参数 | `-Xms2g -Xmx2g -XX:+UseG1GC` | IO 密集型，2G 堆内存配合 G1GC 即可 |
|  | 并发信号量 | `Semaphore(15)` | 依海康侧接口承载力调节（建议 10~25） |
|  | HTTP 连接池 | MaxIdle: 50, KeepAlive: 5m | OkHttp 复用 TCP 握手，规避连接建销开销 |
| **主业务服务** | RestTemplate 超时 | ConnectTimeout: 3s, ReadTimeout: 5s | 调用中转服务极速返回，无需长超时 |
|  | 兜底 Job | 定时周期: 5min, 超时阈值: 3min | 防止 Webhook/MQ 极端丢失，实现最终一致 |

