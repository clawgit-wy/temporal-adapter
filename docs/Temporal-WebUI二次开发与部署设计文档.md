# Temporal Web UI 二次开发与部署架构设计文档

> 本文档针对「自研 Temporal Web UI 二次开发」及「定制化部署架构」进行完整设计。
> 部署采用 **四层分离** 架构：UI 与 Nginx 同机、Temporal Server 与 BFF 同机、数据库独立服务器、Runner 独立集群。
> 文档首先阐明该拓扑的设计原则与技术依据，随后给出 Web UI 二次开发设计与各服务器部署实施方案。

---

## 一、部署架构设计

### 1.1 总体部署拓扑（四层分离）

| 服务器 | 部署组件 | 角色 |
| --- | --- | --- |
| **服务器 A** | Nginx + 自研 UI 前端静态资源 | 接入层（静态托管 + 反向代理） |
| **服务器 B** | Temporal Server + 自研 UI BFF 后端 | 核心控制面（调度 + BFF gRPC 走 localhost） |
| **服务器 C** | Temporal 数据库（PostgreSQL / MySQL） | 持久化层（独立服务器，与 B 同可用区） |
| **服务器 D/E/N** | Runner（Worker）集群 | 数据面（无状态，横向扩缩容） |

> 业务应用（启动 Workflow 的主应用）可独立部署于服务器 E 或与现有业务集群合并，通过 gRPC/REST 访问服务器 B，详见 3.7。

```
+---------------------------------------------------------------------------+
|  服务器 A (接入层)                                                          |
|  +------------------+   +------------------+                              |
|  | 自研 UI 前端      |   | Nginx            |                              |
|  | (Vue3 静态资源)   |-->| (静态托管+反代)   |                              |
|  +------------------+   +--------+---------+                              |
|                                  | HTTPS (REST/WS)                        |
+----------------------------------|-----------------------------------------+
                                   | 跨网络 (内网/专线)
                                   v
+---------------------------------------------------------------------------+
|  服务器 B (核心控制面)                       服务器 C (持久化层, 同可用区)    |
|  +------------------+   +------------------+   +-----------------------+   |
|  | 自研 UI BFF 后端  |-->| Temporal Server  |-->|  Temporal 数据库       |   |
|  | (Java/Spring)    |   | (Frontend/History |   |  (PostgreSQL/MySQL)   |   |
|  | localhost gRPC   |   |  /Matching) :7233 |   |  低延迟内网互通        |   |
|  +------------------+   +--------+---------+   +-----------------------+   |
|                                  |                                       |
+----------------------------------|-----------------------------------------+
                                   | gRPC 长轮询 (跨网络/跨机房均可)
                                   v
+---------------------------------------------------------------------------+
|  服务器 D/E/N (数据面 / Runner 集群, 可水平扩展)                            |
|  [ Worker 实例 1 ]   [ Worker 实例 2 ]   [ Worker 实例 N ... ]             |
|   纯业务执行 (Activity/Workflow), 无状态, 支持 HPA 横向扩容               |
+---------------------------------------------------------------------------+
```

### 1.2 架构设计原则与技术依据

#### 1.2.1 Temporal 的数据流向

```
业务应用 --StartWorkflow--> Temporal Server (Frontend) --+--> [History/Matching] --持久化--> 数据库
                                                          |
Runner(Worker) <--gRPC长轮询(7233)-- Temporal Server <----+   (Worker 从不直连数据库)
```

**两个核心事实：**

1. **Runner（Worker）不直接访问数据库。**
   Runner 只通过 gRPC（默认 7233 端口）与 Temporal Server 的 Frontend Service 通信，采用长轮询方式从 Task Queue 拉取任务，并将 Activity 执行结果回传给 Server。**Runner 与数据库之间不存在任何直接网络调用**，因此 Runner 部署位置完全独立，跨网络访问 Server 是 Temporal 的标准设计，长轮询机制本身可容忍网络波动。

