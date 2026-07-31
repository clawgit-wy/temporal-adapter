# Temporal Web UI 二次开发架构设计方案

> 本文档为 Temporal Web UI 二次开发的完整架构设计方案，明确 **Web UI 与 BFF 层为二次开发范围**，**Temporal Server 与 Runner 采用官方服务**，数据库采用 Docker 容器化部署的 PostgreSQL。
>
> **官方参考资源：**
> - Temporal Server 官方仓库：https://github.com/temporalio/temporal
> - Web UI 官方仓库：https://github.com/temporalio/ui
> - BFF（ui-server）官方仓库：https://github.com/temporalio/ui-server
>
> 本文档专注于架构设计与规划，不包含具体代码实现，重点阐述设计思路、技术选型依据及各组件间的交互关系。

---

## 一、整体架构设计

### 1.1 二次开发范围与边界

本方案的核心原则是：**控制面（Temporal Server）与数据面（Runner/Worker）使用官方服务不做改动，仅对展示层（Web UI）与接入层（BFF）进行二次开发。**

| 组件 | 来源 | 是否二次开发 | 说明 |
| --- | --- | --- | --- |
| **Web UI（前端）** | 自研 | ✅ 二次开发 | 基于 Vue3 自研前端，贴合业务管理后台场景 |
| **BFF（后端接入层）** | 自研 | ✅ 二次开发 | 基于 Java Spring Boot + Temporal Java SDK，负责鉴权、数据聚合、脱敏 |
| **Temporal Server** | 官方 | ❌ 使用官方服务 | 使用官方 Docker 镜像 `temporalio/auto-setup`，不修改源码 |
| **Runner（Worker）** | 官方 SDK | ❌ 使用官方 SDK | Worker 进程使用官方 Temporal SDK 开发业务 Activity/Workflow，连接官方 Server |
| **PostgreSQL** | 官方镜像 | ❌ 使用官方镜像 | 使用官方 `postgres` Docker 镜像，容器化部署 |

**边界划分原则：**

- 自研 BFF 通过 Temporal Java SDK 的 gRPC 接口与官方 Temporal Server 通信，**不修改 Server 任何源码**。
- 自研 Web UI 通过 REST/WebSocket 与自研 BFF 通信，**不直连 Temporal gRPC**。
- Worker 使用官方 SDK 注册 Activity 与 Workflow，业务逻辑由业务团队编写，但**调度与重试机制完全依赖 Temporal Server 官方实现**。

### 1.2 与官方架构的关系

官方 Temporal UI 生态由两部分组成：

| 官方组件 | 仓库 | 技术栈 | 职责 |
| --- | --- | --- | --- |
| **temporalio/ui** | https://github.com/temporalio/ui | Svelte + TypeScript + Vite | 前端 SPA，展示 Workflow 列表、事件历史、Namespace 等 |
| **temporalio/ui-server** | https://github.com/temporalio/ui-server | Go | gRPC-to-HTTP 代理服务器，将 Temporal gRPC API 转换为 HTTP REST 接口供前端调用 |

官方 ui-server 的核心机制是将 gRPC 服务方法映射为 HTTP 端点：`POST /api/v1/{service}/{method}`。例如 `WorkflowService.ListWorkflowExecutions` 映射为 `POST /api/v1/workflow/list-workflow-executions`。

**本方案的替代策略：** 不使用官方 Go ui-server 作为 BFF，而是自研 Java Spring Boot BFF。原因如下：

| 维度 | 官方 ui-server（Go） | 自研 BFF（Java/Spring Boot） |
| --- | --- | --- |
| 鉴权集成 | 需自定义插件适配业务 JWT 体系 | 原生集成 Spring Security + 业务 JWT |
| 数据脱敏 | 仅做 gRPC 透传，无业务过滤 | 可在 Service 层做字段级脱敏与权限过滤 |
| 业务整合 | 独立服务，难以与业务后台统一 | 可整合进 JeecgBoot 管理后台 |
| Codec 解密 | 需额外部署 Codec Server | 可在 BFF 内集成解密逻辑 |
| 团队技术栈 | 需 Go 开发能力 | 复用现有 Java 团队能力 |
| 上游同步 | 可跟踪官方更新 | 需自行关注 gRPC API 变更 |

### 1.3 整体架构图

```
+---------------------------------------------------------------------------+
|                              用户浏览器                                     |
|                         (Vue3 SPA, 自研 Web UI)                            |
+-----------------------------------+---------------------------------------+
                                    | HTTPS (REST + WebSocket)
                                    v
+---------------------------------------------------------------------------+
|  服务器 A (接入层)                                                          |
|  +-------------------+   +-------------------+                            |
|  | Nginx 网关        |   | 自研 Web UI        |                            |
|  | (SSL终止+反代)    |<--| (Vue3 静态资源)    |                            |
|  +--------+----------+   +-------------------+                            |
|           |                                                               |
+-----------|---------------------------------------------------------------+
            | HTTP/WS (跨网络内网)
            v
+---------------------------------------------------------------------------+
|  服务器 B (核心控制面)                                                      |
|  +-------------------+   +-------------------+                            |
|  | 自研 BFF 后端      |   | Temporal Server    |  (官方服务, 不修改)        |
|  | (Java/Spring Boot)|-->| (gRPC :7233)       |                            |
|  | - JWT鉴权/RBAC    |   | localhost 通信      |                            |
|  | - 数据聚合/脱敏    |   +--------+----------+                            |
|  | - Temporal SDK    |            |                                       |
|  | - Codec解密集成   |            | 低延迟内网                             |
|  +-------------------+            |                                       |
+-----------------------------------|---------------------------------------+
                                    v
+---------------------------------------------------------------------------+
|  服务器 C (持久化层, 与 B 同可用区)                                          |
|  +-------------------+                                                     |
|  | PostgreSQL        |  (官方 Docker 镜像, 容器化部署)                      |
|  | (postgres:15)     |                                                     |
|  +-------------------+                                                     |
+---------------------------------------------------------------------------+

                                    + (gRPC 长轮询, 跨网络)
                                    |
+---------------------------------------------------------------------------+
|  服务器 D/E/N (数据面 / Runner 集群, 可水平扩展)                            |
|  +-------------------+   +-------------------+   +-------------------+   |
|  | Worker 实例 1     |   | Worker 实例 2     |   | Worker 实例 N     |   |
|  | (官方 Temporal    |   | (官方 Temporal    |   | (官方 Temporal    |   |
|  |  SDK + 业务代码)  |   |  SDK + 业务代码)  |   |  SDK + 业务代码)  |   |
|  +-------------------+   +-------------------+   +-------------------+   |
+---------------------------------------------------------------------------+
```

