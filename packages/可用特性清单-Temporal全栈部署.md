# Temporal 全栈部署 - 可用特性清单

> **部署日期**：2026-08-19
> **部署环境**：macOS (darwin arm64)
> **Temporal Server**：v1.31.2（原生二进制部署）
> **数据库**：PostgreSQL 15（Docker 部署）
> **UI Server**：temporalio/ui:2.32.0（Docker 部署）
> **前端**：ui-redesign（React + TypeScript + Vite）
> **Worker**：demo-worker（Spring Boot 2.7.18 + Temporal SDK 1.24.0）

---

## 目录

- [一、部署架构总览](#一部署架构总览)
- [二、服务清单与端口映射](#二服务清单与端口映射)
- [三、Temporal Server 可用特性](#三temporal-server-可用特性)
- [四、二次开发 UI (ui-redesign) 可用特性](#四二次开发-ui-ui-redesign-可用特性)
- [五、Demo Worker 可用特性](#五demo-worker-可用特性)
- [六、REST API 端点清单](#六rest-api-端点清单)
- [七、快速验证指南](#七快速验证指南)
- [八、部署处理报告](#八部署处理报告)

---

## 一、部署架构总览

```
┌──────────────────────────────────────────────────────────────────┐
│                         macOS 宿主机                               │
│                                                                  │
│  浏览器 ────────────► Vite Dev Server (:5173)                     │
│                         │  代理 /api/v1 →                        │
│                         ▼                                        │
│               Temporal UI Server (:8280, Docker)                  │
│                         │  gRPC →                                │
│                         ▼                                        │
│               Temporal Server v1.31.2 (:7233, 原生二进制)          │
│                   │  TCP 127.0.0.1:5432                          │
│                   ▼                                              │
│               Docker: PostgreSQL 15 (:5432)                       │
│                 ├─ temporal 库（工作流状态持久化）                   │
│                 └─ temporal_visibility 库（工作流搜索/可见性）       │
│                                                                  │
│  Demo Worker (:18093, Spring Boot)                               │
│    ├─ 连接 Temporal Server (127.0.0.1:7233)                      │
│    ├─ 注册 DEMO_TASK_QUEUE                                        │
│    └─ 调用 Mokapi (:19090) 模拟第三方接口                           │
└──────────────────────────────────────────────────────────────────┘
```

### 部署方式说明

| 组件 | 部署方式 | 说明 |
|------|---------|------|
| PostgreSQL 15 | Docker 容器 | `postgres:15-alpine`，端口映射 5432:5432 |
| Temporal Server v1.31.2 | 原生二进制 | `temporal_1.31.2_darwin_arm64.tar.gz`，直接运行 |
| Temporal UI Server | Docker 容器 | `temporalio/ui:2.32.0`，端口映射 8280:8080 |
| ui-redesign 前端 | Vite Dev Server | React + TypeScript，开发模式 |
| Demo Worker | Maven spring-boot:run | Spring Boot 2.7.18 + Temporal SDK 1.24.0 |
| Mokapi | 已有进程 | 模拟第三方海康校验接口 |

---

## 二、服务清单与端口映射

| # | 服务名称 | 端口 | 部署方式 | 进程/容器 | 状态 |
|---|---------|------|---------|-----------|------|
| 1 | PostgreSQL 15 | 5432 | Docker | temporal-postgresql | healthy |
| 2 | Temporal Server v1.31.2 | 7233 (gRPC) | 原生二进制 | temporal-server (PID 21791) | running |
| 3 | Temporal UI Server | 8280 (HTTP) | Docker | temporal-ui-server | Up |
| 4 | ui-redesign 前端 | 5173 (HTTP) | Vite Dev Server | node (PID 22008) | running |
| 5 | Demo Worker | 18093 (HTTP) | Spring Boot | java (PID 22088) | running |
| 6 | Mokapi | 19090 (HTTP) | 已有进程 | java (PID 45564) | running |

### 端口连接关系

| 组件 | 监听端口 | 连接目标 | 连接地址 |
|------|---------|----------|----------|
| Temporal Server | 7233 (gRPC) | Docker PG | 127.0.0.1:5432 |
| Temporal UI Server | 8280 (HTTP) | Temporal Server | host.docker.internal:7233 |
| ui-redesign | 5173 (HTTP) | Temporal UI Server | localhost:8280 (Vite proxy) |
| Demo Worker | 18093 (HTTP) | Temporal Server | 127.0.0.1:7233 |
| Demo Worker | - | Mokapi | localhost:19090 |
| 浏览器 | - | ui-redesign | localhost:5173 |

---

## 三、Temporal Server 可用特性

### 3.1 核心服务

| 特性 | 说明 |
|------|------|
| Frontend gRPC 服务 | 端口 7233，Worker/UI Server 通过此端口连接 |
| History 服务 | 端口 7234，工作流历史事件持久化 |
| Matching 服务 | 端口 7235，任务队列匹配调度 |
| Worker 服务 | 端口 7239，内部系统工作流执行 |
| HTTP API | 端口 7243，Frontend HTTP 接口 |

### 3.2 持久化

| 特性 | 说明 |
|------|------|
| 主存储 (temporal 库) | PostgreSQL 15，schema v1.19，存储工作流执行状态 |
| 可见性存储 (temporal_visibility 库) | PostgreSQL 15，schema v1.14，支持工作流搜索/查询 |
| History Shards | 4 个分片（开发环境配置） |

### 3.3 动态配置

| 配置项 | 说明 |
|------|------|
| 动态配置文件 | `config/dynamicconfig/development-sql.yaml` |
| 轮询间隔 | 10s |
| Worker 心跳 | 已启用 |

### 3.4 集群信息

| 属性 | 值 |
|------|-----|
| Cluster Name | active |
| Server Version | 1.31.2 |
| Persistence Store | postgres12 |
| Visibility Store | postgres12 |
| History Shard Count | 4 |

---

## 四、二次开发 UI (ui-redesign) 可用特性

### 4.1 全局总览页面 (`/`)

| 特性 | 说明 |
|------|------|
| 统计卡片 | 显示运行中、已完成、失败、超时等工作流数量 |
| 24小时成功率趋势图 | 基于 Recharts 的 AreaChart，按小时展示工作流成功率变化 |
| 最近工作流列表 | 展示最近启动的 5 个工作流，可点击跳转详情 |
| 实时时钟 | 右上角显示当前时间，每秒刷新 |
| 手动刷新 | 右上角刷新按钮，重新拉取所有数据 |
| 错误提示 | API 请求失败时显示错误横幅 |

### 4.2 工作流列表页面 (`/workflows`)

| 特性 | 说明 |
|------|------|
| 状态筛选 | 全部 / 运行中 / 已完成 / 失败 / 超时 / 已取消 |
| 搜索功能 | 按工作流 ID 或名称搜索 |
| 启动工作流 | 点击"启动工作流"按钮，填写表单启动新工作流 |
| 操作菜单 | 每行三点菜单：终止、取消、重置 |
| 批量操作 | 勾选多个工作流后底部出现批量操作栏（批量终止/取消/重置） |
| 表格列 | 工作流名称、ID、状态、类型、任务队列、开始时间、持续时间 |
| 分页 | 返回最多 100 条工作流记录 |
| 自动刷新 | 支持手动刷新按钮 |

### 4.3 工作流详情页面 (`/workflows/:id`)

| 特性 | 说明 |
|------|------|
| 基本信息卡片 | 工作流 ID、运行 ID、工作流类型、任务队列、开始/结束时间 |
| 执行统计卡片 | 持续时间、活动总数、第三方调用次数、重试次数 |
| 搜索属性卡片 | 展示工作流的搜索属性键值对 |
| 执行时间线 | 以时间线形式可视化展示每个 Activity 的执行状态 |
| 活动详情面板 | 点击时间线节点查看详情 |
| - 输入输出标签 | 展示 Activity 的输入数据和输出结果（JSON 格式化） |
| - 第三方交互标签 | 展示 HTTP 方法、端点、状态码、响应时间、请求体、响应体 |
| - 日志标签 | 展示活动执行日志（INFO/WARN/ERROR/HTTP） |
| 操作按钮 | 发送信号 (Signal)、发送更新 (Update)、重置、取消、终止 |
| 底部折叠区 | 工作流输入/结果数据（可展开/折叠） |
| JSON 查看器 | 使用 JsonViewer 组件格式化展示 JSON 数据 |

#### 4.3.1 发送信号 (Signal) Modal

| 字段 | 说明 |
|------|------|
| 信号名称 | 必填，例如 `cancelSync`、`updateConfig` |
| 输入参数 | 可选，JSON 格式，例如 `{"reason": "manual"}` |
| 说明 | 信号立即触发工作流中对应的 signal handler |

#### 4.3.2 发送更新 (Update) Modal

| 字段 | 说明 |
|------|------|
| 更新类型 | 必填，例如 `updatePriority`、`pauseActivity` |
| 输入参数 | 可选，JSON 格式，例如 `{"priority": 5}` |
| 说明 | 更新会等待工作流的 update handler 完成后返回结果 |

### 4.4 工作进程页面 (`/workers`)

| 特性 | 说明 |
|------|------|
| 统计卡片 | 运行中、异常、已排空、排空中的 Worker 数量 |
| Worker 列表 | 名称、状态、任务队列、构建 ID、槽位使用、CPU、内存、轮询器数量、最后心跳 |
| 状态颜色 | running(绿色) / unhealthy(红色) / drained(灰色) / draining(黄色) |
| 槽位使用率 | 进度条展示已用/最大槽位 |
| CPU/内存使用率 | 颜色分级：<50% 绿色、50-80% 黄色、>80% 红色 |
| 详情侧边栏 | 点击"查看详情"打开右侧抽屉，展示完整 Worker 信息 |
| 任务队列监控 | 监控 DEMO_TASK_QUEUE、HIK_SYNC_TASK_QUEUE、order-task-queue |

### 4.5 计划任务页面 (`/schedules`)

| 特性 | 说明 |
|------|------|
| 统计卡片 | 活跃计划数、已暂停数、总执行次数 |
| 计划列表 | 名称、状态、工作流类型、任务队列、Cron 表达式、上次/下次执行、执行次数、失败次数 |
| Cron 中文描述 | 自动将 Cron 表达式转换为中文可读格式（如"每天 09:00"） |
| 暂停/恢复 | 一键切换计划状态 |
| 创建计划 | 点击"创建计划"按钮，填写表单创建 Cron 定时调度 |
| Cron 预设 | 提供常用 Cron 预设（每天 09:00、每小时、每周一 09:00 等） |
| 随机 ID 生成 | 支持随机生成计划 ID |

### 4.6 启动工作流 Modal

| 字段 | 说明 |
|------|------|
| 工作流 ID | 可手动输入或随机生成 UUID |
| 工作流类型 | DemoWorkflow / HikSyncWorkflow |
| 任务队列 | DEMO_TASK_QUEUE / HIK_SYNC_TASK_QUEUE |
| 输入参数 | JSON 格式，带校验 |
| 搜索属性（高级） | 可添加多组键值对 |
| 启动延迟（高级） | 支持秒/分/时延迟启动 |
| 用户备注（高级） | 标识此工作流运行的备注信息 |
| JSON 校验 | 实时校验输入参数 JSON 格式 |
| 成功反馈 | 启动成功后展示 RunID |

### 4.7 全局功能

| 特性 | 说明 |
|------|------|
| 侧边栏导航 | 全局总览、工作流列表、工作进程、计划任务、使用说明 |
| 主题切换 | 深色主题 / 浅色主题 / 跟随系统 |
| 编解码服务器配置 | 可配置 Codec Server 地址，用于解码加密 Payload |
| 命名空间显示 | 侧边栏底部显示当前命名空间 (default) |
| CSRF Token 处理 | 自动从 Cookie 读取 CSRF Token 并附加到 POST/PUT/DELETE 请求 |
| PascalCase 转换 | 自动将 API 返回的 PascalCase 键名转换为 camelCase |
| 运行时配置 | 支持 `window.__APP_CONFIG__` 运行时覆盖配置 |
| Mock API | 支持 `VITE_USE_MOCK=true` 启用 Mock 模式 |

---

## 五、Demo Worker 可用特性

### 5.1 工作流 (DemoWorkflow)

| 属性 | 值 |
|------|-----|
| 工作流名称 | DemoWorkflow |
| 任务队列 | DEMO_TASK_QUEUE |
| 输入模型 | DemoRequest |
| 输出模型 | DemoResult |
| 查询方法 | getStatus() — 返回当前工作流进度状态 |

#### 工作流执行流程

```
Step 1: 初始化 (Workflow.sleep 1s)
  └─ status = "INITIALIZING"

Step 2: 并行执行 3 个 Activity (Async.function)
  ├─ callMockApi (30s 超时, 3 次重试, 2s 退避, backoff=2.0)
  ├─ processData  (60s 超时, 2 次重试, 2s 退避, backoff=2.0)
  └─ generateReport (30s 超时, 不重试)
  └─ status = "RUNNING_ACTIVITIES"

Step 3: 汇总结果
  └─ status = "BUILDING_RESULT" → "COMPLETED"
```

#### 状态流转

```
INIT → INITIALIZING → RUNNING_ACTIVITIES → BUILDING_RESULT → COMPLETED
```

### 5.2 Activity 详情

#### 5.2.1 callMockApi

| 属性 | 值 |
|------|-----|
| 功能 | 调用 mokapi 模拟海康校验接口 |
| 端点 | POST `{mokapi.url}/api/hikvision/verify` |
| 请求体 | `{ "personId": "...", "name": "..." }` |
| 超时 | 30 秒 |
| 重试 | 3 次，初始退避 2s，backoff 系数 2.0 |
| Heartbeat | 调用开始/完成时上报心跳 |
| 错误处理 | RuntimeException 包装为 Activity.wrap(e) |

#### 5.2.2 processData

| 属性 | 值 |
|------|-----|
| 功能 | 模拟数据处理（带 sleep） |
| 超时 | 60 秒 |
| 重试 | 2 次，初始退避 2s，backoff 系数 2.0 |
| Heartbeat | 每步上报进度（"数据处理进度 1/3"、"2/3"、"3/3"） |
| 返回值 | 处理数量 (int) |
| 实现 | 循环 3 次，每次 Thread.sleep(500ms) + heartbeat |

#### 5.2.3 generateReport

| 属性 | 值 |
|------|-----|
| 功能 | 生成汇总报告 |
| 超时 | 30 秒 |
| 重试 | 不重试 |
| 返回值 | 格式化报告字符串 |
| 报告格式 | `人员[姓名](人员ID)的电话为电话号码, 报告生成成功` |

### 5.3 数据模型

#### DemoRequest (输入)

| 字段 | 类型 | 说明 |
|------|------|------|
| workflowId | String | 工作流 ID |
| personId | String | 人员 ID |
| name | String | 姓名 |
| phone | String | 电话 |

#### DemoResult (输出)

| 字段 | 类型 | 说明 |
|------|------|------|
| success | boolean | 是否成功 |
| message | String | 汇总报告 |
| processedCount | int | 处理数量 |
| apiResponse | String | mokapi 接口响应 |
| duration | long | 执行时长（毫秒） |

### 5.4 Worker 配置

| 配置项 | 值 | 说明 |
|--------|-----|------|
| Temporal 地址 | 127.0.0.1:7233 | gRPC 连接地址 |
| Mokapi 地址 | http://localhost:19090 | 第三方模拟接口 |
| HTTP 端口 | 18093 | Spring Boot Web 端口 |
| 最大并发 Activity | 10 | WorkerOptions.maxConcurrentActivityExecutionSize |
| 连接超时 | 10s | WorkflowServiceStubs 连接超时 |
| SDK 版本 | 1.24.0 | Temporal Java SDK |
| Java 版本 | 17 | Temurin JDK 17 |

---

## 六、REST API 端点清单

### 6.1 集群信息

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/cluster-info` | 获取集群信息（版本、分片数、存储类型） |
| GET | `/api/v1/system-info` | 获取系统信息 |
| GET | `/api/v1/settings` | 获取 UI 设置（命名空间、写操作开关等） |

### 6.2 命名空间

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/namespaces` | 获取命名空间列表 |
| GET | `/api/v1/namespaces/{namespace}` | 获取命名空间详情 |

### 6.3 工作流

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/namespaces/{ns}/workflows` | 获取工作流列表（支持 query 过滤、分页） |
| GET | `/api/v1/namespaces/{ns}/workflows/{wid}` | 获取工作流详情 |
| POST | `/api/v1/namespaces/{ns}/workflows/{wid}` | 启动工作流 |
| POST | `/api/v1/namespaces/{ns}/workflows/{wid}/terminate` | 终止工作流 |
| POST | `/api/v1/namespaces/{ns}/workflows/{wid}/cancel` | 取消工作流 |
| POST | `/api/v1/namespaces/{ns}/workflows/{wid}/signal/{signalName}` | 发送信号 |
| POST | `/api/v1/namespaces/{ns}/workflows/{wid}/update/{updateType}` | 发送更新 |
| POST | `/api/v1/namespaces/{ns}/workflows/{wid}/query/{queryType}` | 查询工作流 |
| GET | `/api/v1/namespaces/{ns}/workflows/{wid}/history` | 获取工作流历史事件 |
| GET | `/api/v1/namespaces/{ns}/workflow-count` | 获取工作流计数 |

### 6.4 任务队列

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/namespaces/{ns}/task-queues/{tq}` | 获取任务队列详情（poller 信息） |
| GET | `/api/v1/namespaces/{ns}/task-queues/{tq}/worker-build-id-compatibility` | 获取构建 ID 兼容性 |

### 6.5 计划任务

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/namespaces/{ns}/schedules` | 获取调度列表 |
| GET | `/api/v1/namespaces/{ns}/schedule-count` | 获取调度计数 |
| POST | `/api/v1/namespaces/{ns}/schedules/{sid}` | 创建调度 |
| POST | `/api/v1/namespaces/{ns}/schedules/{sid}/patch` | 暂停/恢复/删除调度 |

---

## 七、快速验证指南

### 7.1 访问 UI

1. 打开浏览器访问 `http://localhost:5173`
2. 首页显示全局总览，统计卡片应显示 0 工作流
3. 点击左侧导航"工作进程"，应看到 DEMO_TASK_QUEUE 有 1 个运行中的 Worker

### 7.2 启动 Demo 工作流

1. 进入"工作流列表"页面
2. 点击右上角"启动工作流"按钮
3. 填写表单：
   - 工作流类型：DemoWorkflow
   - 任务队列：DEMO_TASK_QUEUE
   - 输入参数：
     ```json
     {
       "workflowId": "demo-001",
       "personId": "P001",
       "name": "张三",
       "phone": "13800138000"
     }
     ```
4. 点击"启动"按钮

### 7.3 查看工作流执行

1. 返回工作流列表，应看到刚启动的工作流
2. 点击工作流名称进入详情页
3. 查看执行时间线，观察 3 个 Activity 的并行执行
4. 点击时间线节点查看 Activity 输入输出
5. 查看状态查询返回的进度信息

### 7.4 测试操作功能

1. 在工作流详情页点击"终止"按钮
2. 在工作流列表中使用"取消"操作
3. 在计划任务页面创建 Cron 定时调度

---

## 八、部署处理报告

### 8.1 遇到的问题及处理

| # | 问题类型 | 问题描述 | 处理方法 | 处理结果 |
|---|---------|---------|---------|---------|
| 1 | 端口冲突 | Homebrew PostgreSQL 18 和 Docker PostgreSQL 同时监听 5432 端口，导致 Temporal Server 连接到错误的数据库实例 | 停止 Homebrew PostgreSQL 服务 (`launchctl unload` + kill) | 解决，连接到 Docker PostgreSQL |
| 2 | Schema 版本不匹配 | Temporal v1.31.2 要求 schema v1.19，实际为 v1.18（Docker auto-setup:1.29.7 创建） | 使用 temporal-sql-tool 执行 update-schema 更新到 v1.19 | 解决，schema 版本对齐 |
| 3 | Visibility Schema 过旧 | temporal_visibility schema 停留在 v1.9 | 使用 temporal-sql-tool 更新到 v1.14 | 解决，visibility schema 已更新 |
| 4 | 无 macOS UI Server 二进制 | ui-server_2.53.3 仅有 Linux amd64 二进制，无法在 macOS 上运行 | 使用 Docker 运行 temporalio/ui:2.32.0，通过 host.docker.internal 连接原生 Temporal Server | 解决，UI Server 正常运行 |
| 5 | 旧进程残留 | 之前会话的 temporal CLI (SQLite) 和 demo-worker 进程仍在运行 | kill 旧进程，释放 7233/18093 端口 | 解决，端口已释放 |
| 6 | 旧 Docker 容器残留 | 旧的 temporal-server 和 temporal-ui-server Docker 容器已停止但未删除 | docker rm -f 删除旧容器 | 解决，旧容器已清理 |

### 8.2 问题类型统计

| 问题类型 | 数量 |
|---------|------|
| 端口冲突 | 1 |
| Schema 版本不匹配 | 2 |
| 平台兼容性 | 1 |
| 进程/容器残留 | 2 |
| **合计** | **6** |

### 8.3 最终部署结果

| 组件 | 部署方式 | 端口 | 状态 | 验证结果 |
|------|---------|------|------|---------|
| PostgreSQL 15 | Docker | 5432 | healthy | temporal + temporal_visibility 库均存在，schema 已更新到最新 |
| Temporal Server v1.31.2 | 原生二进制 (darwin_arm64) | 7233 | running | cluster-info 返回 v1.31.2，persistence=postgres12 |
| Temporal UI Server | Docker (temporalio/ui:2.32.0) | 8280 | Up | REST API 正常响应，CORS 已配置 |
| ui-redesign 前端 | Vite Dev Server | 5173 | running | API 代理正常，页面可访问 |
| Demo Worker | Maven spring-boot:run | 18093 | running | 已连接 Temporal Server，DEMO_TASK_QUEUE poller 活跃 |
| Mokapi | 已有进程 | 19090 | running | 模拟海康校验接口可用 |

### 8.4 部署文件位置

| 文件/目录 | 路径 | 说明 |
|----------|------|------|
| Temporal 二进制 | `setup/temporal-native/temporal-server` | v1.31.2 darwin_arm64 |
| Temporal SQL 工具 | `setup/temporal-native/temporal-sql-tool` | Schema 管理工具 |
| Temporal 配置 | `setup/temporal-native/config/development-postgres12.yaml` | PostgreSQL 静态配置 |
| 动态配置 | `setup/temporal-native/config/dynamicconfig/development-sql.yaml` | 动态配置文件 |
| Schema 文件 | `setup/temporal-native/schema/postgresql/v12/` | PostgreSQL schema SQL |
| UI Server 包 | `setup/temporal-native/ui-server/dist/` | UI 前端资源（已解压） |
| 前端项目 | `ui-redesign/` | React + TypeScript + Vite |
| Worker 项目 | `workers/demo-worker/` | Spring Boot + Temporal SDK |

### 8.5 启动命令备忘

```bash
# 1. 启动 PostgreSQL (Docker)
docker start temporal-postgresql

# 2. 启动 Temporal Server (原生二进制)
cd /Users/apple/Desktop/AdapterService/setup/temporal-native
./temporal-server --env development-postgres12 start

# 3. 启动 Temporal UI Server (Docker)
docker start temporal-ui-server

# 4. 启动 ui-redesign 前端
cd /Users/apple/Desktop/AdapterService/ui-redesign
pnpm dev

# 5. 启动 Demo Worker
cd /Users/apple/Desktop/AdapterService/workers/demo-worker
mvn spring-boot:run -Pdev

# 6. 访问 UI
open http://localhost:5173
```

### 8.6 停止命令备忘

```bash
# 停止 Demo Worker: Ctrl+C (Maven 终端)
# 停止 ui-redesign: Ctrl+C (Vite 终端)
# 停止 Temporal UI Server
docker stop temporal-ui-server
# 停止 Temporal Server: Ctrl+C (temporal-server 终端)
# 停止 PostgreSQL
docker stop temporal-postgresql
```

---

## 附录：技术栈版本清单

| 组件 | 版本 | 说明 |
|------|------|------|
| Temporal Server | 1.31.2 | Go 1.26.4, CGO_ENABLED=0, darwin arm64 |
| Temporal SDK (Java) | 1.24.0 | io.temporal:temporal-sdk |
| Spring Boot | 2.7.18 | Java 17 (Temurin) |
| PostgreSQL | 15-alpine | Docker 容器 |
| Temporal UI Server | 2.32.0 | Docker 镜像 temporalio/ui |
| React | 18.3.1 | 前端框架 |
| TypeScript | 5.8.3 | 类型系统 |
| Vite | 6.4.3 | 构建工具 |
| Tailwind CSS | 3.4.19 | 样式框架 |
| Recharts | 3.10.1 | 图表库 |
| React Router | 7.18.2 | 路由管理 |
| Zustand | 5.0.3 | 状态管理 |
| pnpm | 10.33.2 | 包管理器 |
| Maven | 3.9.14 | Java 构建工具 |
| Node.js | v24.14.0 | JavaScript 运行时 |

---

**文档结束**