2. **Temporal Server 与数据库的链路是整个系统最延迟敏感的路径。**
   Temporal Server 的 History Service 几乎对每一个 Workflow 事件、Activity 完成、Heartbeat、Timer 都要进行持久化读写。Server ↔ DB 之间的每一次网络往返（RTT）都会直接叠加到任务调度的关键路径上。

#### 1.2.2 四条设计原则

| 原则 | 说明 | 在本拓扑中的体现 |
| --- | --- | --- |
| **① Server 与 DB 必须同可用区（低延迟内网）** | 官方要求 RTT < 1ms。关键是「同低延迟网络」，而非「同物理机」 | 服务器 B 与服务器 C 部署于同一可用区/同机房，内网互通 |
| **② DB 独立服务器优于同机** | 独立后 DB 拥有专属 I/O 与 CPU，避免与 Server 争抢；便于独立调优、备份、垂直扩容 | 服务器 C 独立部署数据库 |
| **③ BFF 与 Temporal Server 同机** | BFF 通过 gRPC 调用 Server，走 localhost 延迟最低 | 服务器 B 同机部署 BFF + Server |
| **④ Runner 独立集群** | Runner 是无状态数据面，可按 Task Queue 积压自由横向扩容；与 Server/DB 同机会资源争抢、限制扩容、故障耦合 | 服务器 D/E/N 独立部署 |

#### 1.2.3 为什么 DB 独立服务器是更优选择

将数据库从 Temporal Server 所在机器拆出，独立为服务器 C，**前提是 B 与 C 处于同一可用区、通过低延迟内网互通（RTT < 1ms）**。在此前提下，独立部署相比同机部署具有显著优势：

| 维度 | Server+DB 同机 | DB 独立服务器（本方案） |
| --- | --- | --- |
| 资源争抢 | Server 与 DB 共享 CPU/内存/IO，高并发时互相影响 | DB 拥有专属资源，I/O 稳定 |
| 独立调优 | DB 参数与 Server 容器混部，调优受限 | DB 可单独调整连接池、shared_buffers、WAL 等 |
| 备份与升级 | DB 维护窗口影响 Server | DB 可独立备份/升级，不影响 Server 运行 |
| 垂直扩容 | 扩容 DB 等于扩容整机 | DB 可独立纵向扩容（加 CPU/内存/SSD） |
| 故障隔离 | DB 进程异常可能波及同机 Server | 故障域隔离，DB 宕机不直接拖垮 Server 进程 |

> ⚠️ 红线：服务器 C（DB）**必须**与服务器 B（Server）处于同一可用区/同机房，通过内网或专线互联。**严禁跨可用区/跨地域部署**，否则持久化链路延迟将拖垮整体吞吐。

### 1.3 各层职责说明

| 层级 | 服务器 | 职责 | 关键约束 |
| --- | --- | --- | --- |
| 接入层 | A | Nginx 托管前端静态资源、HTTPS 终止、反向代理 API/WS 至 BFF | 纯静态，可多副本 + CDN |
| 控制面 | B | Temporal Server 负责任务调度与持久化编排；BFF 负责鉴权、数据聚合、SDK 接入 | BFF↔Server 走 localhost |
| 持久化层 | C | 数据库存储 Workflow 历史、Visibility 索引 | 与 B 同可用区，低延迟内网 |
| 数据面 | D/E/N | Worker 执行 Activity/Workflow，回传结果 | 无状态，横向扩缩容 |

---

## 二、Web UI 二次开发架构设计

> Temporal 官方 Web UI 本身是一个独立的前端应用（开源库为 `temporalio/ui` 与 `temporalio/ui-server`），通过 gRPC/HTTP 接口与 Temporal Server 通信，因此完全可以自研替换。自研 UI 的核心价值在于贴合业务场景：将 Workflow 状态直观地整合进内部管理后台，屏蔽底层 gRPC Event History 细节，只展示业务关心的状态与字段。

### 2.1 整体架构（BFF 模式）