### 1.4 数据流向

```
┌─────────┐   REST/WS    ┌─────────┐   gRPC(localhost)   ┌─────────────────┐   SQL(内网)   ┌────────────┐
│ Web UI  │ ──────────> │  BFF    │ ──────────────────> │ Temporal Server │ ───────────> │ PostgreSQL │
│ (自研)  │ <────────── │ (自研)  │ <────────────────── │ (官方)          │ <─────────── │ (官方镜像) │
└─────────┘   JSON/WS   └─────────┘   Protobuf         └─────────────────┘              └────────────┘
                                            ^
                                            | gRPC 长轮询 (跨网络)
                                            |
                                     ┌──────────────┐
                                     │ Runner/Worker│  (官方 SDK + 业务代码)
                                     │ 无状态扩缩容 │
                                     └──────────────┘
```

**关键数据流说明：**

| 数据流 | 协议 | 说明 |
| --- | --- | --- |
| 浏览器 ↔ Nginx | HTTPS | Nginx 终止 SSL，托管前端静态资源 |
| Nginx ↔ BFF | HTTP / WebSocket | 反向代理 API 与 WS 至 BFF |
| BFF ↔ Temporal Server | gRPC (localhost:7233) | BFF 通过 Temporal Java SDK 调用 Server，同机零延迟 |
| Temporal Server ↔ PostgreSQL | SQL (TCP 5432) | Server 持久化事件历史，同可用区内网低延迟 |
| Runner ↔ Temporal Server | gRPC 长轮询 (7233) | Worker 从 Task Queue 拉取任务，跨网络可容忍 |
| 业务应用 ↔ Temporal Server | gRPC (7233) | 业务应用通过 SDK 发起 StartWorkflow |

### 1.5 核心设计决策

| 决策点 | 选择 | 依据 |
| --- | --- | --- |
| BFF 语言 | Java 17 + Spring Boot 3 | 与业务主应用同构，复用鉴权/审计体系；团队具备 Java 能力 |
| 前端框架 | Vue 3 + TypeScript | 与 JeecgBoot 前端栈一致，可整合进现有管理后台 |
| BFF ↔ Server 通信 | Temporal Java SDK (gRPC) | 官方 SDK 封装完善，类型安全，无需手写 gRPC stub |
| Server 部署 | 官方 Docker 镜像 | 不修改源码，通过环境变量与 dynamic config 配置 |
| Worker 开发 | 官方 Temporal SDK | Activity/Workflow 逻辑为业务代码，调度机制依赖官方 |
| 数据库 | PostgreSQL 15 (Docker) | Temporal 官方支持，成熟稳定，容器化便于部署运维 |

---

## 二、技术栈选型

### 2.1 Web UI 前端技术栈

| 层级 | 技术 | 版本 | 选型依据 |
| --- | --- | --- | --- |
| 框架 | Vue 3 (Composition API) | 3.4+ | 与 JeecgBoot 前端栈一致；Composition API 提供更好的逻辑复用与类型推导 |
| 语言 | TypeScript | 5.x | 类型安全，提升可维护性；与官方 UI（TypeScript）生态一致 |
| 构建工具 | Vite | 5.x | 官方 UI 同样使用 Vite；HMR 速度快，生产构建优化好 |
| UI 组件库 | Ant Design Vue / Element Plus | 4.x / 2.x | 企业级组件库，与 JeecgBoot 保持一致 |
| 状态管理 | Pinia | 2.x | Vue3 官方推荐，TypeScript 友好 |
| 路由 | Vue Router | 4.x | Vue3 生态标准路由方案 |
| HTTP 客户端 | Axios | 1.x | 拦截器机制便于统一鉴权与错误处理 |
| 实时通信 | WebSocket (原生) | - | 事件历史流式推送，避免轮询开销 |
| 包管理 | pnpm | 9.x | 官方 UI 使用 pnpm；磁盘效率高，workspace 支持好 |
| 代码规范 | ESLint + Prettier + Stylelint | 9.x / 3.x / 16.x | 官方 UI 同样使用 ESLint + Prettier + Stylelint 组合 |

### 2.2 BFF 后端技术栈

| 层级 | 技术 | 版本 | 选型依据 |
| --- | --- | --- | --- |
| 语言 | Java | 17 (LTS) | 长期支持版本，与 Spring Boot 3 要求一致 |
| 框架 | Spring Boot | 3.2+ | 企业级成熟框架，生态完善 |
| Temporal SDK | temporal-sdk | 1.23+ | 官方 Java SDK，与 Temporal Server 版本对齐 |
| gRPC | temporal-serviceclient | 1.23+ | SDK 内置 gRPC 客户端，无需单独引入 |
| 安全框架 | Spring Security + JJWT | 6.x / 0.12+ | JWT 鉴权，RBAC 权限控制 |
| API 文档 | SpringDoc OpenAPI | 2.x | 自动生成 REST API 文档 |
| 数据转换 | MapStruct | 1.5+ | 编译期生成 DTO 转换代码，性能好 |
| 监控 | Micrometer + Actuator | - | 指标暴露与健康检查 |
| 构建工具 | Maven / Gradle | 3.9+ / 8.x | 与业务主应用保持一致 |
| 代码规范 | Checkstyle + SpotBugs | - | 阿里巴巴 Java 开发规范集成 |

