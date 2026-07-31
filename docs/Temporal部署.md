部署 Temporal 方案其实非常简单。Temporal 官方提供了 **Docker Compose 一键部署包**，可以在几分钟内搭好包含 **Temporal Server + 数据库 + 开箱即用的 Web UI 控制台** 的完整集群。

下面为您提供一套**生产级/开发环境通用**的部署落地全流程。

---

## 一、 部署整体架构图

整个系统部署后由 3 个核心容器/组件构成：

```
┌────────────────────────────────────────────────────────────────────────┐
│                          Temporal 基础基础设施                         │
│                                                                        │
│   ┌──────────────────┐    ┌──────────────────┐    ┌────────────────┐   │
│   │  Temporal Server │───►│ PostgreSQL/MySQL │    │ Temporal Web UI│   │
│   │   (调度引擎核心) │    │  (持久化任务历史)│    │ (8233 监控面板)│   │
│   └────────┬─────────┘    └──────────────────┘    └────────────────┘   │
└────────────┼───────────────────────────────────────────────────────────┘
             │ (gRPC 端口 7233)
             ▼
┌─────────────────────────┐                ┌─────────────────────────┐
│ 中转服务 (Temporal Worker) │─── 限流并发 ──►│     海康 API 开放平台   │
│ (运行你的 Java/Go 代码)  │                │    (单个校验 15 QPS)    │
└─────────────────────────┘                └─────────────────────────┘

```

---

## 二、 第一步：部署 Temporal 基础设施 (Server + UI)

我们推荐使用官方推荐的 **PostgreSQL** 作为存储底座。

### 1. 创建 `docker-compose.yml`

在服务器上找一个目录（如 `/opt/temporal`），创建 `docker-compose.yml` 文件：

```yaml
version: '3.5'

services:
  # 1. 数据库持久化存储
  postgresql:
    image: postgres:15-alpine
    container_name: temporal-postgresql
    environment:
      - POSTGRES_USER=temporal
      - POSTGRES_PASSWORD=temporal
    volumes:
      - postgres_data:/var/lib/postgresql/data
    ports:
      - "5432:5432"

  # 2. Temporal 自动初始化 Schema 工具
  temporal-admin-tools:
    image: temporalio/admin-tools:1.24.2
    container_name: temporal-admin-tools
    environment:
      - DB=postgresql
      - DB_PORT=5432
      - POSTGRES_USER=temporal
      - POSTGRES_PWD=temporal
      - POSTGRES_SEEDS=postgresql
    depends_on:
      - postgresql

  # 3. Temporal Server 核心服务
  temporal:
    image: temporalio/auto-setup:1.24.2
    container_name: temporal-server
    ports:
      - "7233:7233" # gRPC 端口，提供给 Java/Go Worker 连接
    environment:
      - DB=postgres
      - DB_PORT=5432
      - POSTGRES_USER=temporal
      - POSTGRES_PWD=temporal
      - POSTGRES_SEEDS=postgresql
      - DYNAMIC_CONFIG_FILE_PATH=config/dynamicconfig/development-sql.yaml
    depends_on:
      - postgresql

  # 4. Temporal 可视化 Web UI
  temporal-ui:
    image: temporalio/ui:2.28.0
    container_name: temporal-ui
    ports:
      - "8233:8233" # Web UI 访问端口
    environment:
      - TEMPORAL_ADDRESS=temporal:7233
      - TEMPORAL_CORS_ORIGINS=http://localhost:8233
    depends_on:
      - temporal

volumes:
  postgres_data:

```

### 2. 启动服务

```bash
# 后台启动所有容器
docker compose up -d

# 查看运行状态
docker compose ps

```

启动完成后：

* **gRPC 地址**：`your-server-ip:7233`（给 Java Worker 连接使用）
* **Web UI 控制台**：浏览器打开 `http://your-server-ip:8233` 即可看到可视界面。

---

## 三、 第二步：编写与部署中转服务 (Java Worker)