采用 **BFF（Backend For Frontend）** 架构，前端不直连 Temporal gRPC，由自研后端层统一接入、鉴权与数据加工。

```
+-------------------+        HTTPS         +-------------------+
|   用户浏览器       |  <----------------> |   Nginx (服务器A)  |
|  (Vue3/React SPA) |                     | (静态托管+反向代理)|
+-------------------+                     +---------+---------+
                                                    |
                                                  REST/WS (跨网络内网)
                                                    |
                                          +---------v---------+
                                          |  自研 UI BFF 后端   |  (服务器B)
                                          | (Java/Spring Boot) |
                                          |  - 认证/RBAC       |
                                          |  - 数据聚合/脱敏    |
                                          |  - Temporal SDK    |
                                          |  - Codec 解密接入  |
                                          +---------+---------+
                                                    |
                                                  gRPC 7233 (localhost)
                                                    |
                                          +---------v---------+
                                          |  Temporal Server   |  (服务器B)
                                          +---------+---------+
                                                    |
                                                  低延迟内网
                                                    |
                                          +---------v---------+
                                          |  Temporal 数据库   |  (服务器C)
                                          +-------------------+
```

### 2.2 通信方式选型：BFF vs gRPC-Web

前端与 Temporal Server 的通信存在两种方案，本设计采用方案 A。

| 方案 | 说明 | 优缺点 |
| --- | --- | --- |
| **方案 A：BFF 接入（推荐，本方案采用）** | 前端通过自研 RESTful / GraphQL 接口与 BFF 后端通信，BFF 使用 Temporal 官方 SDK（Java / Go / TS / Python）连接 Server | ✅ 便于权限控制、字段清洗与数据二次加工；✅ 可集成 Codec Server 解密加密 Payload；✅ 前端无需处理 gRPC 协议复杂性 |
| **方案 B：直连 gRPC-Web** | 前端直接与 Temporal 通信，需配置 Nginx gRPC 代理或使用 Envoy 将 gRPC 转译为 gRPC-Web | ❌ 难以添加自定义业务权限校验；❌ 数据解密逻辑难以注入前端；⚠️ 前端需处理 gRPC-Web 协议复杂性 |

> 选型结论：方案 A 的 BFF 模式在鉴权、数据脱敏、业务整合方面优势明显，是自研 UI 的推荐架构。后续章节均基于方案 A 展开。

### 2.3 技术选型

| 层级 | 选型 | 理由 |
| --- | --- | --- |
| 前端 | Vue 3 + TypeScript + Vite + Element Plus / Ant Design Vue | 与 JeecgBoot 前端栈一致，便于整合进现有管理后台 |
| BFF 后端 | Java 17 + Spring Boot 3 + Temporal Java SDK | 与业务主应用同构，复用鉴权/审计体系 |
| 通信 | REST + WebSocket（实时事件推送） | REST 用于列表/控制类操作，WS 用于 History 流式推送 |
| 鉴权 | 复用业务系统 JWT + RBAC | 避免独立账号体系，统一权限管控 |

### 2.4 核心 API 设计

BFF 后端通过 Temporal Java SDK 的 `WorkflowClient` / `WorkflowServiceStubs` 与 Server 交互，对外暴露以下 RESTful 接口。

#### 2.4.1 工作流查询类

| 接口 | 方法 | 说明 | 对应 SDK 调用 |
| --- | --- | --- | --- |
| `/api/workflows` | GET | 分页查询工作流列表 | `ListWorkflowExecutionsRequest`（需 Visibility API） |
| `/api/workflows/{workflowId}/run/{runId}` | GET | 查询单次执行详情 | `DescribeWorkflowExecution` |
| `/api/workflows/{workflowId}/run/{runId}/history` | GET | 获取事件历史（分页） | `GetWorkflowExecutionHistory` |
| `/api/workflows/{workflowId}/run/{runId}/query/{queryType}` | POST | 查询 Workflow 内部状态 | `QueryWorkflow` |