### 2.3 官方服务技术栈（不可改动）

| 组件 | 技术 | 来源 | 说明 |
| --- | --- | --- | --- |
| Temporal Server | Go | temporalio/auto-setup Docker 镜像 | 官方编译产物，通过环境变量配置 |
| Temporal Server 内部架构 | Frontend / History / Matching / Worker Service | 官方实现 | 四个内部 gRPC 服务，不对外暴露 |
| Runner SDK | Temporal Java SDK / Go SDK / TS SDK | 官方 SDK | 业务团队选用对应语言 SDK 开发 Worker |
| PostgreSQL | postgres:15 | 官方 Docker 镜像 | 容器化部署 |

### 2.4 基础设施与工具链

| 类别 | 工具 | 说明 |
| --- | --- | --- |
| 容器运行时 | Docker 24+ / Docker Compose | 服务编排与本地开发 |
| 镜像仓库 | Harbor / 私有 Registry | 自研 BFF 与 Worker 镜像托管 |
| 反向代理 | Nginx 1.25+ | SSL 终止、静态托管、API 反代 |
| CI/CD | GitLab CI / GitHub Actions | 自动化构建、测试、发布 |
| 代码质量 | SonarQube | 代码扫描与质量门禁 |
| 监控 | Prometheus + Grafana | 指标采集与可视化 |
| 日志 | ELK / Loki | 集中式日志收集 |

---

## 三、开发规范

### 3.1 代码规范

#### 3.1.1 前端代码规范

| 规范项 | 标准 | 工具 |
| --- | --- | --- |
| 代码风格 | ESLint + Prettier，与官方 UI 配置对齐 | ESLint 9.x + Prettier 3.x |
| CSS 规范 | BEM 命名 + Tailwind CSS 原子类 | Stylelint |
| 命名规范 | 组件 PascalCase，变量/函数 camelCase，常量 UPPER_SNAKE_CASE | ESLint 规则 |
| 文件命名 | 组件文件 PascalCase（如 `WorkflowList.vue`），工具文件 kebab-case | ESLint 规则 |
| 提交规范 | Conventional Commits（feat / fix / docs / refactor / test / chore） | commitlint + husky |
| 目录结构 | 按功能模块组织，`src/views`（页面）、`src/components`（通用组件）、`src/api`（接口层）、`src/stores`（状态）、`src/utils`（工具） | 约定式 |

#### 3.1.2 后端代码规范

| 规范项 | 标准 | 工具 |
| --- | --- | --- |
| 代码风格 | 阿里巴巴 Java 开发手册 | Checkstyle |
| 静态分析 | 空指针、资源泄漏、安全隐患 | SpotBugs + SonarQube |
| 命名规范 | 类 PascalCase，方法/变量 camelCase，常量 UPPER_SNAKE_CASE，包全小写 | Checkstyle |
| 注释规范 | 公共类/方法必须 Javadoc；复杂逻辑行内注释；变更注释 `@author` / `@date` | Checkstyle |
| 异常处理 | 统一异常处理器，业务异常与系统异常分层；禁止吞异常 | 约定 + SonarQube |
| 目录结构 | Controller → Service → SDK Client 三层；DTO/VO/Entity 分离 | 约定式 |

### 3.2 接口设计标准

#### 3.2.1 RESTful 规范

| 规则 | 说明 |
| --- | --- |
| 路径风格 | `/api/v1/{resource}/{id}/{sub-resource}`，版本号前缀 |
| HTTP 方法 | GET 查询、POST 创建/操作、PUT 更新、DELETE 删除 |
| 响应格式 | 统一 `{ code, message, data }` 信封格式 |
| 分页 | 请求参数 `page` + `pageSize`；响应返回 `items` + `total` + `nextPageToken` |
| 错误码 | 业务错误码 4xxxx，系统错误码 5xxxx，HTTP 状态码语义对应 |
| 命名 | 路径 kebab-case，JSON 字段 camelCase |
| 幂等性 | 控制类操作（Cancel/Terminate）天然幂等；启动 Workflow 使用业务幂等 key |

#### 3.2.2 接口分层

| 层级 | 路径前缀 | 职责 | 示例 |
| --- | --- | --- | --- |
| 查询类 | `/api/v1/workflows` | 工作流列表、详情、历史、Query | `GET /api/v1/workflows` |
| 控制类 | `/api/v1/workflows/{id}/run/{runId}/{action}` | Signal / Cancel / Terminate / Reset | `POST /api/v1/workflows/{id}/run/{runId}/cancel` |
| 业务封装类 | `/api/v1/business/` | 业务侧封装的启动入口、类型清单 | `POST /api/v1/business/workflows/start` |
| Namespace 类 | `/api/v1/namespaces` | Namespace 列表与切换 | `GET /api/v1/namespaces` |
| 系统类 | `/api/v1/system/` | 健康检查、版本信息、搜索属性 | `GET /api/v1/system/health` |

### 3.3 版本控制策略

#### 3.3.1 Git 分支管理（Git Flow 简化版）

| 分支 | 用途 | 命名规则 | 生命周期 |
| --- | --- | --- | --- |
| `main` | 生产发布基线 | 固定 | 永久 |
| `develop` | 集成测试基线 | 固定 | 永久 |
| `feature/*` | 功能开发 | `feature/{JIRA-ID}-brief-desc` | 合并后删除 |
| `fix/*` | 缺陷修复 | `fix/{JIRA-ID}-brief-desc` | 合并后删除 |
| `release/*` | 发布准备 | `release/v{version}` | 发布后删除 |
| `hotfix/*` | 生产紧急修复 | `hotfix/{JIRA-ID}-brief-desc` | 修复后删除 |

