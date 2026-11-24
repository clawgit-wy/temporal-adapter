# Worker 调用演示完整指南

> 本文档详细说明 demo-worker 和 mokapi 项目的环境配置、部署启动、演示场景操作、日志查看和常见问题排查方法，可指导他人完成整个演示过程。

---

## 目录

1. [环境配置分析](#一环境配置分析)
2. [日志显示配置](#二日志显示配置)
3. [Temporal CLI 调用方法](#三temporal-cli-调用方法)
4. [场景演示实现](#四场景演示实现)
5. [项目打包与部署](#五项目打包与部署)
6. [常见问题排查](#六常见问题排查)

---

## 一、环境配置分析

### 1.1 操作系统要求

| 操作系统 | 版本要求 | 说明 |
|---------|---------|------|
| macOS | 12+ (Monterey) | 开发环境推荐 |
| Windows | 10/11 + WSL2 | 需启用 WSL2 运行 Docker |
| Linux (CentOS/Ubuntu) | CentOS 7+ / Ubuntu 20.04+ | 生产部署推荐 |

### 1.2 软件依赖清单

| 软件名称 | 版本要求 | 用途 | 验证命令 |
|---------|---------|------|---------|
| JDK 8 | 1.8.0_301+ | demo-worker 编译运行 | `java -version` |
| JDK 17 | 17.0.8+ | mokapi 编译运行 | `java -version` |
| Maven | 3.8+ | Java 项目构建 | `mvn -version` |
| Docker | 24.0+ | 容器化部署 | `docker --version` |
| Docker Compose | 2.20+ | 多容器编排 | `docker compose version` |
| Temporal CLI | 1.0+ (tctl 或 temporal) | 工作流操作 | `temporal --version` 或 `tctl --version` |

> **注意**：demo-worker 使用 Java 8 + Spring Boot 2.7.18，mokapi 使用 Java 17 + Spring Boot 3.2.5。如果使用 Docker 部署，Docker 内会自动使用对应 JDK，无需本地安装两个版本。

### 1.3 环境变量配置

```bash
# Temporal Server 地址（默认本地）
export TEMPORAL_ADDRESS=127.0.0.1:7233

# mokapi 服务地址（默认本地）
export MOKAPI_URL=http://localhost:19090
```

### 1.4 端口占用情况

| 服务 | 端口 | 用途 | 冲突检查命令 |
|------|------|------|-------------|
| Temporal Server (gRPC) | 7233 | Worker 连接 Temporal | `lsof -i :7233` |
| Temporal UI Server | 8280 | Web 管理界面 | `lsof -i :8280` |
| PostgreSQL | 5432 | Temporal 持久化存储 | `lsof -i :5432` |
| mokapi | 19090 | 模拟海康接口 | `lsof -i :19090` |
| demo-worker | 18093 | Worker + REST API | `lsof -i :18093` |

### 1.5 资源需求

| 资源 | 最低要求 | 推荐配置 |
|------|---------|---------|
| CPU | 2 核 | 4 核+ |
| 内存 | 4 GB | 8 GB+ |
| 磁盘 | 10 GB | 20 GB+ |
| 网络 | 本地回环 | 本地回环即可 |

### 1.6 网络访问权限

- **Docker 网络**：docker-compose 创建 `demo-network` 桥接网络，容器间通过服务名通信
- **外部访问**：仅需访问 `localhost:8280`（Temporal UI）和 `localhost:18093`（demo-worker API）
- **无需外网**：全部服务本地运行，不依赖外部 API

### 1.7 配置验证步骤

```bash
# 1. 验证 Docker
docker --version && docker compose version

# 2. 验证端口未被占用
lsof -i :7233 -i :8280 -i :19090 -i :18093 -i :5432

# 3. 验证 Docker 网络
docker network ls | grep demo-network

# 4. 验证 Temporal 连通性（启动后）
temporal operator namespace describe default
```

---

## 二、日志显示配置

### 2.1 mokapi 日志支持

**当前状态**：mokapi 已支持 worker 调用日志显示。新增了 `RequestLoggingFilter` 过滤器，自动记录所有进入 mokapi 的 HTTP 请求和响应。

### 2.2 日志启用方法

日志默认已启用，无需额外配置。日志级别通过 `application.yml` 控制：

```yaml
logging:
  level:
    com.hik.mokapi: DEBUG                        # 业务日志
    com.hik.mokapi.config.RequestLoggingFilter: DEBUG  # Worker调用日志
  file:
    path: logs                                    # 日志文件输出路径
```

### 2.3 日志存储路径

| 日志类型 | 文件路径 | 内容 |
|---------|---------|------|
| Worker 调用日志 | `logs/mokapi-worker-calls.log` | 所有 HTTP 请求/响应记录 |
| 通用应用日志 | `logs/mokapi.log` | 应用运行日志 |

> 日志文件自动轮转：单文件最大 50MB，保留 7 天，总上限 500MB。

### 2.4 日志级别设置

| 级别 | 适用场景 | 配置方式 |
|------|---------|---------|
| DEBUG | 开发调试 | `logging.level.com.hik.mokapi: DEBUG` |
| INFO | 正常运行 | `logging.level.com.hik.mokapi: INFO` |
| WARN | 生产环境 | `logging.level.com.hik.mokapi: WARN` |

### 2.5 日志内容格式解析

**Worker 调用日志格式**：
```
[Worker调用] {METHOD} {URI} | 请求体: {BODY} | 状态码: {STATUS} | 耗时: {DURATION}ms
[Worker调用-响应] {METHOD} {URI} | 响应: {RESPONSE}
[Worker调用-异常响应] {METHOD} {URI} | 响应: {ERROR_RESPONSE}
```

**示例日志**：
```
2024-01-15 10:30:00.123 [http-nio-19090-exec-1] INFO  c.h.m.config.RequestLoggingFilter - [Worker调用] POST /api/hikvision/verify | 请求体: {"personId":"NORMAL-001","name":"测试用户"} | 状态码: 200 | 耗时: 52ms
2024-01-15 10:30:00.124 [http-nio-19090-exec-1] INFO  c.h.m.config.RequestLoggingFilter - [Worker调用-响应] POST /api/hikvision/verify | 响应: {"personId":"NORMAL-001","passed":true,"similarity":87.3}
```

**关键信息说明**：
- `请求体`：Worker 发送给 mokapi 的 JSON 请求内容
- `状态码`：HTTP 响应码（200=成功，500=接口异常）
- `耗时`：请求处理时间（毫秒），可用于性能分析
- `响应`：mokapi 返回给 Worker 的 JSON 响应

### 2.6 demo-worker 日志配置

demo-worker 也配置了多级日志输出：

| 日志文件 | 内容 |
|---------|------|
| `logs/demo-worker-worker.log` | Worker 配置和 Controller 调用日志 |
| `logs/demo-worker-activity.log` | Activity 执行日志（含 mokapi 调用细节） |
| `logs/demo-worker.log` | 通用应用日志 |

---

## 三、Temporal CLI 调用方法

### 3.1 Temporal CLI 安装

```bash
# 方式一：通过 Homebrew（macOS）
brew install temporal

# 方式二：直接下载二进制
# https://github.com/temporalio/cli/releases
```

### 3.2 工作流启动命令

#### 基本启动命令

```bash
# 通过 Temporal CLI 启动 Demo Workflow
temporal workflow start \
  --workflow-id "demo-cli-001" \
  --task-queue "DEMO_TASK_QUEUE" \
  --type "DemoWorkflow" \
  --input '{"workflowId":"demo-cli-001","personId":"CLI-001","name":"CLI用户","phone":"13800138000"}'
```

#### 参数配置说明

| 参数 | 说明 | 示例值 |
|------|------|--------|
| `--workflow-id` | 工作流唯一标识（自定义） | `demo-cli-001` |
| `--task-queue` | 任务队列（必须为 `DEMO_TASK_QUEUE`） | `DEMO_TASK_QUEUE` |
| `--type` | 工作流类型名 | `DemoWorkflow` |
| `--input` | 输入参数（JSON 格式） | `{"personId":"001",...}` |

#### 输入参数 JSON 格式

```json
{
  "workflowId": "demo-cli-001",
  "personId": "PERSON-001",
  "name": "张三",
  "phone": "13800138000"
}
```

#### personId 关键字说明（控制 mokapi 响应行为）

| personId 包含关键字 | mokapi 响应行为 | 演示场景 |
|---------------------|----------------|---------|
| `NORMAL` / `LOG` / `CONCURRENT` | 正常返回（80%通过率） | 正常调用 |
| `FAIL` | 返回校验失败（HTTP 200, passed=false） | 业务失败 |
| `ERROR` | 返回 HTTP 500 异常 | 错误重试 |
| `SLOW` | 延迟 3 秒返回 | 慢响应 |
| `TIMEOUT` | 延迟 30 秒返回 | 超时处理 |

### 3.3 任务队列指定方式

```bash
# 查看已注册的任务队列
temporal task-queue describe --task-queue "DEMO_TASK_QUEUE"

# 查看任务队列的 Worker 信息
temporal task-queue describe-poller-info --task-queue "DEMO_TASK_QUEUE"
```

### 3.4 工作流 ID 生成规则

| 命名规则 | 格式 | 用途 |
|---------|------|------|
| 日志场景 | `demo-logging-{timestamp}` | 日志追踪演示 |
| 正常调用 | `demo-normal-{timestamp}` | 正常接口调用 |
| 并发调用 | `demo-concurrent-{timestamp}-{index}` | 并发场景 |
| 错误处理 | `demo-error-{timestamp}` | 错误处理演示 |
| CLI 调用 | `demo-cli-{序号}` | 命令行手动触发 |

### 3.5 查看工作流执行状态

```bash
# 查看工作流执行状态
temporal workflow describe --workflow-id "demo-cli-001"

# 查看工作流执行历史（关键！可查看中间下发数据）
temporal workflow show --workflow-id "demo-cli-001"

# 查询工作流状态（通过 QueryMethod）
temporal workflow query --workflow-id "demo-cli-001" --query "getStatus"

# 列出所有工作流
temporal workflow list

# 列出指定状态的工作流
temporal workflow list --status RUNNING
temporal workflow list --status COMPLETED
temporal workflow list --status FAILED

# 查看工作流执行结果
temporal workflow show --workflow-id "demo-cli-001" --output json
```

### 3.6 通过 CLI 查看中间下发数据

```bash
# 查看完整事件历史（含 Activity 输入输出）
temporal workflow show --workflow-id "demo-cli-001" --output json | jq '.events[]'

# 查看 Activity 执行详情
temporal workflow show --workflow-id "demo-cli-001" --output json | jq '.events[] | select(.eventType | contains("Activity"))'

# 查看 Activity 输入数据（Worker 下发给 Activity 的参数）
temporal workflow show --workflow-id "demo-cli-001" --output json | jq '.events[] | select(.eventType == "EVENT_TYPE_ACTIVITY_TASK_SCHEDULED") | .activityTaskScheduledEventAttributes.input'

# 查看 Activity 输出数据（Activity 执行结果）
temporal workflow show --workflow-id "demo-cli-001" --output json | jq '.events[] | select(.eventType == "EVENT_TYPE_ACTIVITY_TASK_COMPLETED") | .activityTaskCompletedEventAttributes.result'
```

### 3.7 其他常用 CLI 命令

```bash
# 终止工作流
temporal workflow terminate --workflow-id "demo-cli-001"

# 取消工作流
temporal workflow cancel --workflow-id "demo-cli-001"

# 发送 Signal 到工作流
temporal workflow signal --workflow-id "demo-cli-001" --name "signalName" --input '"signalValue"'

# 查看 Temporal 集群信息
temporal operator cluster describe

# 查看命名空间
temporal operator namespace describe default
```

---

## 四、场景演示实现

### 4.1 演示场景概览

| 场景 | API 端点 | 演示目标 |
|------|---------|---------|
| 日志查看 | `GET /api/demo/scenario-logging` | 追踪 Worker 执行每个关键步骤 |
| 正常第三方调用 | `GET /api/demo/scenario-normal` | 展示正常的第三方接口调用流程 |
| 并发接口调用 | `GET /api/demo/scenario-concurrent` | 多任务并发调用第三方接口 |
| 接口错误处理 | `GET /api/demo/scenario-error` | 模拟第三方接口报错，展示错误处理机制 |

### 4.2 场景一：日志查看场景

**目标**：触发完整 Workflow 执行，通过日志追踪每个关键步骤。

**操作命令**：
```bash
# 触发场景
curl "http://localhost:18093/api/demo/scenario-logging?personId=LOG-001&name=日志演示用户"

# 预期返回
{
  "workflowId": "demo-logging-1705...",
  "success": true,
  "message": "人员[日志演示用户](LOG-001)的电话为13800138000, 报告生成成功",
  "processedCount": 3,
  "apiResponse": "{\"personId\":\"LOG-001\",\"passed\":true,\"similarity\":87.3}",
  "duration": "2103ms"
}
```

**日志查看**：
```bash
# 查看 demo-worker 日志
tail -f logs/demo-worker-worker.log

# 查看 mokapi 接收到的请求
tail -f logs/mokapi-worker-calls.log
```

**关键日志步骤追踪**：
1. `[场景1] 日志查看场景启动` - Controller 层
2. `Demo 工作流启动` - Workflow 层
3. `Step 1: 初始化完成` - Workflow 层
4. `Step 2: 并行执行3个 Activity` - Workflow 层
5. `调用 mokapi 校验接口` - Activity 层
6. `[Worker调用] POST /api/hikvision/verify` - mokapi 层
7. `数据处理开始/完成` - Activity 层
8. `生成汇总报告` - Activity 层
9. `Step 3: 汇总结果` - Workflow 层
10. `[场景1] 日志查看场景完成` - Controller 层

### 4.3 场景二：正常第三方接口调用

**目标**：展示 Worker 通过 mokapi 正常调用第三方接口的完整流程。

**操作命令**：
```bash
curl "http://localhost:18093/api/demo/scenario-normal?personId=NORMAL-001&name=正常调用用户"
```

**预期结果**：
```json
{
  "workflowId": "demo-normal-1705...",
  "success": true,
  "message": "人员[正常调用用户](NORMAL-001)的电话为13900139000, 报告生成成功",
  "processedCount": 3,
  "apiResponse": "{\"personId\":\"NORMAL-001\",\"passed\":true,\"similarity\":92.5}",
  "duration": "2087ms"
}
```

**验证步骤**：
1. 在 Temporal UI (`http://localhost:8280`) 查看 Workflow 执行状态
2. 查看 Event History 确认 3 个 Activity 并行执行
3. 在 mokapi 日志中确认收到 `/api/hikvision/verify` 请求
4. 通过 `temporal workflow show --workflow-id <ID>` 查看中间数据

### 4.4 场景三：并发接口调用

**目标**：同时启动多个 Workflow，模拟多任务并发调用第三方接口。

**操作命令**：
```bash
# 并发 5 个任务
curl "http://localhost:18093/api/demo/scenario-concurrent?count=5"

# 并发 10 个任务
curl "http://localhost:18093/api/demo/scenario-concurrent?count=10"
```

**预期结果**：
```json
{
  "batchId": "demo-concurrent-1705...",
  "totalCount": 5,
  "successCount": 5,
  "failCount": 0,
  "results": [
    {"workflowId": "demo-concurrent-1705...-1", "success": true, ...},
    {"workflowId": "demo-concurrent-1705...-2", "success": true, ...},
    ...
  ]
}
```

**验证要点**：
1. Temporal UI 中应看到多个并行运行的 Workflow
2. mokapi 日志中应看到并发的 `/api/hikvision/verify` 请求
3. Worker 的 `maxConcurrentActivityExecutionSize=10` 限制了并发数
4. 所有 Workflow 应在合理时间内完成

### 4.5 场景四：接口错误处理

**目标**：模拟第三方接口报错，展示 Worker 的错误处理机制和恢复策略。

**操作命令**：
```bash
# 触发 ERROR 场景（接口返回 500，触发 Temporal 自动重试）
curl "http://localhost:18093/api/demo/scenario-error?errorType=ERROR"

# 触发 FAIL 场景（业务失败，非异常）
curl "http://localhost:18093/api/demo/scenario-error?errorType=FAIL"

# 触发 SLOW 场景（慢响应，3秒延迟）
curl "http://localhost:18093/api/demo/scenario-error?errorType=SLOW"

# 触发 TIMEOUT 场景（超时，30秒延迟）
curl "http://localhost:18093/api/demo/scenario-error?errorType=TIMEOUT"
```

**错误处理机制说明**：

| 错误类型 | mokapi 行为 | Worker 重试策略 | 预期结果 |
|---------|-------------|----------------|---------|
| ERROR | HTTP 500 | 3次重试，2s/4s退避 | 重试耗尽后 Workflow 失败 |
| FAIL | HTTP 200, passed=false | 不重试（业务成功） | Workflow 正常完成 |
| SLOW | 延迟3秒返回 | 不触发重试 | Workflow 正常完成（稍慢） |
| TIMEOUT | 延迟30秒返回 | 30秒超时后重试 | 可能触发超时重试 |

**ERROR 场景预期结果**：
```json
{
  "workflowId": "demo-error-1705...",
  "errorType": "ERROR",
  "success": false,
  "error": "...",
  "message": "Workflow 在重试 3 次后仍然失败（符合预期）"
}
```

**验证要点**：
1. Temporal UI 中查看 Event History，应看到多次 Activity 失败和重试记录
2. 重试间隔符合指数退避策略（2s -> 4s -> 8s）
3. mokapi 日志中应看到多次 `/api/hikvision/verify` 请求（每次重试都调用）

### 4.6 通过 REST API 查询工作流状态

```bash
# 查询指定工作流的状态
curl "http://localhost:18093/api/demo/status/demo-logging-1705..."

# 返回示例
{
  "workflowId": "demo-logging-1705...",
  "status": "COMPLETED"
}
```

**状态值说明**：
| 状态 | 含义 |
|------|------|
| `INIT` | 初始化 |
| `INITIALIZING` | 初始化中 |
| `RUNNING_ACTIVITIES` | 执行 Activity 中 |
| `BUILDING_RESULT` | 构建结果中 |
| `COMPLETED` | 已完成 |

---

## 五、项目打包与部署

### 5.1 全新电脑部署指南

#### 第一步：环境准备

```bash
# 1. 安装 Docker（macOS）
brew install --cask docker
# 启动 Docker Desktop

# 1. 安装 Docker（Linux - CentOS）
sudo yum install -y yum-utils
sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
sudo yum install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin
sudo systemctl start docker && sudo systemctl enable docker

# 2. 验证 Docker
docker --version
docker compose version

# 3. 安装 Temporal CLI（可选，用于命令行操作）
# macOS
brew install temporal
# Linux
curl -sSL https://temporal.download/cli | sh
```

#### 第二步：获取部署包

```bash
# 方式一：使用打包脚本（在项目根目录执行）
cd /Users/apple/Desktop/AdapterService
./build-demo.sh

# 方式二：手动复制
# 将 workers/demo-worker 和 mokapi 目录复制到目标机器
```

#### 第三步：启动服务

```bash
# 1. 进入 demo-worker 目录
cd demo-deploy-package/demo-worker
# 或
cd workers/demo-worker

# 2. 一键启动全部服务（Temporal + mokapi + demo-worker）
docker compose -f docker-compose.demo.yml up -d

# 3. 查看启动状态
docker compose -f docker-compose.demo.yml ps

# 4. 查看各服务日志
docker compose -f docker-compose.demo.yml logs -f temporal
docker compose -f docker-compose.demo.yml logs -f mokapi
docker compose -f docker-compose.demo.yml logs -f demo-worker
```

#### 第四步：验证部署

```bash
# 1. 验证 Temporal Server
curl http://localhost:8280/api/v1/namespaces 2>/dev/null | head -1
# 或在浏览器打开 http://localhost:8280

# 2. 验证 mokapi
curl http://localhost:19090/api/hikvision/health
# 预期: {"status":"UP","service":"mokapi-hikvision","timestamp":...}

# 3. 验证 demo-worker
curl http://localhost:18093/api/demo/health
# 预期: {"status":"UP","service":"demo-worker","timestamp":...}

# 4. 验证 Temporal Worker 已注册
temporal task-queue describe --task-queue "DEMO_TASK_QUEUE"
```

#### 第五步：执行演示

```bash
# 执行日志查看场景
curl "http://localhost:18093/api/demo/scenario-logging"

# 在 Temporal UI 查看执行结果
# 浏览器打开 http://localhost:8280
```

### 5.2 本地开发模式（不用 Docker）

```bash
# 1. 启动 Temporal Server（需要 Docker）
cd /Users/apple/Desktop/AdapterService/docker-config
docker compose -f docker-compose.temporal-stack.yml up -d

# 2. 编译并启动 mokapi（需要 JDK 17）
cd /Users/apple/Desktop/AdapterService/mokapi
mvn clean package -DskipTests
java -jar target/mokapi-1.0.0.jar

# 3. 编译并启动 demo-worker（需要 JDK 8）
cd /Users/apple/Desktop/AdapterService/workers/demo-worker
mvn clean package -DskipTests
java -jar target/demo-worker-1.0.0.jar

# 4. 执行演示
curl "http://localhost:18093/api/demo/scenario-logging"
```

### 5.3 服务启动顺序

```
PostgreSQL → Temporal Server → Temporal UI → mokapi → demo-worker
```

> docker-compose.yml 已配置 `depends_on` 依赖关系，`docker compose up -d` 会自动按正确顺序启动。

### 5.4 配置修改指南

| 配置项 | 文件位置 | 说明 |
|--------|---------|------|
| Temporal 地址 | `application.yml` → `temporal.address` | 修改为实际 Temporal Server 地址 |
| mokapi 地址 | `application.yml` → `mokapi.url` | 修改为实际 mokapi 服务地址 |
| Worker 端口 | `application.yml` → `server.port` | 默认 18093 |
| mokapi 端口 | mokapi `application.yml` → `server.port` | 默认 19090 |
| 日志级别 | `application.yml` → `logging.level.*` | 调整日志输出级别 |
| 并发限制 | `TemporalWorkerConfig.java` → `maxConcurrentActivityExecutionSize` | Worker 并发 Activity 数 |

### 5.5 停止与清理

```bash
# 停止全部服务
docker compose -f docker-compose.demo.yml down

# 停止并删除数据卷
docker compose -f docker-compose.demo.yml down -v

# 清理 Docker 镜像
docker image prune -f
```

---

## 六、常见问题排查

### 6.1 Worker 无法连接 Temporal Server

**现象**：demo-worker 启动时报 `Connection refused` 或 `UNAVAILABLE`

**排查步骤**：
```bash
# 1. 检查 Temporal Server 是否运行
docker ps | grep temporal

# 2. 检查端口连通性
nc -zv localhost 7233

# 3. 检查 Worker 日志
docker logs demo-worker | head -20
```

**解决方案**：
- 确认 Temporal Server 已启动：`docker compose -f docker-compose.demo.yml ps temporal`
- 检查 `TEMPORAL_ADDRESS` 环境变量是否正确
- Docker 模式下使用容器名 `temporal:7233`，本地模式使用 `127.0.0.1:7233`

### 6.2 mokapi 接口调用失败

**现象**：Worker 日志显示 `调用 mokapi 校验接口失败`

**排查步骤**：
```bash
# 1. 检查 mokapi 是否运行
curl http://localhost:19090/api/hikvision/health

# 2. 手动调用 mokapi 接口测试
curl -X POST http://localhost:19090/api/hikvision/verify \
  -H "Content-Type: application/json" \
  -d '{"personId":"TEST-001","name":"测试"}'

# 3. 检查 mokapi 日志
docker logs demo-mokapi | tail -30
```

### 6.3 Workflow 一直处于 RUNNING 状态

**现象**：Workflow 启动后一直不完成

**排查步骤**：
```bash
# 1. 查看 Workflow 状态
temporal workflow describe --workflow-id <WORKFLOW_ID>

# 2. 查看 Event History
temporal workflow show --workflow-id <WORKFLOW_ID>

# 3. 检查 Worker 是否在轮询
temporal task-queue describe --task-queue "DEMO_TASK_QUEUE"
```

**常见原因**：
- Worker 未启动或未正确注册到 `DEMO_TASK_QUEUE`
- Activity 超时设置过短
- mokapi 服务不可用导致 Activity 一直重试

### 6.4 日志文件未生成

**排查步骤**：
```bash
# 1. 检查日志目录是否存在
ls -la logs/

# 2. 检查 logback 配置是否加载
# 启动日志中应看到 "logback-spring.xml" 相关信息

# 3. Docker 模式下检查挂载卷
docker compose -f docker-compose.demo.yml exec demo-worker ls -la /app/logs/
```

### 6.5 Docker 构建失败

```bash
# 清理 Docker 构建缓存
docker builder prune -f

# 重新构建
docker compose -f docker-compose.demo.yml build --no-cache

# 查看构建日志
docker compose -f docker-compose.demo.yml build 2>&1 | tail -50
```

### 6.6 端口被占用

```bash
# 查找占用端口的进程
lsof -i :18093
lsof -i :19090
lsof -i :7233

# 终止占用进程
kill -9 <PID>

# 或修改 docker-compose.demo.yml 中的端口映射
```

### 6.7 personId 关键字不生效

**确认 mokapi 响应规则**：
```bash
# 测试 ERROR 关键字
curl -X POST http://localhost:19090/api/hikvision/verify \
  -H "Content-Type: application/json" \
  -d '{"personId":"ERROR-TEST","name":"测试"}'
# 预期返回 500 错误

# 测试 SLOW 关键字
curl -X POST http://localhost:19090/api/hikvision/verify \
  -H "Content-Type: application/json" \
  -d '{"personId":"SLOW-TEST","name":"测试"}'
# 预期 3 秒后返回
```

---

## 附录

### A. 项目文件清单

```
demo-worker/
├── pom.xml                              # Maven 配置（Java 8, Spring Boot 2.7.18）
├── Dockerfile                           # Docker 构建文件
├── docker-compose.demo.yml              # 一体化 Docker 编排
└── src/main/
    ├── java/com/hik/worker/
    │   ├── DemoWorkerApplication.java   # 启动类
    │   ├── controller/
    │   │   └── DemoController.java      # 演示场景 REST API
    │   ├── config/
    │   │   └── TemporalWorkerConfig.java # Worker 配置
    │   ├── activity/
    │   │   ├── DemoActivity.java        # Activity 接口
    │   │   └── impl/DemoActivityImpl.java # Activity 实现
    │   ├── workflow/
    │   │   ├── DemoWorkflow.java        # Workflow 接口
    │   │   └── impl/DemoWorkflowImpl.java # Workflow 实现
    │   └── model/
    │       ├── DemoRequest.java         # 请求模型
    │       └── DemoResult.java          # 结果模型
    └── resources/
        ├── application.yml              # 主配置
        ├── application-dev.yml          # 开发环境
        ├── application-prod.yml         # 生产环境
        └── logback-spring.xml           # 日志配置

mokapi/
├── pom.xml                              # Maven 配置（Java 17, Spring Boot 3.2.5）
├── Dockerfile                           # Docker 构建文件
└── src/main/
    ├── java/com/hik/mokapi/
    │   ├── MokApiApplication.java       # 启动类
    │   ├── config/
    │   │   ├── MokApiProperties.java    # 配置属性
    │   │   ├── GlobalExceptionHandler.java # 全局异常处理
    │   │   └── RequestLoggingFilter.java # 请求日志过滤器
    │   ├── controller/
    │   │   ├── MockHikvisionController.java # 模拟海康接口
    │   │   └── WebhookController.java   # Webhook 回调
    │   └── model/
    │       ├── PersonInfo.java          # 人员信息模型
    │       └── VerifyResponse.java      # 校验响应模型
    └── resources/
        ├── application.yml              # 配置文件
        └── logback-spring.xml           # 日志配置
```

### B. 快速验证检查清单

- [ ] Docker 和 Docker Compose 已安装
- [ ] 端口 7233、8280、19090、18093 未被占用
- [ ] `docker compose -f docker-compose.demo.yml up -d` 成功启动
- [ ] `curl http://localhost:8280` 能访问 Temporal UI
- [ ] `curl http://localhost:19090/api/hikvision/health` 返回 UP
- [ ] `curl http://localhost:18093/api/demo/health` 返回 UP
- [ ] `temporal task-queue describe --task-queue "DEMO_TASK_QUEUE"` 显示 Worker 已注册
- [ ] 场景一（日志查看）执行成功
- [ ] 场景二（正常调用）执行成功
- [ ] 场景三（并发调用）执行成功
- [ ] 场景四（错误处理）按预期行为执行

### C. 问题处理报告

#### 发现的问题类型与数量

| 问题类型 | 数量 | 具体问题 |
|---------|------|---------|
| 日志格式错误 | 1 | mokapi MockHikvisionController 中 SLF4J 日志占位符 `{:.1f}` 不支持格式化语法 |
| 线程安全问题 | 1 | mokapi WebhookController 中 `receivedCallbacks` 使用非线程安全的 `ArrayList` |
| 缺失功能 | 1 | mokapi 缺少 Worker 调用日志过滤器 |
| 缺失组件 | 3 | demo-worker 缺少 Dockerfile、docker-compose、REST Controller |
| 缺失配置 | 2 | 两个项目均缺少 logback-spring.xml 日志配置文件 |
| 缺失文档 | 1 | 缺少完整的调用说明文档 |

#### 修改方法与处理结果

| 问题 | 修改方法 | 处理结果 |
|------|---------|---------|
| SLF4J 日志格式错误 | 将 `{:.1f}` 替换为 `String.format("%.1f", value)` | 已修复，相似度正确显示为一位小数 |
| WebhookController 线程安全 | 将 `ArrayList` 替换为 `CopyOnWriteArrayList` | 已修复，并发场景下线程安全 |
| 缺少 Worker 调用日志 | 新增 `RequestLoggingFilter` 过滤器 | 已实现，自动记录所有 HTTP 请求/响应 |
| demo-worker 缺少 REST API | 新增 `DemoController` 控制器 | 已实现 4 个演示场景 API 端点 |
| demo-worker 缺少 Dockerfile | 新增多阶段构建 Dockerfile | 已创建，支持 Docker 部署 |
| demo-worker 缺少 docker-compose | 新增 `docker-compose.demo.yml` | 已创建，一体化编排全部服务 |
| 缺少 logback 配置 | 两个项目均新增 `logback-spring.xml` | 已创建，支持多级日志文件输出 |
| 缺少说明文档 | 编写本完整指南文档 | 已完成，覆盖全部 6 项任务 |
