# Temporal 服务端部署与前端对接文档

> 适用项目：
> - `/Users/apple/Desktop/AdapterService/ui-redesign`（React 前端）
> - `/Users/apple/Desktop/AdapterService/ui-main`（SvelteKit 前端）
>
> 文档版本：v1.0
> 最后更新：2026-08-17

---

## 目录

1. [架构总览](#1-架构总览)
2. [ui-redesign 所需的服务端配置](#2-ui-redesign-所需的服务端配置)
3. [Docker 镜像清单与导出](#3-docker-镜像清单与导出)
4. [docker-compose 部署文件](#4-docker-compose-部署文件)
5. [离线服务器部署流程](#5-离线服务器部署流程)
6. [ui-main 连接外部 Temporal UI Server](#6-ui-main-连接外部-temporal-ui-server)
7. [ui-redesign 连接配置](#7-ui-redesign-连接配置)
8. [环境变量参考](#8-环境变量参考)
9. [故障排查](#9-故障排查)

---

## 1. 架构总览

### 1.1 三种部署场景

```
┌────────────────────────────────────────────────────────────────┐
│ 场景 A：ui-redesign + Docker 后端（推荐）                       │
│                                                                │
│  浏览器                                                        │
│    ↓ http://localhost:5173                                     │
│  ui-redesign (Vite Dev Server 5173)                            │
│    ↓ proxy /api/v1                                             │
│  temporal-ui-server (Docker 8080) ← REST API                  │
│    ↓ gRPC                                                      │
│  temporal-server (Docker 7233) ← 工作流调度核心                │
│    ↓                                                           │
│  postgresql (Docker 5432)                                      │
└────────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────────────┐
│ 场景 B：ui-main + Docker 后端（解决用户当前问题）               │
│                                                                │
│  浏览器                                                        │
│    ↓ http://localhost:3000                                     │
│  ui-main (Vite Dev Server 3000)                                │
│    ↓ 直接请求 http://localhost:8080/api/v1/*                   │
│  temporal-ui-server (Docker 8080) ← REST API                  │
│    ↓ gRPC                                                      │
│  temporal-server (Docker 7233)                                 │
│    ↓                                                           │
│  postgresql (Docker 5432)                                      │
└────────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────────────┐
│ 场景 C：ui-main 内置 UI Server（开发默认）                      │
│                                                                │
│  浏览器                                                        │
│    ↓ http://localhost:3000                                     │
│  ui-main (Vite Dev Server 3000)                                │
│    ├── 启动 Go UI Server（8081，development 模式）             │
│    │     ↓ gRPC                                                │
│    │   temporal-server (7233)                                  │
│    └── 前端代理 /api/v1 → 8081                                 │
│                                                                │
│  注意：内置 UI Server 需要本机有 Go 环境，且                   │
│        某些环境下可能编译失败，建议改用 Docker 方案             │
└────────────────────────────────────────────────────────────────┘
```

### 1.2 端口规划

| 端口 | 服务 | 用途 | 是否对外暴露 |
|------|------|------|-------------|
| 5432 | PostgreSQL | 数据库 | 仅内网（如需排错可暴露） |
| 7233 | Temporal Server gRPC | 工作流调度 | Worker 连接用 |
| 8080 | Temporal UI Server | REST API + 官方 UI | 对前端暴露 |
| 5173 | ui-redesign Dev Server | React 开发服务器 | 对浏览器暴露 |
| 5174 | ui-redesign Dev Server (备) | 当 5173 被占用 | 对浏览器暴露 |
| 3000 | ui-main Dev Server | SvelteKit 开发服务器 | 对浏览器暴露 |
| 8081 | ui-main 内置 Go UI Server | dev 模式下的 BFF | 本地使用 |

---

## 2. ui-redesign 所需的服务端配置

### 2.1 必需组件清单

| 组件 | 镜像 | 版本 | 用途 |
|------|------|------|------|
| PostgreSQL | `postgres:15-alpine` | 15.x | 持久化存储工作流历史、可见性数据 |
| Temporal Server | `temporalio/auto-setup:1.24.2` | 1.24.2 | 工作流调度核心，首次启动自动初始化数据库 schema |
| Temporal UI Server | `temporalio/ui:2.32.0` | 2.32.0 | REST API 代理（`/api/v1/*`） + 官方 Web UI |

### 2.2 REST API 端点清单（ui-redesign 使用的接口）

ui-redesign 前端通过 `/api/v1/*` 调用以下接口：

| 端点 | 方法 | 用途 |
|------|------|------|
| `/api/v1/cluster-info` | GET | 集群信息（顶部展示） |
| `/api/v1/system-info` | GET | 系统信息（版本、能力） |
| `/api/v1/settings` | GET | UI 设置（编码器、命名空间等） |
| `/api/v1/namespaces` | GET | 命名空间列表 |
| `/api/v1/namespaces/{ns}` | GET | 命名空间详情 |
| `/api/v1/namespaces/{ns}/workflows` | GET | 工作流列表 |
| `/api/v1/namespaces/{ns}/workflows/{wid}` | GET | 工作流详情 |
| `/api/v1/namespaces/{ns}/workflows/{wid}/history` | GET | 工作流历史事件 |
| `/api/v1/namespaces/{ns}/workflows/{wid}/terminate` | POST | 终止工作流 |
| `/api/v1/namespaces/{ns}/workflows/{wid}/cancel` | POST | 取消工作流 |
| `/api/v1/namespaces/{ns}/workflows/{wid}/signal/{name}` | POST | 发送信号 |
| `/api/v1/namespaces/{ns}/workflows/{wid}/update/{name}` | POST | 发送更新 |
| `/api/v1/namespaces/{ns}/task-queues/{queue}` | GET | 任务队列详情（Worker 信息） |
| `/api/v1/namespaces/{ns}/schedules` | GET | 计划任务列表 |
| `/api/v1/namespaces/{ns}/schedules/{id}` | POST | 创建计划任务 |
| `/api/v1/namespaces/{ns}/schedules/{id}/patch` | POST | 暂停/恢复/删除计划 |

### 2.3 必需的环境变量配置

**Temporal UI Server 关键环境变量**：

| 变量 | 必需 | 默认值 | 说明 |
|------|------|--------|------|
| `TEMPORAL_ADDRESS` | ✅ | `127.0.0.1:7233` | Temporal Server gRPC 地址 |
| `TEMPORAL_UI_PORT` | ✅ | `8080` | UI Server 监听端口 |
| `TEMPORAL_UI_ENABLED` | ✅ | `true` | 是否启用官方 UI |
| `TEMPORAL_DEFAULT_NAMESPACE` | ✅ | `default` | 默认命名空间 |
| `TEMPORAL_AUTH_ENABLED` | ✅ | `false` | 是否启用认证 |
| `TEMPORAL_CORS_UNSAFE_ALLOW_ALL_ORIGINS` | 开发 | `false` | 允许所有 CORS 源 |
| `TEMPORAL_CORS_ORIGINS` | 推荐 | （空） | 允许的 CORS 源列表 |
| `TEMPORAL_DISABLE_WRITE_ACTIONS` | ✅ | `false` | 是否禁用写操作（终止/取消等） |
| `TEMPORAL_WORKFLOW_SIGNAL_DISABLED` | ✅ | `false` | 是否禁用发送信号 |
| `TEMPORAL_WORKFLOW_UPDATE_DISABLED` | ✅ | `false` | 是否禁用发送更新 |
| `TEMPORAL_CSRF_COOKIE_INSECURE` | HTTP 部署 | `false` | HTTP 下需要设为 true |

### 2.4 必需的动态配置（Dynamic Config）

启用以下配置才能完整支持 ui-redesign 所有功能：

```yaml
# /etc/temporal/config/dynamicconfig/docker.yaml

# 启用 Worker 心跳（工作进程页数据来源）
worker.heartbeatEnabled:
  - value: true
frontend.workerHeartbeatsEnabled:
  - value: true
system.enableWorkerHeartbeats:
  - value: true

# 启用 Workflow Update API（"发送更新"功能所需）
frontend.enableUpdateWorkflowExecution:
  - value: true
frontend.enableUpdateWorkflowExecutionAsyncAccepted:
  - value: true
```

详见 [dynamicconfig/docker.yaml](file:///Users/apple/Desktop/AdapterService/dynamicconfig/docker.yaml)。

---

## 3. Docker 镜像清单与导出

### 3.1 已导出的镜像

镜像已导出为 `.tar.gz` 压缩包，位于 `/Users/apple/Desktop/AdapterService/docker-images/`：

| 文件 | 镜像 | 大小 | 用途 |
|------|------|------|------|
| `temporalio-auto-setup-1.24.2.tar.gz` | `temporalio/auto-setup:1.24.2` | 151 MB | Temporal Server（含自动 schema 初始化） |
| `temporalio-ui-2.32.0.tar.gz` | `temporalio/ui:2.32.0` | 43 MB | Temporal UI Server（REST API + 官方 UI） |
| `postgres-15-alpine.tar.gz` | `postgres:15-alpine` | 104 MB | PostgreSQL 数据库 |

**总计：约 298 MB**

### 3.2 重新导出命令（如需更新）

```bash
# 创建导出目录
mkdir -p /Users/apple/Desktop/AdapterService/docker-images

# 并行导出三个镜像
docker save temporalio/auto-setup:1.24.2 | gzip > docker-images/temporalio-auto-setup-1.24.2.tar.gz &
docker save temporalio/ui:2.32.0 | gzip > docker-images/temporalio-ui-2.32.0.tar.gz &
docker save postgres:15-alpine | gzip > docker-images/postgres-15-alpine.tar.gz &
wait

# 验证导出
ls -lh docker-images/
```

---

## 4. docker-compose 部署文件

部署文件位于 [docker-compose.temporal-stack.yml](file:///Users/apple/Desktop/AdapterService/docker-compose.temporal-stack.yml)。

### 4.1 文件结构

```
/Users/apple/Desktop/AdapterService/
├── docker-compose.temporal-stack.yml   # 一体化部署文件
├── dynamicconfig/
│   └── docker.yaml                     # Temporal 动态配置
└── docker-images/                      # 镜像 tar 包
    ├── temporalio-auto-setup-1.24.2.tar.gz
    ├── temporalio-ui-2.32.0.tar.gz
    └── postgres-15-alpine.tar.gz
```

### 4.2 关键配置说明

- **网络**：所有服务加入 `temporal_default` 网络，容器间通过容器名通信
- **端口暴露**：
  - `8080:8080`：前端访问 UI Server REST API
  - `7233:7233`：Worker 连接 Temporal Server gRPC
  - `5432:5432`：数据库（可选暴露，便于排错）
- **数据持久化**：PostgreSQL 数据通过 `temporal_postgres_data` 卷持久化
- **CORS**：开发环境允许 `localhost:5173/5174/3000/8080` 跨域
- **CSRF Cookie**：HTTP 部署下设置 `TEMPORAL_CSRF_COOKIE_INSECURE=true`，让 Cookie 在 HTTP 下可存储

### 4.3 启动命令

```bash
cd /Users/apple/Desktop/AdapterService

# 启动全部服务
docker compose -f docker-compose.temporal-stack.yml up -d

# 查看启动状态
docker compose -f docker-compose.temporal-stack.yml ps

# 查看日志
docker compose -f docker-compose.temporal-stack.yml logs -f temporal-ui
docker compose -f docker-compose.temporal-stack.yml logs -f temporal

# 停止
docker compose -f docker-compose.temporal-stack.yml down

# 停止并删除数据（重置环境）
docker compose -f docker-compose.temporal-stack.yml down -v
```

---

## 5. 离线服务器部署流程

### 5.1 上传文件到目标服务器

将以下文件上传到目标服务器（建议放在 `/opt/temporal-stack/`）：

```bash
# 在本机执行（替换为实际服务器地址）
SERVER=user@your-server

ssh $SERVER 'mkdir -p /opt/temporal-stack/dynamicconfig /opt/temporal-stack/docker-images'

# 上传 docker-compose 文件
scp /Users/apple/Desktop/AdapterService/docker-compose.temporal-stack.yml $SERVER:/opt/temporal-stack/

# 上传动态配置
scp /Users/apple/Desktop/AdapterService/dynamicconfig/docker.yaml $SERVER:/opt/temporal-stack/dynamicconfig/

# 上传镜像 tar 包
scp /Users/apple/Desktop/AdapterService/docker-images/*.tar.gz $SERVER:/opt/temporal-stack/docker-images/
```

### 5.2 在目标服务器加载镜像

```bash
ssh $SERVER
cd /opt/temporal-stack

# 加载三个镜像
docker load < docker-images/temporalio-auto-setup-1.24.2.tar.gz
docker load < docker-images/temporalio-ui-2.32.0.tar.gz
docker load < docker-images/postgres-15-alpine.tar.gz

# 验证镜像已加载
docker images | grep -E "temporalio|postgres"
# 应看到三行输出
```

### 5.3 启动服务

```bash
cd /opt/temporal-stack

# 启动
docker compose -f docker-compose.temporal-stack.yml up -d

# 等待 30 秒让 schema 初始化完成
sleep 30

# 验证服务状态
docker compose -f docker-compose.temporal-stack.yml ps

# 验证 UI Server 响应
curl http://localhost:8080/api/v1/cluster-info
# 应返回 JSON 格式的集群信息

curl http://localhost:8080/api/v1/system-info
# 应返回 serverVersion 等信息
```

### 5.4 验证服务可用

```bash
# 1. 检查 PostgreSQL 健康
docker exec temporal-postgresql pg_isready -U temporal

# 2. 检查 Temporal Server gRPC 端口
nc -zv localhost 7233

# 3. 检查 UI Server REST API
curl -s http://localhost:8080/api/v1/cluster-info | head -c 200

# 4. 检查命名空间列表（应包含 default）
curl -s http://localhost:8080/api/v1/namespaces | head -c 500

# 5. 检查工作流列表（首次应为空）
curl -s "http://localhost:8080/api/v1/namespaces/default/workflows" | head -c 200
```

---

## 6. ui-main 连接外部 Temporal UI Server

### 6.1 问题原因分析

用户在另一台服务器启动 ui-main 时报错"请确认 Temporal UI Server (端口 8080) 已启动"，原因：

| 原因 | 说明 |
|------|------|
| **场景 1**：ui-main 在 `ui-server` 模式启动，但本机没有 Go 环境 | `vite-plugin-ui-server` 会尝试编译 `server/` 目录下的 Go 代码，编译失败时 UI Server 不会启动 |
| **场景 2**：未使用 vite 内置 UI Server，直接连接 8080 端口 | 但目标服务器上没有 8080 端口的 UI Server 进程 |
| **场景 3**：环境变量未配置 | `VITE_API` 指向了错误的地址 |

### 6.2 推荐方案：使用 Docker 启动外部 UI Server

按 [第 5 节](#5-离线服务器部署流程) 在目标服务器上启动 Docker 版 Temporal UI Server（端口 8080），然后修改 ui-main 配置直接连接。

### 6.3 ui-main 配置修改

ui-main 通过 `VITE_API` 环境变量指定 Temporal UI Server 地址。需要创建一个 `.env` 文件：

```bash
cd /Users/apple/Desktop/AdapterService/ui-main

# 创建 .env 文件指向 Docker 启动的 UI Server
cat > .env << 'EOF'
VITE_API=http://localhost:8080
VITE_MODE=development
VITE_TEMPORAL_UI_BUILD_TARGET=local
EOF
```

或者使用 ui-main 内置的 `dev:docker` 脚本（已预置 `VITE_API=http://localhost:8080`）：

```bash
cd /Users/apple/Desktop/AdapterService/ui-main

# 使用 dev:docker 脚本（package.json 中已定义）
pnpm dev:docker
```

### 6.4 关闭 ui-main 内置 UI Server

如果使用 Docker 启动外部 UI Server，需要禁用 ui-main 的 vite 插件（避免端口冲突）。

修改 `vite.config.ts`：

```typescript
// 判断是否使用外部 UI Server
const USE_EXTERNAL_UI_SERVER = process.env.USE_EXTERNAL_UI_SERVER === 'true';

export default defineConfig({
  plugins: [
    sveltekit(),
    oidcServerPlugin(),
    temporalServer(),
    // 仅在不使用外部 UI Server 时启用内置插件
    ...(USE_EXTERNAL_UI_SERVER ? [] : [uiServerPlugin()]),
  ],
  // ...其他配置
});
```

然后启动时设置环境变量：

```bash
USE_EXTERNAL_UI_SERVER=true pnpm dev:docker
```

### 6.5 简化方案：直接使用 dev:docker 脚本

最简单的做法是使用 ui-main 自带的 `dev:docker` 脚本（[package.json:30](file:///Users/apple/Desktop/AdapterService/ui-main/package.json#L30)）：

```bash
# 1. 先启动 Docker 版 Temporal UI Server（端口 8080）
cd /Users/apple/Desktop/AdapterService
docker compose -f docker-compose.temporal-stack.yml up -d

# 2. 等待 30 秒让服务初始化
sleep 30

# 3. 启动 ui-main，自动连接 8080 端口
cd ui-main
pnpm dev:docker
# 或显式设置：VITE_API=http://localhost:8080 vite dev --mode docker
```

访问 http://localhost:3000 即可。

### 6.6 端口冲突处理

如果 ui-main 内置 UI Server 已占用 8081 端口，但你想改用 Docker 8080 端口：

```bash
# 1. 停止 ui-main（如果运行中）
# 在 ui-main 终端按 Ctrl + C

# 2. 启动 Docker 版本（8080 端口）
docker compose -f docker-compose.temporal-stack.yml up -d temporal-ui

# 3. 修改 .env.ui-server，将端口改为 8080
cat > /Users/apple/Desktop/AdapterService/ui-main/.env.ui-server << 'EOF'
VITE_API=http://localhost:8080
VITE_MODE=development
VITE_TEMPORAL_UI_BUILD_TARGET=local
EOF

# 4. 启动 ui-main（仍使用 ui-server mode，但 API 指向 8080）
cd ui-main
pnpm dev:ui-server
```

注意：此模式下 vite-plugin-ui-server 仍会尝试启动内置 Go UI Server（监听 8081）。如不想启动，需修改 `vite.config.ts`（参见 6.4）。

---

## 7. ui-redesign 连接配置

### 7.1 开发环境（默认配置）

ui-redesign 默认通过 Vite proxy 将 `/api/v1` 代理到 `http://localhost:8080`，无需额外配置。

```bash
# 1. 启动 Docker 后端
cd /Users/apple/Desktop/AdapterService
docker compose -f docker-compose.temporal-stack.yml up -d

# 2. 启动 ui-redesign
cd ui-redesign
pnpm dev

# 3. 访问 http://localhost:5173
```

### 7.2 连接到远程服务器

如果 Temporal UI Server 部署在另一台服务器，修改 `.env.development`：

```bash
# .env.development
DEV_TEMPORAL_UI_SERVER=http://remote-server:8080
```

或创建 `.env.development.local` 覆盖：

```bash
# .env.development.local（不提交到 git）
DEV_TEMPORAL_UI_SERVER=http://192.168.1.100:8080
```

### 7.3 生产环境构建

生产环境 ui-redesign 通常部署在 Nginx 后面，由 Nginx 反向代理 `/api/v1` 到 Temporal UI Server。详见 [多环境部署文档](file:///Users/apple/Desktop/AdapterService/ui-redesign/docs/多环境部署文档.md)。

---

## 8. 环境变量参考

### 8.1 Temporal UI Server 关键环境变量

| 变量 | 默认值 | 说明 |
|------|--------|------|
| `TEMPORAL_ADDRESS` | `127.0.0.1:7233` | Temporal Server gRPC 地址 |
| `TEMPORAL_UI_PORT` | `8080` | 监听端口 |
| `TEMPORAL_UI_ENABLED` | `true` | 是否启用官方 UI |
| `TEMPORAL_CLOUD_UI` | `false` | 是否为云版 UI |
| `TEMPORAL_DEFAULT_NAMESPACE` | `default` | 默认命名空间 |
| `TEMPORAL_AUTH_ENABLED` | `false` | 是否启用认证 |
| `TEMPORAL_DISABLE_WRITE_ACTIONS` | `false` | 禁用所有写操作 |
| `TEMPORAL_WORKFLOW_TERMINATE_DISABLED` | `false` | 禁用终止 |
| `TEMPORAL_WORKFLOW_CANCEL_DISABLED` | `false` | 禁用取消 |
| `TEMPORAL_WORKFLOW_SIGNAL_DISABLED` | `false` | 禁用信号 |
| `TEMPORAL_WORKFLOW_UPDATE_DISABLED` | `false` | 禁用更新 |
| `TEMPORAL_WORKFLOW_RESET_DISABLED` | `false` | 禁用重置 |
| `TEMPORAL_START_WORKFLOW_DISABLED` | `false` | 禁用启动工作流 |
| `TEMPORAL_CORS_UNSAFE_ALLOW_ALL_ORIGINS` | `false` | 允许所有源（开发用） |
| `TEMPORAL_CORS_ORIGINS` | （空） | 允许的源列表（逗号分隔） |
| `TEMPORAL_CSRF_COOKIE_INSECURE` | `false` | HTTP 下需设为 true |
| `TEMPORAL_CODEC_ENDPOINT` | （空） | 编解码服务器地址 |
| `TEMPORAL_CODEC_PASS_ACCESS_TOKEN` | `false` | 编解码请求携带 access token |
| `TEMPORAL_FEEDBACK_URL` | （空） | 反馈链接 |
| `TEMPORAL_HIDE_LOGS` | `false` | 隐藏日志 |

完整变量定义参见 [server/config/docker.yaml](file:///Users/apple/Desktop/AdapterService/ui-main/server/config/docker.yaml)。

### 8.2 Temporal Server 关键环境变量

| 变量 | 默认值 | 说明 |
|------|--------|------|
| `DB` | `postgres12` | 数据库类型 |
| `DB_PORT` | `5432` | 数据库端口 |
| `POSTGRES_USER` | `temporal` | 数据库用户 |
| `POSTGRES_PWD` | `temporal` | 数据库密码 |
| `POSTGRES_SEEDS` | `postgresql` | 数据库主机 |
| `DBNAME` | `temporal` | 数据库名 |
| `DYNAMIC_CONFIG_FILE_PATH` | （空） | 动态配置文件路径 |
| `LOG_LEVEL` | `info` | 日志级别 |
| `SERVICES` | `history,matching,frontend,worker` | 启动的服务 |

---

## 9. 故障排查

### 9.1 ui-main 报错"请确认 Temporal UI Server (端口 8080) 已启动"

**原因**：ui-main 配置 `VITE_API=http://localhost:8080`，但 8080 端口无服务监听。

**排查步骤**：

```bash
# 1. 检查 8080 端口是否有进程监听
lsof -i :8080
# 或
nc -zv localhost 8080

# 2. 检查 Docker 容器状态
docker ps | grep temporal-ui

# 3. 检查容器日志
docker logs temporal-ui-server --tail 50

# 4. 直接 curl 测试
curl -v http://localhost:8080/api/v1/cluster-info
```

**修复方案**：

```bash
# 方案 A：启动 Docker 版 UI Server
cd /Users/apple/Desktop/AdapterService
docker compose -f docker-compose.temporal-stack.yml up -d temporal-ui

# 方案 B：修改 ui-main 使用 8081 端口（内置 Go UI Server）
cd ui-main
pnpm dev:ui-server
# 内置 UI Server 监听 8081，前端通过 8081 访问

# 方案 C：使用 dev:docker 脚本自动连接 8080
cd ui-main
pnpm dev:docker
```

### 9.2 启动 Docker 报错 "network temporal_default already exists"

**原因**：之前使用其他 docker-compose 文件创建了同名网络。

**解决**：

```bash
# 方案 A：使用现有网络（推荐，无需处理）
# docker-compose.temporal-stack.yml 已配置为使用外部网络

# 方案 B：删除旧网络
docker network rm temporal_default

# 方案 C：修改 docker-compose 中的网络名
# 编辑 docker-compose.temporal-stack.yml，将 temporal_default 改为其他名字
```

### 9.3 启动报错 "address already in use" 端口冲突

**原因**：8080 端口被其他进程占用。

**排查**：

```bash
# 查看占用进程
lsof -i :8080

# 如果是旧的 temporal-ui-server 容器
docker stop temporal-ui-server
docker rm temporal-ui-server

# 如果是其他进程
kill -9 <PID>
```

### 9.4 Temporal Server 启动后立即退出

**原因**：数据库未就绪，或数据库初始化失败。

**排查**：

```bash
# 查看日志
docker logs temporal-server --tail 100

# 常见错误：
# 1. "connection refused" → PostgreSQL 未启动或不可达
# 2. "schema already exists" → 数据卷已有旧数据，需清理
# 3. "permission denied" → 数据库用户权限问题

# 清理重置
docker compose -f docker-compose.temporal-stack.yml down -v
docker compose -f docker-compose.temporal-stack.yml up -d
```

### 9.5 工作流详情页 "工作流未找到"

**原因**：访问的工作流 ID 不存在，或命名空间不匹配。

**排查**：

```bash
# 1. 检查工作流列表是否为空
curl -s "http://localhost:8080/api/v1/namespaces/default/workflows" | head -c 500

# 2. 如果列表为空，启动一个工作流
# 通过 ui-redesign "启动工作流" 功能创建

# 3. 检查命名空间配置
curl -s http://localhost:8080/api/v1/namespaces | head -c 500
```

### 9.6 "发送信号" / "发送更新" 按钮点击无响应

**原因**：Temporal Server 未启用 Update API，或 UI Server 禁用了对应功能。

**排查**：

```bash
# 1. 检查动态配置
cat /Users/apple/Desktop/AdapterService/dynamicconfig/docker.yaml
# 应包含 frontend.enableUpdateWorkflowExecution: true

# 2. 检查 UI Server 环境变量
docker exec temporal-ui-server env | grep -E "SIGNAL|UPDATE|DISABLE"
# 应均为 false

# 3. 重启 Temporal Server 让动态配置生效
docker compose -f docker-compose.temporal-stack.yml restart temporal
```

### 9.7 工作进程页面无数据

**原因**：Worker 心跳未启用，或没有 Worker 连接到 Temporal Server。

**排查**：

```bash
# 1. 检查是否有 Worker 进程在运行
docker ps | grep worker

# 2. 检查 Worker 心跳配置
cat /Users/apple/Desktop/AdapterService/dynamicconfig/docker.yaml | grep heartbeat
# 应包含 worker.heartbeatEnabled: true

# 3. 启动一个 Worker 测试
# 参考 /Users/apple/Desktop/AdapterService/temporal/docker-compose-all.yml
```

### 9.8 创建计划任务失败

**原因**：Temporal Server 版本过低不支持 Schedules API，或 UI Server 禁用了写操作。

**排查**：

```bash
# 1. 检查 Temporal Server 版本（需 ≥ 1.18）
docker logs temporal-server 2>&1 | grep -i version | head -5

# 2. 检查 UI Server 是否禁用写操作
docker exec temporal-ui-server env | grep DISABLE
# TEMPORAL_DISABLE_WRITE_ACTIONS 应为 false

# 3. 检查动态配置
# 确保 system.enableSchedules: true（默认启用，无需显式配置）
```

---

## 附录：完整部署检查清单

部署完成后，按此清单逐项验证：

- [ ] PostgreSQL 容器健康：`docker exec temporal-postgresql pg_isready -U temporal`
- [ ] Temporal Server gRPC 端口可达：`nc -zv localhost 7233`
- [ ] UI Server REST API 可达：`curl http://localhost:8080/api/v1/cluster-info`
- [ ] 集群信息返回正常：`curl -s http://localhost:8080/api/v1/cluster-info | jq .clusterName`
- [ ] 命名空间列表包含 default：`curl -s http://localhost:8080/api/v1/namespaces | jq .namespaces`
- [ ] 系统信息返回版本号：`curl -s http://localhost:8080/api/v1/system-info | jq .serverVersion`
- [ ] 设置接口返回正确：`curl -s http://localhost:8080/api/v1/settings | jq .disableWriteActions`
- [ ] ui-redesign 能访问：浏览器打开 http://localhost:5173，工作流列表加载正常
- [ ] ui-main 能访问：浏览器打开 http://localhost:3000，无"UI Server 未启动"错误
- [ ] Worker 能连接：启动一个 Worker 容器，能注册到 Temporal Server

---

**文档结束**