**分支流转规则：**

```
main ─────────────────────────●─────────────────────●────────── (生产)
  \                          / ↑                   / ↑
   \                        /  hotfix             /  release
    \                      /                     /
develop ──●──●──●──●──●──●──────────●──────────●────────── (集成)
          ↑   ↑   ↑   ↑
       feature/a  feature/b  feature/c
```

#### 3.3.2 Commit 规范

采用 Conventional Commits 规范：

| 前缀 | 用途 | 示例 |
| --- | --- | --- |
| `feat` | 新功能 | `feat(ui): 新增工作流事件历史时间轴组件` |
| `fix` | 缺陷修复 | `fix(bff): 修复分页 token 解码异常` |
| `docs` | 文档变更 | `docs: 更新部署架构设计文档` |
| `refactor` | 重构 | `refactor(bff): 抽取 Temporal SDK 配置公共类` |
| `test` | 测试相关 | `test(ui): 新增工作流列表页单元测试` |
| `chore` | 构建/工具 | `chore: 升级 Temporal SDK 至 1.23` |

#### 3.3.3 代码审查规范

| 规则 | 说明 |
| --- | --- |
| 审查要求 | 所有 PR 至少 1 人审查通过方可合并 |
| 审查重点 | 逻辑正确性、安全漏洞（SQL 注入/XSS）、性能问题、规范一致性 |
| CI 门禁 | PR 必须通过编译 + 单元测试 + 代码扫描方可合并 |
| 分支保护 | `main` / `develop` 禁止 force push，禁止直接 push |

---

## 四、扩展点设计

### 4.1 Web UI 扩展点

| 扩展区域 | 扩展机制 | 说明 |
| --- | --- | --- |
| **自定义页面** | 路由配置 + 页面组件 | 业务可新增自定义页面（如「订单工作流监控大盘」），挂载到 Vue Router |
| **工作流详情增强** | 事件历史渲染器插件 | 为特定 WorkflowType 注册自定义事件渲染器，将技术事件映射为业务语义 |
| **搜索属性扩展** | 动态列配置 | 根据 Temporal Search Attributes 动态生成列表筛选条件与表格列 |
| **操作面板扩展** | Action 插件注册 | 为特定 WorkflowType 注册自定义 Signal/Query 操作按钮 |
| **主题定制** | CSS 变量 + 主题配置 | 支持深色/浅色主题切换，可对接业务系统主题色 |
| **国际化** | i18n 资源文件 | 支持中英文切换，可扩展其他语言 |
| **Codec 集成** | 前端解码器配置 | 前端可配置 Codec Server 地址，由 BFF 代理调用解密 |

### 4.2 BFF 扩展点

| 扩展区域 | 扩展机制 | 说明 |
| --- | --- | --- |
| **鉴权适配器** | `AuthProvider` 接口 | 可插拔鉴权策略：JWT / OAuth2 / SSO，通过配置切换 |
| **数据脱敏规则** | `DataMaskingStrategy` 接口 | 按 WorkflowType 注册脱敏规则，对 Payload 字段做掩码处理 |
| **Codec 解密** | `PayloadCodec` 接口 | 可集成官方 Codec Server 或内嵌解密逻辑，支持 AES/RSA |
| **业务操作拦截器** | `WorkflowOperationInterceptor` | 在 Signal/Cancel/Terminate 前后插入业务校验与审计逻辑 |
| **Namespace 权限** | `NamespaceAccessFilter` | 按用户角色过滤可访问的 Namespace，注入查询条件 |
| **API 版本管理** | URL 版本前缀 + Header 协商 | 支持 `/api/v1/` 与 `/api/v2/` 并存，平滑升级 |
| **WebHook 通知** | `WorkflowEventListener` | 监听 Workflow 状态变更，触发外部 WebHook 通知 |
| **指标埋点** | Micrometer 自定义指标 | 为 BFF 接口与 Temporal 调用埋点，对接 Prometheus |

### 4.3 扩展实现原则

| 原则 | 说明 |
| --- | --- |
| **开闭原则** | 扩展点通过接口/抽象类定义，新增扩展不修改现有代码 |
| **配置驱动** | 扩展通过配置文件或注解启用，不硬编码 |
| **隔离性** | 各扩展点互不影响，扩展失败不阻断主流程（降级处理） |
| **可观测** | 扩展执行记录日志与指标，便于排查 |

---

## 五、集成方案

### 5.1 与官方 Temporal Server 的集成

| 集成维度 | 方式 | 说明 |
| --- | --- | --- |
| **镜像来源** | `temporalio/auto-setup:1.23` | 官方镜像，包含 auto-setup 自动建库能力 |
| **通信协议** | gRPC (7233 端口) | BFF 通过 Temporal Java SDK 的 `WorkflowServiceStubs` 连接 |
| **连接地址** | `localhost:7233` | BFF 与 Server 同机部署，走 localhost 回环 |
| **配置方式** | 环境变量 + dynamic config | 不修改源码，通过 `POSTGRES_SEEDS`、`DB` 等环境变量配置 |
| **Namespace 管理** | auto-setup 自动创建 / CLI 手动创建 | `temporal operator namespace create default` |
| **版本对齐** | SDK 版本与 Server 版本一致 | Temporal Java SDK 1.23 ↔ Server 1.23 |
| **健康检查** | gRPC Health Checking Protocol | BFF 通过 SDK 探测 Server 健康状态 |

**集成边界：** BFF 仅作为 Temporal Server 的客户端，通过 gRPC 调用 `WorkflowService`、`OperatorService` 等官方 gRPC 服务。**不修改 Server 源码，不注入 Server 进程，不访问 Server 内部服务端口。**

