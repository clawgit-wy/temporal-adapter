# Temporal Server v1.31.2 + UI Server v2.53.3 离线安装文档

> **目标环境**：离线 CentOS 7 x86_64（内核 3.10，glibc 2.17）
> **PostgreSQL**：已通过 Docker 部署（端口 5432 已映射到宿主机）
> **安装方式**：Temporal Server 与 UI Server 均为**二进制直装 + systemd 管理**（非 Docker）
> **软件包来源**：`setup/packages/` 目录

---

## 目录

- [一、软件包清单](#一软件包清单)
- [二、系统依赖](#二系统依赖)
- [三、安装顺序](#三安装顺序)
- [四、安装 Temporal Server v1.31.2](#四安装-temporal-server-v1312)
- [五、安装 UI Server v2.53.3](#五安装-ui-server-v2533)
- [六、验证部署](#六验证部署)
- [七、systemd 服务管理](#七systemd-服务管理)
- [八、常见问题](#八常见问题)

---

## 一、软件包清单

| 文件 | 大小 | 内容 | 架构 | 链接方式 |
|------|------|------|------|----------|
| `temporal_1.31.2_linux_amd64.tar.gz` | 89 MB | temporal-server + temporal-sql-tool + temporal-cassandra-tool + temporal-elasticsearch-tool + tdbg + config/ | x86_64 | 静态链接（go1.26.4, CGO_ENABLED=0） |
| `ui-server_2.53.3_linux_amd64.tar.gz` | 9 MB | ui-server 二进制 + LICENSE + README | x86_64 | 静态链接（go1.26.5, CGO_ENABLED=0） |

### 1.1 temporal 包内容

```
temporal-server               # Temporal Server 核心服务（frontend/history/matching/worker 四合一）
temporal-sql-tool              # PostgreSQL/MySQL schema 初始化工具
temporal-cassandra-tool        # Cassandra schema 工具（本项目不用）
temporal-elasticsearch-tool    # ES schema 工具（可选）
tdbg                           # 调试工具
config/                        # 配置模板目录
  ├── docker.yaml              # Go 模板配置（通过环境变量渲染，与 auto-setup 镜像一致）
  ├── development-postgres12.yaml  # 静态 YAML 配置（直接写死参数，推荐裸机使用）
  ├── dynamicconfig/
  │   └── docker.yaml          # 动态配置（Worker 心跳、Update API 等）
  └── ...其他开发环境配置
```

### 1.2 ui-server 包内容

```
ui-server                     # Temporal UI Server 二进制（REST API + Web UI）
LICENSE
README.md
```

### 1.3 架构与兼容性验证

三个二进制经 `file` + `objdump` 验证：

```
ELF 64-bit LSB executable, x86-64, version 1 (SYSV), statically linked, stripped
无 NEEDED 动态依赖记录
```

- **静态链接**（CGO_ENABLED=0），不依赖 glibc
- **x86_64 架构**，与目标服务器匹配
- **内核 3.10 远超 Go 最低要求**，CentOS 7 可直接运行

---

## 二、系统依赖

### 2.1 二进制运行依赖

Temporal Server 和 UI Server 均为静态链接 Go 二进制，**无 RPM 依赖**，仅需以下系统组件：

| 依赖 | 是否必需 | 说明 | CentOS 7 状态 |
|------|---------|------|--------------|
| `ca-certificates` | 必需 | Go 读取 `/etc/pki/tls/certs/ca-bundle.crt` 校验 TLS 证书 | 通常已安装 |
| `tzdata` | 建议必需 | 时区数据（`/usr/share/zoneinfo`），Temporal 调度依赖 | 通常已安装 |
| `/etc/nsswitch.conf` | 已存在即可 | Go 纯 Go DNS 解析器读取 | 系统默认存在 |
| `/etc/resolv.conf` | 已存在即可 | DNS 配置 | 系统默认存在 |

**验证方法**：

```bash
# 检查 ca-certificates
ls -l /etc/pki/tls/certs/ca-bundle.crt
# 不存在则从 vault.centos.org 下载安装: yum localinstall ca-certificates-*.rpm

# 检查 tzdata
ls /usr/share/zoneinfo/Asia/Shanghai
# 不存在则从 vault.centos.org 下载安装: yum localinstall tzdata-*.rpm

# 检查时区设置
timedatectl status
# 建议设置为 Asia/Shanghai: timedatectl set-timezone Asia/Shanghai
```

### 2.2 PostgreSQL 前置条件

PostgreSQL 已通过 Docker 部署，需确认以下信息：

```bash
# 确认 PG 端口映射到宿主机
ss -tlnp | grep 5432
# 预期: 0.0.0.0:5432 LISTEN

# 确认连接信息（根据你的 docker-compose 配置）
# 用户名: temporal
# 密码: temporal
# 数据库: temporal（主库）
# 端口: 5432（宿主机映射）

# 测试连接
docker exec temporal-postgresql psql -U temporal -d temporal -c '\l'
```

> **注意**：如果 PG 尚未创建 `temporal_visibility` 数据库，需在初始化 schema 时创建（见 4.4 节）。

### 2.3 schema 文件获取

二进制包**不含** schema SQL 文件，需从 `temporalio/admin-tools` Docker 镜像提取（服务器已有 Docker）：

```bash
# 拉取 admin-tools 镜像（联网机操作，或离线导入）
docker pull temporalio/admin-tools:1.31.2

# 创建临时容器（不启动）
docker create --name temporal-extract temporalio/admin-tools:1.31.2

# 提取 schema 文件
docker cp temporal-extract:/etc/temporal/schema /etc/temporal/schema

# 清理临时容器
docker rm temporal-extract

# 验证
ls /etc/temporal/schema/postgresql/v12/
# 预期: temporal/ visibility/
```

> **离线服务器**：若无法 `docker pull`，在联网机执行后 `docker save` 导出，传到离线机 `docker load` 再提取。

---

## 三、安装顺序

```
1. 确认 PG 已通过 Docker 运行（端口 5432）
2. 安装 ca-certificates + tzdata（若缺失）
3. 提取 schema 文件（从 admin-tools 镜像）
4. 解压 temporal 二进制包，放置二进制 + 配置文件
5. 初始化 PG schema（temporal-sql-tool）
6. 配置 temporal-server systemd 服务，启动
7. 解压 ui-server 二进制包，放置二进制 + 配置文件
8. 配置 ui-server systemd 服务，启动
9. 验证部署
```

---

## 四、安装 Temporal Server v1.31.2

### 4.1 创建用户和目录

```bash
# 创建专用用户（不以 root 运行）
useradd -r -s /sbin/nologin temporal

# 创建目录
mkdir -p /etc/temporal/config/dynamicconfig
mkdir -p /var/lib/temporal
mkdir -p /var/log/temporal
```

### 4.2 解压并放置二进制

```bash
# 解压到临时目录
mkdir -p /tmp/temporal-install
tar -xzf /path/to/setup/packages/temporal_1.31.2_linux_amd64.tar.gz -C /tmp/temporal-install

# 放置二进制
cp /tmp/temporal-install/temporal-server    /usr/local/bin/
cp /tmp/temporal-install/temporal-sql-tool  /usr/local/bin/
cp /tmp/temporal-install/tdbg               /usr/local/bin/
chmod +x /usr/local/bin/temporal-server /usr/local/bin/temporal-sql-tool /usr/local/bin/tdbg

# 放置配置文件
cp /tmp/temporal-install/config/docker.yaml                       /etc/temporal/config/
cp /tmp/temporal-install/config/dynamicconfig/docker.yaml         /etc/temporal/config/dynamicconfig/

# 设置权限
chown -R temporal:temporal /etc/temporal /var/lib/temporal /var/log/temporal
```

### 4.3 配置文件

temporal-server 支持两种配置方式，**推荐方式 A**（与 Docker 镜像环境变量一致）。

#### 方式 A：Go 模板 + 环境变量（推荐，与 auto-setup 镜像行为一致）

配置文件 `/etc/temporal/config/docker.yaml` 已在 4.2 步复制到位，它是 Go 模板，通过环境变量渲染。systemd 服务文件中设置环境变量即可（见 4.5 节）。

关键环境变量：

| 环境变量 | 值 | 说明 |
|----------|-----|------|
| `DB` | `postgres12` | 数据库类型 |
| `DB_PORT` | `5432` | PG 端口 |
| `POSTGRES_SEEDS` | `127.0.0.1` | PG 地址（宿主机，因为 PG 在 Docker 中端口已映射） |
| `POSTGRES_USER` | `temporal` | PG 用户名 |
| `POSTGRES_PWD` | `temporal` | PG 密码 |
| `DBNAME` | `temporal` | 主库名 |
| `VISIBILITY_DBNAME` | `temporal_visibility` | 可见性库名 |
| `LOG_LEVEL` | `error,warn,info` | 日志级别 |
| `DYNAMIC_CONFIG_FILE_PATH` | `config/dynamicconfig/docker.yaml` | 动态配置路径 |
| `BIND_ON_IP` | `0.0.0.0` | 监听地址（允许远程连接） |

#### 方式 B：静态 YAML 配置（备选）

如不想用环境变量模板，可创建静态配置 `/etc/temporal/config/temporal-server.yaml`：

```yaml
log:
  stdout: true
  level: error,warn,info

persistence:
  defaultStore: postgres-default
  visibilityStore: postgres-visibility
  numHistoryShards: 4
  datastores:
    postgres-default:
      sql:
        pluginName: "postgres12"
        databaseName: "temporal"
        connectAddr: "127.0.0.1:5432"
        connectProtocol: "tcp"
        user: "temporal"
        password: "temporal"
        maxConns: 20
        maxIdleConns: 20
        maxConnLifetime: "1h"
    postgres-visibility:
      sql:
        pluginName: "postgres12"
        databaseName: "temporal_visibility"
        connectAddr: "127.0.0.1:5432"
        connectProtocol: "tcp"
        user: "temporal"
        password: "temporal"
        maxConns: 20
        maxIdleConns: 20
        maxConnLifetime: "1h"

global:
  membership:
    maxJoinDuration: 30s
  broadcastAddress: "127.0.0.1"

services:
  frontend:
    rpc:
      grpcPort: 7233
      membershipPort: 6933
      bindOnIP: "0.0.0.0"
  history:
    rpc:
      grpcPort: 7234
      membershipPort: 6934
      bindOnIP: "0.0.0.0"
  matching:
    rpc:
      grpcPort: 7235
      membershipPort: 6935
      bindOnIP: "0.0.0.0"
  worker:
    rpc:
      grpcPort: 7239
      membershipPort: 6939
      bindOnIP: "0.0.0.0"

dynamicConfig:
  filepath: ""
```

> 使用方式 B 时，systemd 中 `ExecStart` 改为 `temporal-server --env temporal-server start`，对应文件 `/etc/temporal/config/temporal-server.yaml`。

### 4.4 初始化数据库 Schema

```bash
# 1. 创建 visibility 数据库（主库 temporal 已由 Docker PG 创建）
docker exec temporal-postgresql psql -U temporal -c "CREATE DATABASE temporal_visibility OWNER temporal;"

# 2. 设置密码环境变量（temporal-sql-tool 通过 SQL_PASSWORD 读取密码）
export SQL_PASSWORD="temporal"

# 3. 初始化主库 schema
temporal-sql-tool \
    --plugin postgres12 \
    --ep 127.0.0.1 \
    -u temporal \
    -p 5432 \
    --db temporal \
    setup-schema -v 0.0

temporal-sql-tool \
    --plugin postgres12 \
    --ep 127.0.0.1 \
    -u temporal \
    -p 5432 \
    --db temporal \
    update-schema -d /etc/temporal/schema/postgresql/v12/temporal/versioned

# 4. 初始化 visibility 库 schema
temporal-sql-tool \
    --plugin postgres12 \
    --ep 127.0.0.1 \
    -u temporal \
    -p 5432 \
    --db temporal_visibility \
    setup-schema -v 0.0

temporal-sql-tool \
    --plugin postgres12 \
    --ep 127.0.0.1 \
    -u temporal \
    -p 5432 \
    --db temporal_visibility \
    update-schema -d /etc/temporal/schema/postgresql/v12/visibility/versioned

# 5. 验证表已创建
docker exec temporal-postgresql psql -U temporal -d temporal -c '\dt'
docker exec temporal-postgresql psql -U temporal -d temporal_visibility -c '\dt'

unset SQL_PASSWORD
```

> **注意**：如果 PG 的 `pg_hba.conf` 使用 `ident` 认证（Docker 部署通常用 `md5` 或 `trust`），`temporal-sql-tool` 会报认证失败。Docker 部署的 PG 默认允许密码登录，一般无需修改。

### 4.5 创建 systemd 服务

```bash
cat > /etc/systemd/system/temporal-server.service <<'EOF'
[Unit]
Description=Temporal Server v1.31.2
Documentation=https://docs.temporal.io
After=network.target docker.service
Requires=docker.service

[Service]
Type=simple
User=temporal
Group=temporal
Environment=TEMPORAL_HOME=/etc/temporal
WorkingDirectory=/etc/temporal

# 数据库配置（方式 A：Go 模板 + 环境变量）
Environment=DB=postgres12
Environment=DB_PORT=5432
Environment=POSTGRES_SEEDS=127.0.0.1
Environment=POSTGRES_USER=temporal
Environment=POSTGRES_PWD=temporal
Environment=DBNAME=temporal
Environment=VISIBILITY_DBNAME=temporal_visibility
Environment=LOG_LEVEL=error,warn,info
Environment=DYNAMIC_CONFIG_FILE_PATH=config/dynamicconfig/docker.yaml
Environment=BIND_ON_IP=0.0.0.0

ExecStart=/usr/local/bin/temporal-server --env docker start
Restart=on-failure
RestartSec=10
LimitNOFILE=65536

[Install]
WantedBy=multi-user.target
EOF

# 重新加载 systemd
systemctl daemon-reload
```

> **说明**：
> - `--env docker` 对应配置文件 `/etc/temporal/config/docker.yaml`（Go 模板，用环境变量渲染）
> - `After=docker.service` 确保 Docker（PG）先启动
> - `BIND_ON_IP=0.0.0.0` 允许 Worker 从其他机器连接

### 4.6 启动 Temporal Server

```bash
# 启动
systemctl start temporal-server
systemctl enable temporal-server

# 查看启动日志（等待 "Server ready"）
journalctl -u temporal-server -f

# 看到以下日志说明启动成功：
# INFO  startupLog {"message":"Server ready"}

# 检查状态
systemctl status temporal-server --no-pager

# 验证端口监听
ss -tlnp | grep 7233
# 预期: 0.0.0.0:7233 LISTEN
```

---

## 五、安装 UI Server v2.53.3

### 5.1 解压并放置二进制

```bash
# 解压到临时目录
mkdir -p /tmp/ui-server-install
tar -xzf /path/to/setup/packages/ui-server_2.53.3_linux_amd64.tar.gz -C /tmp/ui-server-install

# 放置二进制
cp /tmp/ui-server-install/ui-server /usr/local/bin/
chmod +x /usr/local/bin/ui-server

# 创建配置目录
mkdir -p /etc/temporal-ui/config
```

### 5.2 配置文件

UI Server 使用 Go 模板配置文件，通过环境变量渲染。创建 `/etc/temporal-ui/config/docker.yaml`：

```yaml
temporalGrpcAddress: {{ env "TEMPORAL_ADDRESS" | default "127.0.0.1:7233" }}
port: {{ env "TEMPORAL_UI_PORT" | default "8080" }}
enableUi: {{ env "TEMPORAL_UI_ENABLED" | default "true" }}
defaultNamespace: {{ env "TEMPORAL_DEFAULT_NAMESPACE" | default "default" }}
disableNewsFetch: {{ env "TEMPORAL_DISABLE_NEWS_FETCH" | default "true" }}
disableWriteActions: {{ env "TEMPORAL_DISABLE_WRITE_ACTIONS" | default "false" }}
workflowTerminateDisabled: {{ env "TEMPORAL_WORKFLOW_TERMINATE_DISABLED" | default "false" }}
workflowCancelDisabled: {{ env "TEMPORAL_WORKFLOW_CANCEL_DISABLED" | default "false" }}
workflowSignalDisabled: {{ env "TEMPORAL_WORKFLOW_SIGNAL_DISABLED" | default "false" }}
workflowUpdateDisabled: {{ env "TEMPORAL_WORKFLOW_UPDATE_DISABLED" | default "false" }}
workflowResetDisabled: {{ env "TEMPORAL_WORKFLOW_RESET_DISABLED" | default "false" }}
startWorkflowDisabled: {{ env "TEMPORAL_START_WORKFLOW_DISABLED" | default "false" }}
hideWorkflowQueryErrors: {{ env "TEMPORAL_HIDE_WORKFLOW_QUERY_ERRORS" | default "false" }}
cors:
  cookieInsecure: {{ env "TEMPORAL_CSRF_COOKIE_INSECURE" | default "true" }}
  unsafeAllowAllOrigins: {{ env "TEMPORAL_CORS_UNSAFE_ALLOW_ALL_ORIGINS" | default "true" }}
  allowOrigins:
    {{- if env "TEMPORAL_CORS_ORIGINS" }}
    {{- range env "TEMPORAL_CORS_ORIGINS" | split "," }}
    - "{{ . }}"
    {{- end }}
    {{- else }}
    - "http://localhost:8080"
    {{- end }}
auth:
  enabled: {{ env "TEMPORAL_AUTH_ENABLED" | default "false" }}
codec:
  endpoint: {{ env "TEMPORAL_CODEC_ENDPOINT" | default "" }}
hideLogs: {{ env "TEMPORAL_HIDE_LOGS" | default "false" }}
```

### 5.3 创建 systemd 服务

```bash
cat > /etc/systemd/system/temporal-ui.service <<'EOF'
[Unit]
Description=Temporal UI Server v2.53.3
Documentation=https://docs.temporal.io
After=network.target temporal-server.service
Requires=temporal-server.service

[Service]
Type=simple
User=temporal
Group=temporal
WorkingDirectory=/etc/temporal-ui

Environment=TEMPORAL_ADDRESS=127.0.0.1:7233
Environment=TEMPORAL_UI_PORT=8080
Environment=TEMPORAL_UI_ENABLED=true
Environment=TEMPORAL_DEFAULT_NAMESPACE=default
Environment=TEMPORAL_AUTH_ENABLED=false
Environment=TEMPORAL_DISABLE_WRITE_ACTIONS=false
Environment=TEMPORAL_CSRF_COOKIE_INSECURE=true
Environment=TEMPORAL_CORS_UNSAFE_ALLOW_ALL_ORIGINS=true
Environment=TEMPORAL_CORS_ORIGINS=http://localhost:8080,http://localhost:5173

ExecStart=/usr/local/bin/ui-server --env docker start
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF

# 重新加载 systemd
systemctl daemon-reload
```

> **关键说明**：
> - `TEMPORAL_ADDRESS=127.0.0.1:7233` 指向同机的 Temporal Server
> - `TEMPORAL_CORS_UNSAFE_ALLOW_ALL_ORIGINS=true` 允许所有跨域（开发用，**生产环境应改用具体域名**）
> - `TEMPORAL_CSRF_COOKIE_INSECURE=true` HTTP 下需要（无 HTTPS 时）
> - `After=temporal-server.service` 确保 Temporal Server 先启动

### 5.4 启动 UI Server

```bash
# 启动
systemctl start temporal-ui
systemctl enable temporal-ui

# 查看日志
journalctl -u temporal-ui -f

# 检查状态
systemctl status temporal-ui --no-pager

# 验证端口监听
ss -tlnp | grep 8080
# 预期: 0.0.0.0:8080 LISTEN
```

---

## 六、验证部署

### 6.1 访问 Web UI

```bash
# 获取服务器 IP
hostname -I
```

在浏览器打开：`http://<服务器IP>:8080`

看到 Temporal Web UI 界面即说明部署成功。

### 6.2 验证 Temporal Server 连通性

```bash
# 检查集群健康状态（通过 UI Server 的 REST API）
curl -s http://127.0.0.1:8080/api/v1/cluster | python -m json.tool
# 预期: 返回 JSON，包含 clusterName、hostInfo 等

# 检查 gRPC 端口
ss -tlnp | grep -E '7233|7234|7235|7239'
# 预期: 四个端口均 LISTEN
```

### 6.3 验证数据库

```bash
# 检查 temporal 库表
docker exec temporal-postgresql psql -U temporal -d temporal -c '\dt'
# 预期: executions, tasks, namespace_registry 等多张表

# 检查 temporal_visibility 库表
docker exec temporal-postgresql psql -U temporal -d temporal_visibility -c '\dt'
# 预期: executions 等表
```

### 6.4 防火墙配置

```bash
# 放行 Temporal Server gRPC 端口（Worker 连接用）
firewall-cmd --add-port=7233/tcp --permanent

# 放行 Web UI 端口（浏览器访问）
firewall-cmd --add-port=8080/tcp --permanent

firewall-cmd --reload
```

---

## 七、systemd 服务管理

### 7.1 日常操作

```bash
# Temporal Server
systemctl start    temporal-server
systemctl stop     temporal-server
systemctl restart  temporal-server
systemctl status   temporal-server

# Temporal UI Server
systemctl start    temporal-ui
systemctl stop     temporal-ui
systemctl restart  temporal-ui
systemctl status   temporal-ui
```

### 7.2 日志查看

```bash
# Temporal Server 日志
journalctl -u temporal-server -f                # 实时日志
journalctl -u temporal-server --since today     # 今天的日志
journalctl -u temporal-server -n 200             # 最近 200 行

# Temporal UI Server 日志
journalctl -u temporal-ui -f
journalctl -u temporal-ui -n 200
```

### 7.3 修改配置后重启

```bash
# 修改 temporal 动态配置
vi /etc/temporal/config/dynamicconfig/docker.yaml
systemctl restart temporal-server

# 修改 temporal-server 环境变量
vi /etc/systemd/system/temporal-server.service
systemctl daemon-reload
systemctl restart temporal-server

# 修改 ui-server 环境变量
vi /etc/systemd/system/temporal-ui.service
systemctl daemon-reload
systemctl restart temporal-ui
```

---

## 八、常见问题

### 8.1 Temporal Server 启动失败

| 日志关键词 | 原因 | 解决 |
|-----------|------|------|
| `connection refused 127.0.0.1:5432` | Docker PG 未启动或端口未映射 | `docker start temporal-postgresql`；检查 `ss -tlnp \| grep 5432` |
| `authentication failed for user "temporal"` | 密码不匹配 | 检查 systemd 中 `POSTGRES_PWD` 与 Docker PG 的 `POSTGRES_PASSWORD` 一致 |
| `database "temporal" does not exist` | 主库未创建 | `docker exec temporal-postgresql psql -U temporal -c "CREATE DATABASE temporal OWNER temporal;"` |
| `database "temporal_visibility" does not exist` | 可见性库未创建 | `docker exec temporal-postgresql psql -U temporal -c "CREATE DATABASE temporal_visibility OWNER temporal;"` |
| `relation "schema_version" does not exist` | schema 未初始化 | 执行 4.4 节 schema 初始化步骤 |
| `schema version mismatch` | schema 版本不匹配 | 重新执行 `update-schema` |
| `Address already in use :7233` | 端口被占用 | `ss -tlnp \| grep 7233` 查找占用进程，`kill` 或换端口 |

### 8.2 UI Server 启动失败

| 日志关键词 | 原因 | 解决 |
|-----------|------|------|
| `connection refused 127.0.0.1:7233` | Temporal Server 未启动 | `systemctl start temporal-server`；等待 "Server ready" |
| `Address already in use :8080` | 端口被占用 | `ss -tlnp \| grep 8080` 查找占用进程，或改 `TEMPORAL_UI_PORT` |
| `x509: certificate signed by unknown authority` | ca-certificates 缺失 | `yum install ca-certificates` |
| UI 白屏 | CORS 配置问题 | 确认 `TEMPORAL_CORS_UNSAFE_ALLOW_ALL_ORIGINS=true`（开发环境） |

### 8.3 Docker PG 连接问题

| 问题 | 原因 | 解决 |
|------|------|------|
| temporal-server 连不上 PG | PG 在 Docker 网络内，宿主机 127.0.0.1 不可达 | 确认 PG 端口已映射 `-p 5432:5432`；用 `127.0.0.1` 而非容器名 |
| temporal-sql-tool 认证失败 | Docker PG 的 pg_hba.conf 配置 | Docker PG 默认 `trust` 或 `md5`，一般无需修改；如需：`docker exec temporal-postgresql bash -c "echo 'host all all 0.0.0.0/0 md5' >> /var/lib/postgresql/data/pg_hba.conf && pg_ctl reload"` |

### 8.4 schema 初始化问题

| 问题 | 原因 | 解决 |
|------|------|------|
| `temporal-sql-tool: command not found` | 二进制未放置或无执行权限 | `cp temporal-sql-tool /usr/local/bin/ && chmod +x /usr/local/bin/temporal-sql-tool` |
| `No such file or directory: schema/postgresql/v12/...` | schema 文件未提取 | 执行 2.3 节从 admin-tools 镜像提取 schema |
| `SQL_PASSWORD not set` | 密码环境变量未设置 | `export SQL_PASSWORD="temporal"` |

### 8.5 CentOS 7 特定问题

| 问题 | 原因 | 解决 |
|------|------|------|
| `cannot execute binary file` | 架构不匹配（如在 arm64 上运行 amd64 二进制） | 确认 `uname -m` 输出 `x86_64` |
| 二进制启动极慢 | SELinux 对非标准路径二进制做安全扫描 | `setenforce 0`（临时关闭）或 `chcon -t bin_t /usr/local/bin/temporal-server` |
| systemd 服务无法启动 | SELinux 拒绝 temporal 用户执行 | `chcon -t bin_t /usr/local/bin/temporal-server /usr/local/bin/ui-server` |
| 日志时间不对 | 时区未设置 | `timedatectl set-timezone Asia/Shanghai` |

---

## 附录：端口与连接关系

```
                    ┌─────────────────────────────────────┐
                    │       CentOS 7 宿主机               │
                    │                                     │
  浏览器 ──────────► │  UI Server (:8080)                 │
                    │    │                                │
                    │    │ 127.0.0.1:7233                 │
                    │    ▼                                │
                    │  Temporal Server (:7233 gRPC)       │
                    │    │                                │
                    │    │ 127.0.0.1:5432                 │
                    │    ▼                                │
                    │  Docker: PostgreSQL (:5432)         │
                    │    └─ temporal 库（工作流状态）      │
                    │    └─ temporal_visibility 库（搜索）│
                    └─────────────────────────────────────┘

  Worker ──────────► :7233 (gRPC，可远程连接)
```

| 组件 | 监听端口 | 连接目标 | 连接地址 |
|------|---------|----------|----------|
| Temporal Server | 7233 (gRPC) | Docker PG | 127.0.0.1:5432 |
| UI Server | 8080 (HTTP) | Temporal Server | 127.0.0.1:7233 |
| Worker | - | Temporal Server | <服务器IP>:7233 |
| 浏览器 | - | UI Server | <服务器IP>:8080 |