#### 2.4.2 工作流控制类

| 接口 | 方法 | 说明 | 对应 SDK 调用 |
| --- | --- | --- | --- |
| `/api/workflows/{workflowId}/run/{runId}/signal` | POST | 发送 Signal | `SignalWorkflow` |
| `/api/workflows/{workflowId}/run/{runId}/cancel` | POST | 取消执行 | `CancelWorkflow` |
| `/api/workflows/{workflowId}/run/{runId}/terminate` | POST | 强制终止 | `TerminateWorkflow` |
| `/api/workflows/{workflowId}/run/{runId}/reset` | POST | 重置到指定 EventId | `ResetWorkflow` |

#### 2.4.3 业务侧封装

| 接口 | 方法 | 说明 |
| --- | --- | --- |
| `/api/workflows/types` | GET | 返回已注册的 Workflow 类型清单（业务侧维护白名单） |
| `/api/workflows/start` | POST | 业务封装的启动入口（参数校验 + 业务前置处理 + StartWorkflow） |

### 2.5 关键实现要点

#### 2.5.1 BFF 后端核心配置（Java）

```java
@Configuration
public class TemporalClientConfig {

    @Bean
    public WorkflowServiceStubs workflowServiceStubs(
            @Value("${temporal.server.address}") String address) {
        // 与本机 Temporal Server 建立 gRPC 连接 (localhost:7233)
        return WorkflowServiceStubs.newInstance(
                WorkflowServiceStubsOptions.newBuilder()
                        .setTarget(address)           // localhost:7233
                        .build());
    }

    @Bean
    public WorkflowClient workflowClient(WorkflowServiceStubs stubs,
                                         @Value("${temporal.namespace}") String namespace) {
        return WorkflowClient.newInstance(stubs, WorkflowClientOptions.newBuilder()
                .setNamespace(namespace)
                .build());
    }
}
```

#### 2.5.2 工作流列表查询 Service 示例

```java
@Service
@RequiredArgsConstructor
public class WorkflowQueryService {

    private final WorkflowClient workflowClient;

    /**
     * 分页查询工作流执行列表
     */
    public WorkflowListVO listWorkflows(WorkflowQueryParam param) {
        WorkflowServiceStubs stubs = workflowClient.getWorkflowServiceStubs();

        ListWorkflowExecutionsRequest request = ListWorkflowExecutionsRequest.newBuilder()
                .setNamespace(workflowClient.getOptions().getNamespace())
                .setQuery(buildQueryString(param))   // SQL-like 过滤
                .setPageSize(param.getPageSize())
                .setNextPageToken(param.getNextPageToken() == null
                        ? ByteString.EMPTY
                        : ByteString.copyFrom(param.getNextPageToken()))
                .build();

        ListWorkflowExecutionsResponse resp = stubs.blocking()
                .listWorkflowExecutions(request);

        // 业务侧字段过滤与脱敏
        List<WorkflowVO> items = resp.getExecutionsList().stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        return WorkflowListVO.builder()
                .items(items)
                .nextPageToken(resp.getNextPageToken().toByteArray())
                .build();
    }

    private String buildQueryString(WorkflowQueryParam p) {
        // 示例: "WorkflowType = 'orderProcess' AND ExecutionStatus = 'Running'"
        // 注意: 需启用 Advanced Visibility (Elasticsearch) 才支持复杂查询
        StringBuilder sb = new StringBuilder();
        if (StringUtils.hasText(p.getWorkflowType())) {
            sb.append("WorkflowType = '").append(p.getWorkflowType()).append("'");
        }
        if (StringUtils.hasText(p.getStatus())) {
            if (sb.length() > 0) sb.append(" AND ");
            sb.append("ExecutionStatus = '").append(p.getStatus()).append("'");
        }
        return sb.toString();
    }
}
```

#### 2.5.3 事件历史查询 + 解密（Codec 接入）

如果 Worker 侧通过 DataConverter 对 Payload 做了加密，BFF 需要调用 Codec Server 解密后再返回前端：