### 5.2 与官方 Runner（Worker）的集成

| 集成维度 | 方式 | 说明 |
| --- | --- |
| **SDK 来源** | 官方 Temporal SDK（Java / Go / TypeScript / Python） | 业务团队按语言选用官方 SDK |
| **连接地址** | `server-b.example.com:7233` | Worker 跨网络连接 Temporal Server |
| **任务队列** | Task Queue 名称约定 | Worker 注册时指定 Task Queue，与 StartWorkflow 时一致 |
| **DataConverter** | 可选加密 Payload | Worker 侧配置 `DataConverter`，BFF 侧配置对应 Codec 解密 |
| **扩缩容** | 无状态水平扩展 | 新增 Worker 实例无需 Server 侧配置，自动接入 |
| **心跳机制** | Activity Heartbeat | 长时间 Activity 定期心跳，避免超时重试 |

**集成边界：** Worker 进程由业务团队开发部署，使用官方 SDK 注册 Activity/Workflow。**调度、重试、超时、定时器等机制完全依赖 Temporal Server 官方实现，不自研调度逻辑。**

### 5.3 与 PostgreSQL 数据库的集成

| 集成维度 | 方式 | 说明 |
| --- | --- |
| **镜像来源** | `postgres:15` | 官方 PostgreSQL Docker 镜像 |
| **部署方式** | Docker 容器化，独立服务器 | 与 Temporal Server 同可用区，内网低延迟 |
| **连接方** | 仅 Temporal Server | **BFF 与 Worker 均不直连数据库**，仅 Server 通过 JDBC 访问 |
| **数据库 Schema** | auto-setup 自动创建 | Temporal Server 启动时自动创建所需表结构 |
| **数据持久化** | Docker Volume 挂载独立数据盘 | `temporal-db-data` 卷映射到宿主机 SSD |
| **连接池配置** | Temporal Server 侧配置 | 通过 Server 环境变量调整 `DB_MAX_CONNS`、`DB_MAX_IDLE_CONNS` |
| **备份策略** | `pg_dump` 逻辑备份 + 物理快照 | 定期备份，验证可恢复性 |

**集成边界：** PostgreSQL 仅被 Temporal Server 访问。**自研 BFF、Web UI、Worker 均不直连数据库**，避免绕过 Server 的数据一致性保障。

### 5.4 DataConverter / Codec 集成链路

```
Worker 侧                          BFF 侧                        前端
┌─────────────────┐               ┌─────────────────┐          ┌─────────┐
│ DataConverter   │   加密Payload  │ PayloadCodec    │  解密JSON │ Web UI  │
│ (加密/序列化)    │ ────────────> │ (解密/反序列化)  │ ────────> │ 展示    │
│ - AES-GCM 加密  │   存入 DB     │ - 调用 Codec     │          │         │
│ - JSON 序列化   │               │   Server 或内嵌  │          │         │
└─────────────────┘               └─────────────────┘          └─────────┘
```

| 环节 | 实现方式 |
| --- | --- |
| Worker 加密 | Worker 侧配置 `DataConverter`，使用 `EncryptionCodec` 对 Payload 加密后发送给 Server |
| BFF 解密 | BFF 侧集成 `PayloadCodec` 接口，调用 Codec Server 或内嵌解密逻辑，对 History 事件中的 Payload 解密 |
| 密钥管理 | 使用 KMS（如阿里云 KMS / AWS KMS）管理加密密钥，BFF 与 Worker 共享密钥访问权限 |

---

## 六、部署架构

### 6.1 四层分离部署拓扑

| 服务器 | 部署组件 | 角色 | 资源建议 |
| --- | --- | --- | --- |
| **服务器 A** | Nginx + 自研 Web UI 静态资源 | 接入层 | 2核4G ×N |
| **服务器 B** | Temporal Server（官方） + 自研 BFF | 核心控制面 | 8核16G+ |
| **服务器 C** | PostgreSQL（官方镜像，Docker） | 持久化层 | 8核16G+ SSD |
| **服务器 D/E/N** | Runner 集群（官方 SDK + 业务代码） | 数据面 | 按峰值弹性 |

### 6.2 Docker 容器化部署方案

#### 6.2.1 服务器 A：Nginx + Web UI

| 配置项 | 说明 |
| --- | --- |
| 基础镜像 | `nginx:1.25-alpine` |
| 前端产物 | Vue3 构建产物挂载至 `/usr/share/nginx/html` |
| SSL | 证书挂载，Nginx 终止 HTTPS |
| 反向代理 | `/api/` → 服务器 B BFF；`/ws/` → 服务器 B BFF（WebSocket） |
| 静态缓存 | JS/CSS/图片缓存 7 天，`Cache-Control: immutable` |
| 高可用 | 2+ 节点，前置 SLB 负载均衡 |

#### 6.2.2 服务器 B：Temporal Server + BFF

| 配置项 | 说明 |
| --- | --- |
| Temporal Server 镜像 | `temporalio/auto-setup:1.23`（官方） |
| BFF 镜像 | 自研 `temporal-ui-bff:latest` |
| Server 数据库连接 | `POSTGRES_SEEDS` 指向服务器 C 内网地址 |
| BFF → Server | `localhost:7233`（同机回环） |
| 资源限制 | Server: 4核8G；BFF: 2核4G（Docker resource limits） |
| 端口暴露 | 7233（gRPC，供 Worker 与 BFF）；8080（BFF HTTP，供 Nginx 反代） |

#### 6.2.3 服务器 C：PostgreSQL

| 配置项 | 说明 |
| --- | --- |
| 基础镜像 | `postgres:15`（官方） |
| 数据持久化 | Docker Volume 映射宿主机独立 SSD 数据盘 |
| 端口 | 5432，仅对服务器 B 内网开放，禁止公网 |
| 调优参数 | `shared_buffers`、`effective_cache_size`、`max_connections` 等 |
| 网络隔离 | 安全组限制来源 IP 为服务器 B |
| 备份 | 定期 `pg_dump` + 物理快照 |