中转服务只需作为一个普通的 Spring Boot / Java 微服务部署在任意 Docker 或 K8s 容器中。

### 1. 引入依赖 (`pom.xml`)

```xml
<dependency>
    <groupId>io.temporal</groupId>
    <artifactId>temporal-sdk</artifactId>
    <version>1.24.0</version>
</dependency>

```

### 2. 配置 Worker 连接与并发限流参数

这是 Temporal 最优雅的地方：**并发度控制（15 QPS）直接在代码初始化 Worker 时声明即可**：

```java
@Configuration
public class TemporalWorkerConfig {

    @Value("${temporal.server.address:127.0.0.1:7233}")
    private String temporalAddress;

    @Bean
    public WorkflowClient workflowClient() {
        // 1. 连接到 Temporal Server
        WorkflowServiceStubs service = WorkflowServiceStubs.newConnectedServiceStubs(
            WorkflowServiceStubsOptions.newBuilder()
                .setTarget(temporalAddress)
                .build(),
            Duration.ofSeconds(5)
        );
        return WorkflowClient.newInstance(service);
    }

    @Bean
    public WorkerFactory workerFactory(WorkflowClient client, HikActivity hikActivity) {
        WorkerFactory factory = WorkerFactory.newInstance(client);

        // 2. 创建监听 "HIK_BATCH_TASK_QUEUE" 队列的 Worker
        Worker worker = factory.newWorker("HIK_BATCH_TASK_QUEUE", 
            WorkerOptions.newBuilder()
                // 核心限流配置：严格控制该 Worker 全局并发 Activity（调海康API）不超过 15！
                .setMaxConcurrentActivityExecutionSize(15)
                .build()
        );

        // 3. 注册工作流与具体执行类
        worker.registerWorkflowImplementationTypes(HikBatchWorkflowImpl.class);
        worker.registerActivitiesImplementations(hikActivity);

        factory.start(); // 启动 Worker 监听
        return factory;
    }
}

```

---

## 四、 第三步：主服务 (Client) 接入调用方式

主服务（业务系统）只需要引入 `temporal-sdk`，然后通过几行代码触发任务（耗时小于 **5ms**）：

```java
@Service
public class MainBusinessService {

    @Autowired
    private WorkflowClient workflowClient;

    public String submitBatchVerify(List<PersonInfo> persons) {
        String batchId = "BATCH_" + System.currentTimeMillis();

        // 1. 设置 Workflow 配置
        WorkflowOptions options = WorkflowOptions.newBuilder()
                .setTaskQueue("HIK_BATCH_TASK_QUEUE")
                .setWorkflowId(batchId) // 将 batchId 作为 WorkflowUniqueId，自动天然防重
                .build();

        // 2. 创建 Workflow Stub
        HikBatchWorkflow workflow = workflowClient.newWorkflowStub(HikBatchWorkflow.class, options);

        // 3. 异步启动 Workflow (非阻塞，极速返回，耗时 < 5ms)
        WorkflowClient.start(workflow::processBatch, new BatchRequest(batchId, persons));

        // 4. 直接把 batchId 返回给前端/上游
        return batchId;
    }
}

```

---

## 五、 部署后的日常运维与监控流程

部署完成后，整个系统的运转流程如下：

1. **主服务提交**：主服务调用 `WorkflowClient.start()`，发一条轻量级的 gRPC 指令给 Temporal Server (`7233`)，立马返回。
2. **中转服务拉取**：中转服务 (Worker) 通过长轮询从 Temporal Server 拉取任务。因为配置了 `setMaxConcurrentActivityExecutionSize(15)`，Worker 会以**恒定不超过 15 个并发**的速率处理调海康 API。
3. **可视化监控与排查**：
* 打开 `http://<IP>:8233` 面板。
* 可以搜索任意 `batchId`。
* 在界面上能清晰看到：300 个人里，哪几个人校验成功、哪几个失败了（带完整 Response JSON），以及海康 API 报错自动重试的倒计时。