```java
public WorkflowHistoryVO getHistory(String workflowId, String runId) {
    GetWorkflowExecutionHistoryRequest request = GetWorkflowExecutionHistoryRequest.newBuilder()
            .setNamespace(namespace)
            .setExecution(WorkflowExecution.newBuilder()
                    .setWorkflowId(workflowId)
                    .setRunId(runId)
                    .build())
            .build();

    GetWorkflowExecutionHistoryResponse resp = stubs.blocking()
            .getWorkflowExecutionHistory(request);

    List<HistoryEventVO> events = resp.getHistory().getEventsList().stream()
            .map(this::convertEvent)        // 结构转换
            .map(this::decryptPayloads)     // 调用 Codec Server 解密
            .collect(Collectors.toList());

    return new WorkflowHistoryVO(events, resp.getNextPageToken().toByteArray());
}
```

#### 2.5.4 前端核心页面（Vue3 Composition API）

```vue
<template>
  <div class="workflow-list">
    <a-table :columns="columns" :data-source="dataSource" :pagination="pagination"
             :loading="loading" @change="handleTableChange" row-key="workflowId">
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'status'">
          <a-tag :color="statusColor(record.status)">{{ record.status }}</a-tag>
        </template>
        <template v-if="column.key === 'action'">
          <a @click="showHistory(record)">事件历史</a>
          <a-divider type="vertical" />
          <a @click="showControl(record)">控制</a>
        </template>
      </template>
    </a-table>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { message } from 'ant-design-vue';
import { listWorkflows, cancelWorkflow, terminateWorkflow } from '@/api/temporal';

interface WorkflowItem {
  workflowId: string;
  runId: string;
  workflowType: string;
  status: string;
  startTime: string;
  closeTime?: string;
}

const dataSource = ref<WorkflowItem[]>([]);
const loading = ref(false);
const pagination = ref({ current: 1, pageSize: 20, total: 0 });

const columns = [
  { title: '工作流ID', dataIndex: 'workflowId', key: 'workflowId' },
  { title: '类型', dataIndex: 'workflowType', key: 'workflowType' },
  { title: '状态', dataIndex: 'status', key: 'status' },
  { title: '开始时间', dataIndex: 'startTime', key: 'startTime' },
  { title: '操作', key: 'action', width: 180 },
];

const statusColor = (status: string) => ({
  Running: 'processing',
  Completed: 'success',
  Failed: 'error',
  Canceled: 'default',
  Terminated: 'warning',
}[status] || 'default');

async function fetchData() {
  loading.value = true;
  try {
    const res = await listWorkflows({
      pageSize: pagination.value.pageSize,
      page: pagination.value.current,
    });
    dataSource.value = res.items;
    pagination.value.total = res.total;
  } finally {
    loading.value = false;
  }
}

async function handleTableChange(p: any) {
  pagination.value = p;
  await fetchData();
}

function showHistory(record: WorkflowItem) {
  // 跳转事件历史页 / 打开抽屉
}
function showControl(record: WorkflowItem) {
  // 打开控制面板 (Signal/Cancel/Terminate)
}

onMounted(fetchData);
</script>
```

### 2.6 鉴权与权限设计

| 维度 | 策略 |
| --- | --- |
| 认证 | 复用业务系统 JWT，BFF 网关校验 Token 有效性 |
| RBAC | 角色区分：`viewer`（只读）、`operator`（可 Signal/Cancel）、`admin`（可 Terminate/Reset） |
| 数据隔离 | 按 namespace / workflowType 做数据权限过滤，BFF 在查询时注入过滤条件 |
| 审计 | 所有控制类操作（Cancel/Terminate/Reset/Signal）记录操作日志 |

---

## 三、部署实施方案

### 3.1 服务器角色划分