#### 6.2.4 服务器 D/E/N：Runner 集群

| 配置项 | 说明 |
| --- | --- |
| Worker 镜像 | 自研 `temporal-worker:latest`（官方 SDK + 业务代码） |
| 连接地址 | `server-b.example.com:7233`（跨网络） |
| 扩缩容 | Docker 多节点部署 / K8s HPA 按 Task Queue 积压自动扩缩 |
| 资源限制 | 按业务负载配置 CPU/内存上限 |

### 6.3 服务编排

| 编排场景 | 工具 | 说明 |
| --- | --- | --- |
| 单机开发环境 | Docker Compose | 一键启动 DB + Server + BFF + UI |
| 生产环境（非 K8s） | Docker Compose + systemd | 每台服务器独立 Compose 文件，systemd 守护 |
| 生产环境（K8s） | Kubernetes + Helm | 推荐生产环境，支持 HPA 自动扩缩容 |
| 配置管理 | 环境变量 + .env 文件 | 敏感信息（密码、密钥）通过 Secret 管理 |

### 6.4 网络配置

| 网络区域 | 组件 | 端口 | 访问控制 |
| --- | --- | --- | --- |
| 公网入口 | Nginx（服务器 A） | 443/80 | 公网开放，WAF 防护 |
| 内网-接入层到控制面 | Nginx → BFF（A → B） | 8080 | 内网/专线 |
| 内网-控制面内部 | BFF → Server（B 内 localhost） | 7233 | 仅 localhost |
| 内网-控制面到持久化层 | Server → DB（B → C） | 5432 | 同可用区内网，安全组限源 IP |
| 内网-数据面到控制面 | Worker → Server（D/E/N → B） | 7233 | 内网/跨机房 |
| 禁止 | BFF/UI/Worker → DB | 5432 | 数据库仅允许 Server 访问 |

### 6.5 数据持久化策略

| 数据类型 | 存储位置 | 持久化方式 | 备份策略 |
| --- | --- | --- | --- |
| Workflow 事件历史 | PostgreSQL（服务器 C） | Docker Volume → SSD | pg_dump 每日 + 物理快照每小时 |
| Visibility 索引 | PostgreSQL（服务器 C） | 同上 | 同上 |
| BFF 配置 | 服务器 B | 环境变量 / 配置文件 | 纳入 Git 版本控制 |
| 前端静态资源 | 服务器 A | Nginx 容器内 | CI/CD 构建产物，可重建 |
| Worker 状态 | 无 | 无状态 | 不持久化，重启自动恢复 |
| 审计日志 | ELK / 数据库 | 日志卷 + 索引 | 日志保留 90 天 |

---

## 七、开发环境搭建

### 7.1 工具要求

| 工具 | 版本 | 用途 |
| --- | --- | --- |
| Docker Desktop | 24+ | 容器运行时 |
| Docker Compose | 2.x | 本地服务编排 |
| Node.js | 20 LTS | 前端运行时 |
| pnpm | 9.x | 前端包管理 |
| JDK | 17 | BFF 编译运行 |
| Maven / Gradle | 3.9+ / 8.x | BFF 构建 |
| Temporal CLI | latest | 本地 Temporal Server 开发调试 |
| IDE | VS Code（前端）/ IntelliJ IDEA（后端） | 开发工具 |
| Git | 2.x | 版本控制 |

### 7.2 本地开发环境配置步骤

#### 步骤一：启动 Temporal Server + PostgreSQL（官方服务）

使用 Docker Compose 一键启动官方 Temporal Server 与 PostgreSQL，作为本地开发依赖。

| 配置项 | 说明 |
| --- | --- |
| Docker Compose 文件 | 包含 `postgres:15` + `temporalio/auto-setup:1.23` |
| 数据库 | 本地 PostgreSQL 容器，auto-setup 自动建表 |
| Server 端口 | 7233（gRPC）、7233/udp |
| 启动命令 | `docker compose up -d` |
| 验证 | `temporal operator namespace describe default` |

#### 步骤二：启动自研 BFF 后端

| 配置项 | 说明 |
| --- | --- |
| 连接 Server | `temporal.server.address=localhost:7233` |
| Namespace | `default` |
| JWT 密钥 | 本地开发用测试密钥 |
| 启动方式 | IDE 直接运行 Spring Boot 主类 / `mvn spring-boot:run` |
| 验证 | 访问 `http://localhost:8080/api/v1/system/health` |

#### 步骤三：启动自研 Web UI 前端

| 配置项 | 说明 |
| --- | --- |
| API 地址 | `VITE_API=http://localhost:8080`（指向 BFF） |
| 安装依赖 | `pnpm install` |
| 开发服务器 | `pnpm dev`（Vite HMR） |
| 访问地址 | `http://localhost:3000` |

#### 步骤四：启动本地 Worker（可选）

| 配置项 | 说明 |
| --- | --- |
| 连接 Server | `localhost:7233` |
| Task Queue | 与业务约定一致 |
| 启动方式 | IDE 运行 Worker 主类 |
| 用途 | 本地测试 Workflow 执行与事件历史展示 |

### 7.3 开发环境与生产环境差异

| 维度 | 开发环境 | 生产环境 |
| --- | --- | --- |
| Temporal Server | 本地 Docker（单节点） | 官方 Docker 镜像（服务器 B） |
| PostgreSQL | 本地 Docker（单节点） | 独立服务器 C（Docker，SSD） |
| BFF | IDE 直接运行 | Docker 容器（服务器 B） |
| Web UI | Vite 开发服务器（HMR） | Nginx 静态托管（服务器 A） |
| Worker | IDE 直接运行 | Docker 容器集群（服务器 D/E/N） |
| 鉴权 | 测试 JWT / 关闭鉴权 | 业务 JWT + RBAC |
| 数据 | 测试数据 | 生产数据 |
| 日志级别 | DEBUG | INFO/WARN |

