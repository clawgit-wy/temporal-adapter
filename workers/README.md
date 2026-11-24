# 楼宇门禁权限下发平台 - Worker 部署管理

## 项目结构

```
workers/
├── device-access-worker/          # Worker 项目（单镜像，通过 WORKER_SCENARIOS 区分场景）
│   ├── src/                       # Java 源码
│   ├── docs/                      # 业务文档
│   ├── Dockerfile                 # Worker 镜像构建
│   ├── Jenkinsfile                # 本项目的 Jenkins 发版配置
│   ├── pom.xml
│   └── README.md                  # Worker 项目说明
├── docker-compose.yml             # Worker 群编排（仅Worker，不含基础设施）
├── .env.example                   # 环境变量模板
└── README.md                      # 本文件
```

> 每个 Worker 项目独立一个 Jenkins Pipeline。当前只有 `device-access-worker` 一个项目，后续新增 Worker 项目时，在该项目目录下放置独立的 Jenkinsfile 即可。

## 架构说明

```
                    ┌──────────────────────────────────┐
                    │        Temporal Server            │
                    │        (独立部署/已有服务)          │
                    └──────────────┬───────────────────┘
                                   │
        ┌──────────────┬───────────┼───────────┬──────────────┐
        │              │           │           │              │
  ┌─────▼─────┐ ┌──────▼─────┐ ┌──▼──────┐ ┌──▼────────┐ ┌──▼─────────┐
  │ permission │ │ permission │ │ permis- │ │ batch-    │ │ visitor-   │
  │ -issue     │ │ -modify    │ │ sion-   │ │ issue     │ │ issue      │
  │            │ │            │ │ cancel  │ │           │ │            │
  └────────────┘ └────────────┘ └─────────┘ └───────────┘ └────────────┘
        │              │           │           │              │
        └──────────────┴───────────┼───────────┴──────────────┘
                                   │
                    ┌──────────────▼───────────────────┐
                    │     富士 API / 新开普 API          │
                    └──────────────────────────────────┘
```

所有 Worker 共用同一个 Docker 镜像（`device-access-worker`），通过 `WORKER_SCENARIOS` 环境变量区分不同场景。docker-compose 仅管理 Worker 容器，Temporal Server 需独立部署。

## Jenkins 部署

### 配置方式

每个 Worker 项目对应一个独立的 Jenkins Pipeline 任务：

| Jenkins 任务名 | Pipeline 脚本路径 | 说明 |
|---------------|------------------|------|
| device-access-worker | `device-access-worker/Jenkinsfile` | 门禁权限下发 Worker |

> 后续新增 Worker 项目时，在新项目目录下放置 Jenkinsfile，然后在 Jenkins 中新建对应任务即可。

### Jenkins 任务配置步骤

1. Jenkins 新建 **Pipeline** 任务，命名为 `device-access-worker`
2. Pipeline 定义选择 **Pipeline script from SCM**
3. 指定 Git 仓库地址
4. Script Path 填写 `device-access-worker/Jenkinsfile`
5. 构建时选择对应参数

### 支持的操作

| 操作 | 说明 | 参数 |
|------|------|------|
| BUILD | 构建镜像（不部署） | IMAGE_TAG |
| DEPLOY | 构建镜像 + 部署 | IMAGE_TAG, WORKER_NAME |
| SCALE | 弹性扩缩容 | WORKER_NAME, REPLICA_COUNT |
| START | 启动指定Worker | WORKER_NAME |
| STOP | 停止指定Worker | WORKER_NAME |
| RESTART | 重启指定Worker | WORKER_NAME |
| STATUS | 查看状态 | - |

> **WORKER_NAME** 可选值：`all`、`worker-permission-issue`、`worker-permission-modify`、`worker-permission-cancel`、`worker-permission-increment`、`worker-batch-issue`、`worker-visitor-issue`

### 常见操作示例

**代码更新后全量部署**：
- OPERATION=DEPLOY, WORKER_NAME=all, IMAGE_TAG=1.0.1

**只更新权限下发 Worker**：
- OPERATION=DEPLOY, WORKER_NAME=worker-permission-issue

**批量下发扩容到 3 实例**：
- OPERATION=SCALE, WORKER_NAME=worker-batch-issue, REPLICA_COUNT=3

## 快速部署

### 1. 配置环境变量

```bash
cp .env.example .env
vi .env
```

关键配置：
- `TEMPORAL_ADDRESS` - Temporal Server 地址（如 `10.66.32.100:7233`）
- `FUJI_API_BASE_URL` - 富士 API 地址
- `XINKAIPU_API_BASE_URL` - 新开普 API 地址

### 2. 构建并启动

```bash
# 构建镜像
docker-compose build

# 启动全部 Worker
docker-compose up -d

# 只启动部分 Worker
docker-compose up -d worker-permission-issue worker-permission-cancel

# 查看状态
docker-compose ps

# 查看日志
docker-compose logs -f worker-permission-issue
```

### 3. 弹性扩缩容

```bash
# 权限下发扩容到 3 个实例
docker-compose up -d --scale worker-permission-issue=3 worker-permission-issue

# 缩容到 1 个实例
docker-compose up -d --scale worker-permission-issue=1 worker-permission-issue
```

> Temporal Server 会自动将任务轮询分配给同一 TaskQueue 的多个 Worker 实例。

## Worker 容器资源限制

每个容器都配置了 CPU/内存限制和日志轮转：

| Worker 服务 | 场景 | CPU上限 | 内存上限 | JVM参数 |
|-------------|------|---------|---------|---------|
| worker-permission-issue | 权限下发 | 1.0核 | 768M | -Xms256m -Xmx512m |
| worker-permission-modify | 权限改写 | 1.0核 | 512M | -Xms128m -Xmx384m |
| worker-permission-cancel | 权限注销 | 1.0核 | 512M | -Xms128m -Xmx384m |
| worker-permission-increment | 增量权限 | 1.0核 | 512M | -Xms128m -Xmx384m |
| worker-batch-issue | 批量下发 | 2.0核 | 1024M | -Xms256m -Xmx768m |
| worker-visitor-issue | 访客下发 | 1.0核 | 768M | -Xms256m -Xmx512m |

日志轮转：每个容器单文件最大 100M，最多保留 3 个文件。

> 资源预留总计约：2.0核 / 1152M
> 资源上限总计约：7.0核 / 4096M

## 常用运维命令

```bash
# 查看全部 Worker 状态
docker-compose ps

# 查看指定 Worker 日志（实时）
docker-compose logs -f worker-permission-issue

# 重启指定 Worker
docker-compose restart worker-permission-issue

# 停止全部 Worker
docker-compose down

# 重新构建镜像并部署
docker-compose build && docker-compose up -d

# 清理旧镜像
docker image prune -f
```

## 相关文档

- [Worker 项目说明](device-access-worker/README.md)
- [业务逻辑文档](device-access-worker/docs/楼宇门禁权限下发业务逻辑.md)
- [工作流设计文档](device-access-worker/docs/楼宇门禁权限下发工作流.md)
- [测试说明](device-access-worker/src/test/resources/README.md)