| 角色 | 部署组件 | 资源建议 | 说明 |
| --- | --- | --- | --- |
| **服务器 A（接入层）** | Nginx + 自研 UI 前端静态资源 | 2核4G ×N | 纯静态，可多副本 + CDN 加速 |
| **服务器 B（核心控制面）** | Temporal Server + BFF 后端 | 8核 16G+ | BFF↔Server 走 localhost；通过内网访问服务器 C 的 DB |
| **服务器 C（持久化层）** | Temporal 数据库 | 8核 16G+ SSD | 与 B 同可用区；专属 I/O，独立调优/备份 |
| **服务器 D/E/N（数据面）** | Runner（Worker）集群 | 按峰值弹性 | 无状态，横向扩缩容 |

### 3.2 服务器 A 部署：UI 前端 + Nginx（Docker Compose）

前端构建产物与 Nginx 同机部署，仅做静态托管 + 反向代理至服务器 B 的 BFF。

```yaml
version: "3.8"
services:
  nginx:
    image: nginx:1.25-alpine
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/conf.d/default.conf:ro
      - ./ui-dist:/usr/share/nginx/html:ro     # 前端构建产物
      - ./nginx/certs:/etc/nginx/certs:ro
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "wget", "-q", "--spider", "http://localhost/"]
      interval: 30s
    deploy:
      resources:
        limits:
          cpus: "2"
          memory: 2G
```

多副本高可用：接入层可部署 2+ 节点，前置 SLB（如阿里云 SLB / AWS ALB）做负载均衡。

### 3.3 服务器 A 的 Nginx 配置（反向代理至服务器 B 的 BFF）

```nginx
server {
    listen 443 ssl;
    server_name workflow.yourdomain.com;

    ssl_certificate     /etc/nginx/certs/server.crt;
    ssl_certificate_key /etc/nginx/certs/server.key;

    # 1. 前端静态资源
    location / {
        root /usr/share/nginx/html;
        index index.html;
        try_files $uri $uri/ /index.html;
        # 静态资源缓存
        location ~* \.(js|css|png|jpg|svg|woff2)$ {
            expires 7d;
            add_header Cache-Control "public, immutable";
        }
    }

    # 2. BFF 后端 API (反向代理至服务器 B)
    location /api/ {
        proxy_pass http://server-b.example.com:8080/;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header Authorization $http_authorization;
    }

    # 3. WebSocket (事件历史实时推送)
    location /ws/ {
        proxy_pass http://server-b.example.com:8080/ws/;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_read_timeout 3600s;
    }
}
```

### 3.4 服务器 B 部署：Temporal Server + BFF（Docker Compose）

服务器 B 不部署数据库（已独立到服务器 C），不部署 Runner（独立到数据面），不部署 Nginx（独立到服务器 A）。BFF 通过 `localhost:7233` 访问同机 Server，Server 通过内网地址访问服务器 C 的数据库。

```yaml
version: "3.8"
services:
  # 1. Temporal Server (与 BFF 同机, gRPC 走 localhost)
  temporal-server:
    image: temporalio/auto-setup:1.23
    environment:
      - DB=postgresql
      - DB_PORT=5432
      - POSTGRES_USER=temporal
      - POSTGRES_PWD=${DB_PASSWORD}
      - POSTGRES_SEEDS=db-server-c.example.com   # 服务器 C 的数据库内网地址
      - DYNAMIC_CONFIG_FILE_PATH=config/dynamicconfig/development.yaml
    ports:
      - "7233:7233"     # gRPC, 供 BFF(localhost) 与 Worker(跨网络) 连接
      - "7233:7233/udp"
    restart: unless-stopped
    deploy:
      resources:
        limits:
          cpus: "4"
          memory: 8G

  # 2. 自研 UI BFF 后端 (与 Server 同机, gRPC 走 localhost)
  ui-bff:
    image: your-registry/temporal-ui-bff:latest
    environment:
      - TEMPORAL_SERVER_ADDRESS=localhost:7233   # 同机访问, 延迟最低
      - TEMPORAL_NAMESPACE=default
      - JWT_SECRET=${JWT_SECRET}
    ports:
      - "8080:8080"     # 供服务器 A 的 Nginx 反向代理访问
    depends_on:
      - temporal-server
    restart: unless-stopped
    deploy:
      resources:
        limits:
          cpus: "2"
          memory: 4G
```