### 7.4 调试工具

| 工具 | 用途 |
| --- | --- |
| Temporal CLI | 命令行查看 Workflow、Namespace、Search Attributes |
| Temporal 官方 UI（dev 模式） | 对照查看官方 UI 展示效果，辅助自研 UI 验证 |
| gRPCurl | 直接调用 Temporal gRPC 接口，验证 Server 响应 |
| SpringDoc Swagger UI | BFF 接口文档与在线调试 |
| Vue DevTools | 前端组件状态与路由调试 |

---

## 八、测试策略

### 8.1 测试分层

```
┌─────────────────────────────────────────────────────┐
│                    E2E 测试 (Playwright)             │  ← 用户场景验证
├─────────────────────────────────────────────────────┤
│              集成测试 (Testcontainers + Temporal)     │  ← 组件间交互验证
├─────────────────────────────────────────────────────┤
│         单元测试 (Vitest / JUnit 5)                  │  ← 函数/组件级验证
├─────────────────────────────────────────────────────┤
│              静态分析 (ESLint / Checkstyle / SonarQube)│  ← 代码质量门禁
└─────────────────────────────────────────────────────┘
```

### 8.2 前端测试策略

| 测试类型 | 工具 | 覆盖范围 | 目标覆盖率 |
| --- | --- | --- | --- |
| 单元测试 | Vitest | 工具函数、 composables、store 逻辑 | ≥ 80% |
| 组件测试 | Vitest + Vue Test Utils | 组件渲染、交互事件、props/emit | ≥ 70% |
| 视觉回归 | Storybook + Chromatic | UI 组件视觉一致性 | 关键组件 100% |
| E2E 测试 | Playwright | 关键用户流程（列表→详情→历史→控制） | 核心流程 100% |
| 可访问性测试 | axe-core | WCAG 合规 | 关键页面通过 |

**E2E 测试场景清单：**

| 场景 | 验证点 |
| --- | --- |
| 工作流列表查询 | 分页、筛选、状态过滤正确 |
| 工作流详情查看 | 基本信息、状态、执行时间展示正确 |
| 事件历史查看 | 事件列表加载、Payload 展示、解密正确 |
| 工作流控制 | Signal 发送、Cancel、Terminate 操作成功 |
| Namespace 切换 | 切换后列表数据正确刷新 |
| 权限控制 | viewer 角色无法执行控制操作 |
| 异常处理 | Server 不可用时错误提示友好 |

### 8.3 后端测试策略

| 测试类型 | 工具 | 覆盖范围 | 目标覆盖率 |
| --- | --- | --- | --- |
| 单元测试 | JUnit 5 + Mockito | Service 层业务逻辑、工具类 | ≥ 80% |
| 集成测试 | Spring Boot Test + Testcontainers | Controller → Service → SDK 链路 | ≥ 70% |
| Temporal 集成测试 | Testcontainers + Temporal Test Server | BFF ↔ Temporal Server gRPC 交互 | 关键路径 100% |
| 契约测试 | Spring Cloud Contract | BFF 对前端暴露的 API 契约 | 核心接口 100% |
| 性能测试 | JMeter / Gatling | BFF 接口吞吐量与延迟 | 基准测试 |

**Testcontainers 集成测试方案：**

| 测试容器 | 用途 |
| --- | --- |
| Temporal Server 容器 | 启动真实 Temporal Server（非 mock），验证 gRPC 调用 |
| PostgreSQL 容器 | 验证 Server 与 DB 的真实交互 |
| BFF 应用 | Spring Boot Test 启动完整应用上下文 |

### 8.4 CI/CD 测试流水线

| 阶段 | 触发条件 | 执行内容 | 门禁要求 |
| --- | --- | --- | --- |
| Lint | PR 提交 | ESLint + Checkstyle + Prettier 检查 | 零 error |
| 单元测试 | PR 提交 | Vitest + JUnit 5 | 覆盖率达标 + 零失败 |
| 集成测试 | PR 提交 | Testcontainers 集成测试 | 零失败 |
| 代码扫描 | PR 提交 | SonarQube 静态分析 | 质量门禁通过 |
| E2E 测试 | 合并至 develop | Playwright E2E | 核心场景通过 |
| 安全扫描 | 合并至 develop | 依赖漏洞扫描 + SAST | 无高危漏洞 |

### 8.5 官方服务测试保障

由于 Temporal Server 与 Runner 使用官方服务，测试策略侧重验证 **自研 BFF 与官方 Server 的集成正确性**：

| 验证维度 | 方式 |
| --- | --- |
| gRPC 兼容性 | 集成测试使用真实 Temporal Server 容器，验证 SDK 调用 |
| 版本升级回归 | Temporal Server 升级时，全量运行集成测试与 E2E 测试 |
| API 变更检测 | CI 中对比 Temporal API proto 变更，评估 BFF 适配 |
| Worker 兼容性 | 使用 Test Server 验证 Worker Activity/Workflow 注册与执行 |

---

## 九、版本管理与发布流程

### 9.1 版本号规则

采用语义化版本（Semantic Versioning）：`MAJOR.MINOR.PATCH`

| 版本段 | 含义 | 触发条件 | 示例 |
| --- | --- | --- | --- |
| MAJOR | 不兼容的 API 变更 | BFF REST API 破坏性变更；前端组件接口不兼容 | 1.0.0 → 2.0.0 |
| MINOR | 向后兼容的功能新增 | 新增页面、新增 API、新增扩展点 | 1.0.0 → 1.1.0 |
| PATCH | 向后兼容的缺陷修复 | Bug 修复、性能优化、安全补丁 | 1.0.0 → 1.0.1 |