> 说明：`POSTGRES_SEEDS` 指向服务器 C 的内网域名/IP，需保证 B↔C 同可用区、内网低延迟（RTT < 1ms）。BFF 的 `TEMPORAL_SERVER_ADDRESS` 设为 `localhost:7233`，与 Server 同机通信零延迟。

### 3.5 服务器 C 部署：数据库（独立服务器）

数据库独立部署，拥有专属 CPU/内存/I/O，便于独立调优、备份与垂直扩容。

```yaml
version: "3.8"
services:
  temporal-db:
    image: postgres:15
    environment:
      POSTGRES_USER: temporal
      POSTGRES_PASSWORD: ${DB_PASSWORD}
      POSTGRES_DB: temporal
    volumes:
      - temporal-db-data:/var/lib/postgresql/data
    ports:
      - "5432:5432"     # 仅对服务器 B 内网开放, 禁止公网访问
    restart: unless-stopped
    deploy:
      resources:
        limits:
          cpus: "8"
          memory: 16G
    # 生产环境关键调优 (通过 postgresql.conf 挂载或命令行参数)
    command: >
      postgres
        -c shared_buffers=4GB
        -c effective_cache_size=12GB
        -c wal_buffers=64MB
        -c max_connections=300
        -c random_page_cost=1.1

volumes:
  temporal-db-data:
```

**安全与运维要求：**

1. **网络隔离**：服务器 C 的 5432 端口仅对服务器 B 内网开放，通过安全组/防火墙限制来源 IP，**严禁暴露公网**。
2. **同可用区**：服务器 C 必须与服务器 B 处于同一可用区/同机房，内网 RTT < 1ms。
3. **存储**：使用本地 SSD 或高性能云盘，保证 IOPS 与吞吐；`temporal-db-data` 建议挂载独立数据盘。
4. **备份**：定期 `pg_dump` / 物理快照，验证可恢复性。
5. **监控**：监控 DB 连接数、慢查询、磁盘水位、复制延迟（若启用）。

### 3.6 服务器 D/E/N 部署：Runner 集群（独立数据面）

```yaml
version: "3.8"
services:
  worker:
    image: your-registry/temporal-worker:latest
    environment:
      # 指向服务器 B 的 Temporal Server (跨网络, 长轮询容忍延迟)
      - TEMPORAL_SERVER_ADDRESS=server-b.example.com:7233
      - TEMPORAL_NAMESPACE=default
      - WORKER_TASK_QUEUE=order-task-queue
      - WORKER_CONCURRENCY=50
    deploy:
      resources:
        limits:
          cpus: "4"
          memory: 4G
    restart: unless-stopped
```

扩容方式：在服务器 D/E/F 重复部署，或使用 K8s HPA 按 Task Queue 积压自动扩缩容。Runner 无状态，扩缩容不影响控制面与持久化层。

### 3.7 业务应用部署（独立）

业务应用（启动 Workflow 的主应用）独立部署，通过 gRPC/REST 访问服务器 B。

```yaml
version: "3.8"
services:
  business-app:
    image: your-registry/business-app:latest
    ports:
      - "8090:8090"
    environment:
      # 访问服务器 B 的 Temporal Server 启动工作流
      - TEMPORAL_SERVER_ADDRESS=server-b.example.com:7233
      # 访问 BFF 查询/控制工作流
      - UI_BFF_ADDRESS=server-b.example.com:8080
    restart: unless-stopped
```

> 业务应用可独立成服务器 E，或与现有业务集群合并部署，按业务规模灵活调整。

### 3.8 网络连通性要求

| 链路 | 方向 | 协议/端口 | 延迟要求 | 说明 |
| --- | --- | --- | --- | --- |
| 用户 → 服务器 A | 入站 | HTTPS 443 | 公网 | Nginx 终止 SSL |
| 服务器 A → 服务器 B | 内网 | HTTP 8080 / WS | 低延迟 | Nginx 反代至 BFF |
| 服务器 B（BFF）→ 服务器 B（Server） | localhost | gRPC 7233 | < 1ms | 同机回环 |
| 服务器 B（Server）→ 服务器 C（DB） | 内网 | TCP 5432 | **< 1ms（关键）** | 必须同可用区 |
| 服务器 D/E/N → 服务器 B | 内网/跨机房 | gRPC 7233 | 可容忍 | 长轮询机制 |
| 业务应用 → 服务器 B | 内网 | gRPC 7233 / HTTP 8080 | 低延迟 | 启动/查询工作流 |

---

## 四、风险与应对

| 风险 | 应对策略 |
| --- | --- |
| **B↔C 跨可用区导致 DB 延迟升高** | 强制服务器 B 与 C 同可用区部署；上线前用 `pgbench`/`ping` 验证 RTT < 1ms；监控 DB 查询 P99 延迟 |
| **数据库性能瓶颈** | 服务器 C 使用 SSD + 充足连接池；监控慢查询与 IOPS；必要时纵向扩容或将 DB 迁至托管 RDS（仍与 B 同可用区） |
| **服务器 B 资源争抢（Server vs BFF）** | 用 Docker `resource limits` 为 Temporal Server 与 BFF 分别设置 CPU/内存上限（示例已配置），互不挤占 |
| **Nginx 集群到 BFF 跨网络** | 服务器 A 与 B 间走内网/专线，确保 REST/WS 代理稳定；WS 长连接需设置 `proxy_read_timeout` |
| **Worker 扩容导致 Server 负载升高** | Worker 大量汇报 Heartbeat 会冲击 Server→DB 链路；合理设置 Heartbeat 频率，避免过密 |
| **跨网络 Worker 通信** | Runner 跨机房访问 Server 时，确保 gRPC 连接稳定；长轮询机制本身可容忍网络波动，但需监控连接重连指标 |
| **自研 UI 与官方功能脱节** | Temporal Server 版本升级时需同步核对 gRPC API 变更；建议锁定 SDK 版本，建立回归测试 |
| **加密 Payload 解密** | BFF 需部署 Codec Server 或集成解密逻辑，注意密钥管理（KMS）与权限隔离 |

---

## 五、总结

### 部署架构总览

| 服务器 | 部署组件 | 角色 |
| --- | --- | --- |
| 服务器 A | Nginx + UI 前端 | 接入层 |
| 服务器 B | Temporal Server + BFF | 核心控制面 |
| 服务器 C | Temporal 数据库 | 持久化层 |
| 服务器 D/E/N | Runner 集群 | 数据面 |

### 核心原则

1. **Temporal Server 与数据库必须同可用区（低延迟内网）**——这是性能的生命线。本方案将 DB 独立为服务器 C，但与 Server（服务器 B）部署于同一可用区，既满足低延迟要求，又获得资源隔离与独立运维的优势。
2. **BFF 与 Temporal Server 同机**——gRPC 走 localhost，延迟最低。
3. **Runner（Worker）独立部署**——它是无状态的数据面，享受横向扩容是 Temporal 的核心价值；与 Server/DB 同机会资源争抢、限制扩容、故障耦合。
4. **UI 前端与 Nginx 同机**——静态资源与反向代理合并部署，便于 CDN 加速、多副本高可用，与后端解耦发布。
5. **数据库独立服务器**——专属 I/O 与 CPU，便于独立调优、备份、垂直扩容，故障域隔离。
6. **自研 UI 采用 BFF 模式**——便于鉴权、数据脱敏与业务整合，避免前端直连 gRPC。

按此设计落地，可在保证性能与稳定性的前提下，获得贴合业务场景的定制化 Temporal 管控能力。