**版本对齐策略：**

| 组件 | 版本基准 | 对齐要求 |
| --- | --- | --- |
| 自研 Web UI | 独立版本号 | 独立发版 |
| 自研 BFF | 独立版本号 | 独立发版 |
| Temporal Java SDK | 跟随 Server | SDK 版本与 Server 版本严格对齐 |
| Temporal Server | 官方版本 | 使用官方发布版本，不自行编译 |
| PostgreSQL | 官方版本 | 使用官方镜像版本 |

### 9.2 发布流程

```
feature 分支 ──> develop (集成测试)
                      │
                      v
               release/v{version} (发布准备)
                      │
                      ├── 修复发布问题
                      ├── 更新版本号
                      ├── 执行全量回归测试
                      │
                      v
               合并至 main + 打 Tag
                      │
                      v
               CI 构建镜像 ──> 推送镜像仓库
                      │
                      v
               部署至 Staging ──> 验收测试
                      │
                      v
               部署至 Production
```

### 9.3 发布步骤详解

| 步骤 | 操作 | 责任人 | 产出物 |
| --- | --- | --- | --- |
| 1. 发布准备 | 从 develop 拉出 `release/v{version}` 分支 | 开发负责人 | release 分支 |
| 2. 版本更新 | 更新 `package.json`（前端）/ `pom.xml`（BFF）版本号 | 开发负责人 | 版本号变更 |
| 3. 回归测试 | 执行全量集成测试 + E2E 测试 | QA | 测试报告 |
| 4. 发布说明 | 编写 CHANGELOG，记录变更项 | 开发负责人 | CHANGELOG.md |
| 5. 合并与打标 | 合并至 main，打 Git Tag `v{version}` | 开发负责人 | Git Tag |
| 6. 镜像构建 | CI 自动构建 Docker 镜像并推送 | CI/CD | Docker 镜像 |
| 7. Staging 部署 | 部署至预发布环境 | 运维 | 预发布环境 |
| 8. 验收测试 | 业务验收 | QA + 业务方 | 验收报告 |
| 9. 生产部署 | 灰度发布 → 全量发布 | 运维 | 生产环境 |
| 10. 发布回顾 | 记录发布问题与改进项 | 团队 | 回顾文档 |

### 9.4 灰度发布策略

| 策略 | 说明 |
| --- | --- |
| 前端灰度 | Nginx 按 Cookie/IP 分流，部分用户访问新版本静态资源 |
| BFF 灰度 | Nginx upstream 权重分流，部分流量路由至新版本 BFF 实例 |
| 回滚机制 | 镜像保留前 3 个版本，一键切换 Nginx upstream 回滚 |
| 数据库迁移 | Temporal Server 升级前必须备份数据库；auto-setup 自动迁移 Schema |

### 9.5 Temporal Server 升级流程

由于 Temporal Server 使用官方服务，升级需特别关注：

| 步骤 | 说明 |
| --- | --- |
| 1. 版本评估 | 查阅官方 Release Notes，评估 API 变更与 Breaking Changes |
| 2. SDK 对齐 | 升级 BFF 中的 Temporal Java SDK 至对应版本 |
| 3. 兼容测试 | 在 Staging 环境使用新版本 Server 运行全量集成测试 |
| 4. 数据库备份 | 生产数据库全量备份 |
| 5. 滚动升级 | 更新 Server Docker 镜像版本，auto-setup 自动迁移 Schema |
| 6. 验证 | E2E 测试 + 业务验证 |
| 7. 回滚预案 | 若升级失败，回滚 Server 镜像版本并恢复数据库备份 |

### 9.6 配置项版本管理

| 配置类型 | 管理方式 | 说明 |
| --- | --- | --- |
| Docker Compose 文件 | Git 仓库 | 每个环境独立 Compose 文件，版本化 |
| Nginx 配置 | Git 仓库 | 与前端版本绑定 |
| BFF application.yml | Git 仓库 + 环境变量 | 敏感信息用环境变量注入 |
| Temporal dynamic config | Git 仓库 | Server 动态配置文件 |
| PostgreSQL 调优参数 | Git 仓库 | 数据库启动参数 |

---

## 十、总结

### 架构设计要点回顾

| 要点 | 说明 |
| --- | --- |
| **二次开发边界清晰** | 仅 Web UI（Vue3）与 BFF（Java/Spring Boot）为自研；Temporal Server、Runner SDK、PostgreSQL 均使用官方服务 |
| **BFF 模式隔离前端与 gRPC** | 前端不直连 Temporal gRPC，由 BFF 统一鉴权、脱敏、解密 |
| **四层分离部署** | 接入层（A）、控制面（B）、持久化层（C）、数据面（D/E/N）独立部署 |
| **Docker 容器化** | 所有组件容器化部署，PostgreSQL 使用官方镜像 |
| **扩展点设计** | Web UI 与 BFF 均提供插件化扩展机制，支持业务定制 |
| **测试体系完整** | 单元测试 + 集成测试 + E2E 测试 + 静态分析，CI/CD 全流程门禁 |
| **版本管理规范** | 语义化版本 + Git Flow 分支管理 + 灰度发布 + 回滚预案 |

### 核心原则

1. **Temporal Server 与 Runner 使用官方服务** -- 不修改源码，通过配置与 SDK 集成，享受官方维护与版本升级。
2. **自研 BFF 承接业务逻辑** -- 鉴权、脱敏、Codec 解密、数据聚合均在 BFF 层完成，前端与 Server 解耦。
3. **PostgreSQL 容器化独立部署** -- 与 Server 同可用区保证低延迟，独立服务器保证资源隔离与运维便利。
4. **扩展点优先于硬编码** -- 业务定制通过扩展点机制实现，避免修改核心代码。
5. **测试驱动质量保障** -- 自研代码全覆盖测试，官方服务集成测试验证兼容性。
