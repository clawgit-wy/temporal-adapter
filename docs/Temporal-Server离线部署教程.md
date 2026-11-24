# Temporal Server + PostgreSQL 离线部署教程（二进制安装）

> **适用场景**：在联网 CentOS 服务器上下载并打包全部安装资源，传输至离线（无外网）CentOS 服务器完成 Temporal Server + PostgreSQL 的安装部署。
>
> **安装方式**：Temporal Server 和 PostgreSQL 使用**二进制/RPM 原生安装**（非 Docker），Temporal Web UI 和 Worker 使用 **Docker 镜像**部署。
>
> **环境要求**：CentOS 7 / CentOS 8（Stream）/ Rocky Linux 8/9，root 权限，建议 8核16G+。

---

## 目录

- [一、总体思路](#一总体思路)
- [二、联网 CentOS：准备离线安装包](#二联网-centos准备离线安装包)
  - [2.1 下载 Docker 及其依赖 RPM（UI/Worker 用）](#21-下载-docker-及其依赖-rpmuiworker-用)
  - [2.2 下载 docker-compose 二进制](#22-下载-docker-compose-二进制)
  - [2.3 下载 PostgreSQL 15 RPM 及依赖](#23-下载-postgresql-15-rpm-及依赖)
  - [2.4 下载 Temporal Server 二进制包](#24-下载-temporal-server-二进制包)
  - [2.5 从 Docker 镜像提取 schema 文件和工具](#25-从-docker-镜像提取-schema-文件和工具)
  - [2.6 拉取 Temporal UI Docker 镜像并导出](#26-拉取-temporal-ui-docker-镜像并导出)
  - [2.7 准备配置文件与 systemd 服务](#27-准备配置文件与-systemd-服务)
  - [2.8 打包离线安装包](#28-打包离线安装包)
- [三、传输至离线服务器](#三传输至离线服务器)
- [四、离线 CentOS：安装部署](#四离线-centos安装部署)
  - [4.1 安装 Docker（为 UI/Worker 准备）](#41-安装-docker为-uiworker-准备)
  - [4.2 安装 docker-compose](#42-安装-docker-compose)
  - [4.3 安装 PostgreSQL 15](#43-安装-postgresql-15)
  - [4.4 安装 Temporal Server 二进制](#44-安装-temporal-server-二进制)
  - [4.5 初始化数据库 Schema](#45-初始化数据库-schema)
  - [4.6 启动 Temporal Server](#46-启动-temporal-server)
  - [4.7 启动 Temporal Web UI（Docker）](#47-启动-temporal-web-uidocker)
  - [4.8 验证部署](#48-验证部署)
- [五、日常运维操作](#五日常运维操作)
- [六、常见问题排查](#六常见问题排查)

---

## 一、总体思路

```
┌──────────────────────────────────────┐        ┌──────────────────────────────────────┐
│        联网 CentOS（下载机）           │        │        离线 CentOS（生产机）           │
│                                      │        │                                      │
│  1. 下载 Docker RPM（UI/Worker 用）   │        │  1. 安装 Docker RPM                   │
│  2. 下载 PostgreSQL 15 RPM + 依赖     │  拷贝   │  2. 安装 PostgreSQL 15 RPM            │
│  3. 下载 Temporal Server 二进制包     │ ────>  │  3. 放置 temporal-server 二进制       │
│  4. 从镜像提取 schema + sql-tool      │  U盘/  │  4. 初始化 PG 数据库 + schema          │
│  5. 拉取 UI Docker 镜像导出 tar       │  scp   │  5. 配置 + systemd 启动 temporal      │
│  6. 准备配置文件 + systemd 服务文件    │        │  6. docker run 启动 UI                │
│  7. 统一打包成 tar.gz                 │        │  7. 验证 UI + Server 可访问           │
│                                      │        │                                      │
└──────────────────────────────────────┘        └──────────────────────────────────────┘

安装方式划分：
  Temporal Server  → 二进制安装 + systemd 管理（原生进程，性能最优）
  PostgreSQL       → RPM 包安装 + systemd 管理（原生数据库，便于调优）
  Temporal Web UI  → Docker 容器（轻量，无状态）
  Worker           → Docker 容器（便于扩缩容，参考 Worker 扩缩容方案）
```

**核心原则**：控制面（Temporal Server + DB）用原生安装保证性能和稳定性；数据面（UI + Worker）用 Docker 便于弹性管理。

---

## 二、联网 CentOS：准备离线安装包

> 以下操作均在**联网的 CentOS** 上以 root 用户执行。
>
> 先安装 Docker（联网机上需要 Docker 来拉取 UI 镜像和提取 schema 文件）：
> ```bash
> yum install -y yum-utils
> yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
> yum install -y docker-ce docker-ce-cli containerd.io
> systemctl start docker
> ```

### 2.1 下载 Docker 及其依赖 RPM（UI/Worker 用）

```bash
# 创建存放 Docker RPM 的目录
mkdir -p /tmp/temporal-offline/docker-rpms

# 下载 docker-ce 及其全部依赖（--resolve 自动解析依赖）
yumdownloader --resolve --destdir=/tmp/temporal-offline/docker-rpms \
    docker-ce \
    docker-ce-cli \
    containerd.io \
    docker-compose-plugin

# 验证下载结果（应有多个 .rpm 文件）
ls -lh /tmp/temporal-offline/docker-rpms/
```

> **说明**：这些 RPM 用于在离线服务器上安装 Docker，以便运行 UI 和 Worker 容器。

---

### 2.2 下载 docker-compose 二进制

```bash
# 创建目录
mkdir -p /tmp/temporal-offline/bin

# 下载独立版 docker-compose 二进制（x86_64）
curl -L "https://github.com/docker/compose/releases/download/v2.29.2/docker-compose-linux-x86_64" \
    -o /tmp/temporal-offline/bin/docker-compose

chmod +x /tmp/temporal-offline/bin/docker-compose
```

---

### 2.3 下载 PostgreSQL 15 RPM 及依赖

Temporal Server 要求 PostgreSQL 12+，本方案使用 PostgreSQL 15。

#### 2.3.1 配置 PostgreSQL 官方 yum 源

```bash
# 根据联网机的系统版本选择对应的 repo RPM
# CentOS 7:
yum install -y https://download.postgresql.org/pub/repos/yum/reporpms/EL-7-x86_64/pgdg-redhat-repo-latest.noarch.rpm

# CentOS 8 / Rocky 8:
# yum install -y https://download.postgresql.org/pub/repos/yum/reporpms/EL-8-x86_64/pgdg-redhat-repo-latest.noarch.rpm

# CentOS 9 / Rocky 9:
# yum install -y https://download.postgresql.org/pub/repos/yum/reporpms/EL-9-x86_64/pgdg-redhat-repo-latest.noarch.rpm
```

> **提示**：如果提示 `does not update installed package` 或 `Nothing to do`，说明 repo 已安装过，直接继续下一步即可。

> **CentOS 7 常见问题**：`yum makecache` 时报 `repomd.xml signature could not be verified for pgdg-common`。这是因为 pgdg 仓库开启了**元数据签名验证**（`repo_gpgcheck=1`），需同时关闭 `gpgcheck` 和 `repo_gpgcheck`：
>
> - `gpgcheck`：验证 RPM 包的 GPG 签名
> - `repo_gpgcheck`：验证仓库元数据（repomd.xml）的 GPG 签名 ← **报错根源**

```bash
# 关闭所有 pgdg 仓库的包签名验证 + 元数据签名验证
# 注意：pgdg repo 文件中 repo_gpgcheck 的等号两侧带空格，需精确匹配
sed -i 's/gpgcheck=1/gpgcheck=0/g' /etc/yum.repos.d/pgdg-*.repo
sed -i 's/repo_gpgcheck = 1/repo_gpgcheck = 0/g' /etc/yum.repos.d/pgdg-*.repo

# 禁用不需要的 PostgreSQL 仓库（CentOS 7 上 pgdg16/pgdg17 等高版本仓库 404，只用 pgdg15）
yum-config-manager --disable pgdg16 pgdg17 2>/dev/null || true

# 验证修改结果（两个值都应为 0）
grep -E "gpgcheck|repo_gpgcheck" /etc/yum.repos.d/pgdg-*.repo

# 清理旧缓存后重新刷新
yum clean all
yum makecache
```

#### 2.3.2 下载 PostgreSQL 15 RPM 及全部依赖

```bash
# 创建存放 PG RPM 的目录
mkdir -p /tmp/temporal-offline/pg-rpms

# 下载 PostgreSQL 15 服务端和客户端及其全部依赖
yumdownloader --resolve --destdir=/tmp/temporal-offline/pg-rpms \
    postgresql15-server \
    postgresql15

# 验证下载结果
ls -lh /tmp/temporal-offline/pg-rpms/
```

> **说明**：`yumdownloader --resolve` 会自动解析并下载所有依赖包（如 `libpq`、`postgresql-libs` 等）。

---

### 2.4 下载 Temporal Server 二进制包

Temporal 官方在 GitHub Releases 中提供预编译的 Linux 二进制包。

```bash
# 创建目录
mkdir -p /tmp/temporal-offline/temporal-bin

# 下载 Temporal Server v1.24.2 二进制包
curl -OL "https://github.com/temporalio/temporal/releases/download/v1.24.2/temporal_1.24.2_linux_amd64.tar.gz" \
    -o /tmp/temporal-offline/temporal-bin/temporal_1.24.2_linux_amd64.tar.gz

# 解压
cd /tmp/temporal-offline/temporal-bin
tar -xzf temporal_1.24.2_linux_amd64.tar.gz

# 查看解压后的文件
ls -lh
# 预期看到 temporal-server 二进制文件（可能还包含 temporal-sql-tool）

# 清理 tar 包
rm -f temporal_1.24.2_linux_amd64.tar.gz
```

> **二进制说明**：
> - `temporal-server`：Temporal Server 核心二进制，包含 frontend/history/matching/worker 全部服务
> - 无需 Go 运行时，静态编译二进制可直接运行

---

### 2.5 从 Docker 镜像提取 schema 文件和工具

Temporal Server 二进制包不包含数据库 schema 文件和 `temporal-sql-tool` 工具，需要从 Docker 镜像中提取。

```bash
# 创建提取目录
mkdir -p /tmp/temporal-offline/temporal-schema

# 拉取 auto-setup 镜像（内含 schema 文件和工具二进制）
docker pull temporalio/auto-setup:1.24.2

# 创建临时容器（不启动）
docker create --name temporal-extract temporalio/auto-setup:1.24.2

# 提取 schema 文件目录（包含 PostgreSQL/MySQL 等全部建表 SQL）
docker cp temporal-extract:/etc/temporal/schema /tmp/temporal-offline/temporal-schema/

# 提取 temporal-sql-tool 二进制（用于初始化数据库 schema）
docker cp temporal-extract:/usr/local/bin/temporal-sql-tool /tmp/temporal-offline/temporal-bin/

# 提取 temporal CLI 二进制（用于运维操作，可选）
docker cp temporal-extract:/usr/local/bin/temporal /tmp/temporal-offline/temporal-bin/

# 清理临时容器
docker rm temporal-extract

# 验证提取结果
ls -lh /tmp/temporal-offline/temporal-schema/schema/
# 预期看到 postgresql/、mysql/ 等目录

ls -lh /tmp/temporal-offline/temporal-bin/
# 预期看到 temporal-server、temporal-sql-tool、temporal
```

> **提取的文件用途**：
> | 文件/目录 | 用途 |
> | --- | --- |
> | `schema/postgresql/v12/temporal/versioned/` | Temporal 主库建表 SQL（按版本递增） |
> | `schema/postgresql/v12/visibility/versioned/` | Visibility 库建表 SQL |
> | `temporal-sql-tool` | 执行建库、建表、版本管理的命令行工具 |
> | `temporal` | Temporal CLI，用于运维（注册 namespace 等） |

---

### 2.6 拉取 Temporal UI Docker 镜像并导出

```bash
# 创建镜像存放目录
mkdir -p /tmp/temporal-offline/images

# 拉取 Temporal Web UI 镜像
docker pull temporalio/ui:2.28.0

# 导出为 tar 包
docker save -o /tmp/temporal-offline/images/temporal-ui-2.28.0.tar temporalio/ui:2.28.0

# 验证
ls -lh /tmp/temporal-offline/images/
```

---

### 2.7 准备配置文件与 systemd 服务

#### 2.7.1 Temporal Server 配置文件

创建 `/tmp/temporal-offline/config/temporal-server.yaml`：

```bash
mkdir -p /tmp/temporal-offline/config
```

写入以下内容：

```yaml
# Temporal Server 配置文件 - PostgreSQL 持久化
# 此文件放置于离线服务器 /etc/temporal/config/temporal-server.yaml

log:
  stdout: true
  level: info

persistence:
  # 默认存储（工作流状态、事件历史）
  defaultStore: postgres-default
  # 可见性存储（工作流搜索、列表）
  visibilityStore: postgres-visibility
  # 历史分片数（创建后不可更改，开发环境 4，生产建议 128~512）
  numHistoryShards: 4
  datastores:
    # ==================== 主库 ====================
    postgres-default:
      sql:
        pluginName: "postgres"
        databaseName: "temporal"
        connectAddr: "127.0.0.1:5432"
        connectProtocol: "tcp"
        user: "temporal"
        password: "temporal123"
        maxConns: 20
        maxIdleConns: 20
        maxConnLifetime: "1h"
    # ==================== 可见性库 ====================
    postgres-visibility:
      sql:
        pluginName: "postgres"
        databaseName: "temporal_visibility"
        connectAddr: "127.0.0.1:5432"
        connectProtocol: "tcp"
        user: "temporal"
        password: "temporal123"
        maxConns: 20
        maxIdleConns: 20
        maxConnLifetime: "1h"

global:
  membership:
    maxJoinDuration: 30s
  broadcastAddress: "127.0.0.1"

# 服务监听配置
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

# 动态配置（开发环境用，生产可关闭）
dynamicConfig:
  filepath: ""
```

> **关键参数说明**：
> | 参数 | 说明 |
> | --- | --- |
> | `numHistoryShards` | 历史分片数，**创建后不可更改**。开发 4，小规模生产 128，中规模 512 |
> | `connectAddr` | PostgreSQL 地址。Temporal Server 与 DB 同机部署用 `127.0.0.1:5432` |
> | `grpcPort: 7233` | frontend 端口，Worker 和客户端通过此端口连接 |
> | `bindOnIP: "0.0.0.0"` | 监听所有网卡，允许远程连接 |

#### 2.7.2 systemd 服务文件

创建 `/tmp/temporal-offline/config/temporal-server.service`：

```ini
[Unit]
Description=Temporal Server
Documentation=https://docs.temporal.io
After=network.target postgresql-15.service
Requires=postgresql-15.service

[Service]
Type=simple
User=temporal
Group=temporal
Environment=TEMPORAL_HOME=/etc/temporal
WorkingDirectory=/etc/temporal
# 指定配置文件环境名称（对应 /etc/temporal/config/<env>.yaml）
ExecStart=/usr/local/bin/temporal-server --env temporal-server start
Restart=on-failure
RestartSec=10
LimitNOFILE=65536

[Install]
WantedBy=multi-user.target
```

> **说明**：
> - `--env temporal-server` 对应配置文件路径 `/etc/temporal/config/temporal-server.yaml`
> - `After=postgresql-15.service` 确保数据库先启动
> - `User=temporal` 以非 root 用户运行，更安全

#### 2.7.3 数据库初始化脚本

创建 `/tmp/temporal-offline/config/init-temporal-db.sh`：

```bash
#!/bin/bash
# Temporal 数据库初始化脚本
# 在 PostgreSQL 安装并启动后执行

set -e

PG_USER="temporal"
PG_PASS="temporal123"
PG_HOST="127.0.0.1"
PG_PORT="5432"

# temporal-sql-tool 和 schema 文件路径
SQL_TOOL="/usr/local/bin/temporal-sql-tool"
SCHEMA_DIR="/etc/temporal/schema"

echo "=========================================="
echo "  Temporal 数据库初始化"
echo "=========================================="

# 1. 创建数据库用户和数据库（通过 psql）
echo "[1/4] 创建数据库用户和数据库..."
su - postgres -c "psql -c \"CREATE USER ${PG_USER} WITH PASSWORD '${PG_PASS}';\"" 2>/dev/null || true
su - postgres -c "psql -c \"CREATE DATABASE temporal OWNER ${PG_USER};\"" 2>/dev/null || true
su - postgres -c "psql -c \"CREATE DATABASE temporal_visibility OWNER ${PG_USER};\"" 2>/dev/null || true

# 2. 授权
echo "[2/4] 授权..."
su - postgres -c "psql -c \"GRANT ALL PRIVILEGES ON DATABASE temporal TO ${PG_USER};\""
su - postgres -c "psql -c \"GRANT ALL PRIVILEGES ON DATABASE temporal_visibility TO ${PG_USER};\""

# 3. 初始化 temporal 主库 schema
echo "[3/4] 初始化 temporal 主库 schema..."
export PGPASSWORD="${PG_PASS}"
${SQL_TOOL} \
    --plugin postgres12 \
    --ep ${PG_HOST} \
    -u ${PG_USER} \
    -p ${PG_PORT} \
    --db temporal \
    setup-schema -v 0.0

${SQL_TOOL} \
    --plugin postgres12 \
    --ep ${PG_HOST} \
    -u ${PG_USER} \
    -p ${PG_PORT} \
    --db temporal \
    update-schema -schema-dir ${SCHEMA_DIR}/postgresql/v12/temporal/versioned

# 4. 初始化 visibility 库 schema
echo "[4/4] 初始化 visibility 库 schema..."
${SQL_TOOL} \
    --plugin postgres12 \
    --ep ${PG_HOST} \
    -u ${PG_USER} \
    -p ${PG_PORT} \
    --db temporal_visibility \
    setup-schema -v 0.0

${SQL_TOOL} \
    --plugin postgres12 \
    --ep ${PG_HOST} \
    -u ${PG_USER} \
    -p ${PG_PORT} \
    --db temporal_visibility \
    update-schema -schema-dir ${SCHEMA_DIR}/postgresql/v12/visibility/versioned

unset PGPASSWORD

echo "=========================================="
echo "  Temporal 数据库初始化完成！"
echo "=========================================="
```

```bash
chmod +x /tmp/temporal-offline/config/init-temporal-db.sh
```

#### 2.7.4 UI Docker 启动脚本

创建 `/tmp/temporal-offline/config/start-temporal-ui.sh`：

```bash
#!/bin/bash
# Temporal Web UI Docker 容器启动脚本

# 检查镜像是否已加载
if ! docker images | grep -q "temporalio/ui.*2.28.0"; then
    echo "错误：temporalio/ui:2.28.0 镜像未找到，请先执行 docker load"
    exit 1
fi

# 停止并删除旧容器（如有）
docker rm -f temporal-ui 2>/dev/null || true

# 启动 UI 容器
# TEMPORAL_ADDRESS 指向宿主机上的 Temporal Server（非 Docker 网络内）
docker run -d \
    --name temporal-ui \
    --restart always \
    --add-host=host.docker.internal:host-gateway \
    -p 8233:8080 \
    -e TEMPORAL_ADDRESS=host.docker.internal:7233 \
    temporalio/ui:2.28.0

echo "Temporal Web UI 已启动，访问 http://<服务器IP>:8233"
```

```bash
chmod +x /tmp/temporal-offline/config/start-temporal-ui.sh
```

> **关键说明**：`TEMPORAL_ADDRESS=host.docker.internal:7233` 使 UI 容器通过 Docker 宿主机网关连接到原生安装的 Temporal Server（7233 端口），而非容器内部网络。

---

### 2.8 打包离线安装包

```bash
# 查看完整目录结构
cd /tmp/temporal-offline
find . -type f | sort

# 预期结构：
# .
# ├── bin/
# │   └── docker-compose
# ├── config/
# │   ├── temporal-server.yaml          # Temporal Server 配置
# │   ├── temporal-server.service       # systemd 服务文件
# │   ├── init-temporal-db.sh           # 数据库初始化脚本
# │   └── start-temporal-ui.sh          # UI 启动脚本
# ├── docker-rpms/
# │   ├── containerd.io-xxx.rpm
# │   ├── docker-ce-xxx.rpm
# │   ├── docker-ce-cli-xxx.rpm
# │   ├── docker-compose-plugin-xxx.rpm
# │   └── (其他依赖 rpm)
# ├── pg-rpms/
# │   ├── postgresql15-xxx.rpm
# │   ├── postgresql15-server-xxx.rpm
# │   └── (其他依赖 rpm)
# ├── temporal-bin/
# │   ├── temporal-server               # Temporal Server 二进制
# │   ├── temporal-sql-tool             # 数据库工具
# │   └── temporal                      # CLI 工具
# ├── temporal-schema/
# │   └── schema/
# │       ├── postgresql/
# │       │   └── v12/
# │       │       ├── temporal/
# │       │       │   └── versioned/    # 主库建表 SQL
# │       │       └── visibility/
# │       │           └── versioned/    # 可见性库建表 SQL
# │       └── mysql/
# │           └── ...
# └── images/
#     └── temporal-ui-2.28.0.tar        # UI Docker 镜像

# 打包
cd /tmp
tar -czf temporal-offline-package.tar.gz temporal-offline/

# 查看包大小
ls -lh /tmp/temporal-offline-package.tar.gz
```

> 包大小约 **1.5~2 GB**。

---

## 三、传输至离线服务器

### 方式一：scp 直传（两台机器网络可达）

```bash
scp /tmp/temporal-offline-package.tar.gz root@<离线服务器IP>:/opt/
```

### 方式二：U盘 / 移动硬盘（完全物理隔离）

```bash
# 联网机拷贝到 U盘
cp /tmp/temporal-offline-package.tar.gz /media/usb/

# U盘插到离线机，拷贝到 /opt/
cp /media/usb/temporal-offline-package.tar.gz /opt/
```

### 离线机解压

```bash
cd /opt
tar -xzf temporal-offline-package.tar.gz
ls -la /opt/temporal-offline/
```

---

## 四、离线 CentOS：安装部署

> 以下操作均在**离线 CentOS** 上以 root 用户执行。

### 4.1 安装 Docker（为 UI/Worker 准备）

#### 4.1.1 清理旧版本（如有）

```bash
yum remove -y docker docker-client docker-client-latest docker-common \
    docker-latest docker-latest-logrotate docker-logrotate docker-engine 2>/dev/null || true
```

#### 4.1.2 本地安装 RPM 包

```bash
cd /opt/temporal-offline/docker-rpms
yum localinstall -y *.rpm
```

#### 4.1.3 启动 Docker

```bash
systemctl start docker
systemctl enable docker
docker version
```

---

### 4.2 安装 docker-compose

```bash
cp /opt/temporal-offline/bin/docker-compose /usr/local/bin/
chmod +x /usr/local/bin/docker-compose
docker-compose version
```

---

### 4.3 安装 PostgreSQL 15

#### 4.3.1 本地安装 RPM 包

```bash
cd /opt/temporal-offline/pg-rpms
yum localinstall -y *.rpm
```

#### 4.3.2 初始化数据库

```bash
# 初始化 PostgreSQL 数据目录
/usr/pgsql-15/bin/postgresql-15-setup initdb
```

#### 4.3.3 配置允许本地连接

编辑 `/var/lib/pgsql/15/data/pg_hba.conf`，在文件末尾添加或修改：

```bash
# 找到 # IPv4 local connections: 部分，修改为 md5 认证
# 原：host    all    all    127.0.0.1/32    ident
# 改为：
host    all    all    127.0.0.1/32    md5
host    all    all    0.0.0.0/0        md5
```

可通过以下命令直接修改：

```bash
# 备份原文件
cp /var/lib/pgsql/15/data/pg_hba.conf /var/lib/pgsql/15/data/pg_hba.conf.bak

# 将 ident 认证改为 md5 认证（允许密码登录）
sed -i 's/ident/md5/g' /var/lib/pgsql/15/data/pg_hba.conf

# 添加远程访问（如果 Worker 在其他机器上需要连 DB，可选）
echo "host    all    all    0.0.0.0/0    md5" >> /var/lib/pgsql/15/data/pg_hba.conf
```

#### 4.3.4 启动 PostgreSQL

```bash
systemctl start postgresql-15
systemctl enable postgresql-15

# 验证
systemctl status postgresql-15 --no-pager
```

#### 4.3.5 验证 PostgreSQL

```bash
su - postgres -c "psql -c '\l'"
```

预期输出列出默认的 `postgres`、`template0`、`template1` 数据库。

---

### 4.4 安装 Temporal Server 二进制

#### 4.4.1 创建 temporal 用户和目录

```bash
# 创建专用用户（不以 root 运行 Temporal Server）
useradd -r -s /sbin/nologin temporal

# 创建配置目录
mkdir -p /etc/temporal/config

# 创建数据目录
mkdir -p /var/lib/temporal
```

#### 4.4.2 放置二进制文件

```bash
# 复制 temporal-server 主程序
cp /opt/temporal-offline/temporal-bin/temporal-server /usr/local/bin/
chmod +x /usr/local/bin/temporal-server

# 复制 temporal-sql-tool 工具
cp /opt/temporal-offline/temporal-bin/temporal-sql-tool /usr/local/bin/
chmod +x /usr/local/bin/temporal-sql-tool

# 复制 temporal CLI 工具
cp /opt/temporal-offline/temporal-bin/temporal /usr/local/bin/
chmod +x /usr/local/bin/temporal

# 验证
ls -lh /usr/local/bin/temporal*
```

#### 4.4.3 放置 schema 文件

```bash
# 复制 schema 目录到 Temporal 配置目录
cp -r /opt/temporal-offline/temporal-schema/schema /etc/temporal/

# 验证
ls /etc/temporal/schema/postgresql/v12/
# 预期看到 temporal/ 和 visibility/ 目录
```

#### 4.4.4 放置配置文件

```bash
# 复制 Temporal Server 配置文件
cp /opt/temporal-offline/config/temporal-server.yaml /etc/temporal/config/

# 设置权限
chown -R temporal:temporal /etc/temporal
```

#### 4.4.5 安装 systemd 服务

```bash
# 复制 systemd 服务文件
cp /opt/temporal-offline/config/temporal-server.service /etc/systemd/system/

# 重新加载 systemd
systemctl daemon-reload
```

---

### 4.5 初始化数据库 Schema

#### 4.5.1 执行初始化脚本

```bash
# 复制初始化脚本
cp /opt/temporal-offline/config/init-temporal-db.sh /etc/temporal/

# 执行
bash /etc/temporal/init-temporal-db.sh
```

预期输出：

```
==========================================
  Temporal 数据库初始化
==========================================
[1/4] 创建数据库用户和数据库...
[2/4] 授权...
[3/4] 初始化 temporal 主库 schema...
[4/4] 初始化 visibility 库 schema...
==========================================
  Temporal 数据库初始化完成！
==========================================
```

#### 4.5.2 验证数据库表

```bash
# 检查 temporal 库的表
su - postgres -c "psql -d temporal -c '\dt'"

# 检查 temporal_visibility 库的表
su - postgres -c "psql -d temporal_visibility -c '\dt'"
```

预期输出包含多张表（如 `executions`、`tasks`、`namespace_registry` 等）。

---

### 4.6 启动 Temporal Server

#### 4.6.1 启动服务

```bash
systemctl start temporal-server
systemctl enable temporal-server
```

#### 4.6.2 查看启动日志

```bash
# 查看实时日志（关注是否出现 "Server ready"）
journalctl -u temporal-server -f
```

关键日志标识（出现即说明启动成功）：

```
INFO  startupLog {"level":"INFO","message":"Starting server..." }
...
INFO  startupLog {"level":"INFO","message":"Server ready" }
```

看到 "Server ready" 后按 `Ctrl+C` 退出日志查看。

#### 4.6.3 检查服务状态

```bash
systemctl status temporal-server --no-pager
```

预期输出 `Active: active (running)`。

#### 4.6.4 验证 7233 端口监听

```bash
ss -tlnp | grep 7233
```

预期输出显示 `0.0.0.0:7233` 处于 LISTEN 状态。

---

### 4.7 启动 Temporal Web UI（Docker）

#### 4.7.1 导入 UI 镜像

```bash
docker load -i /opt/temporal-offline/images/temporal-ui-2.28.0.tar
docker images | grep temporal
```

#### 4.7.2 启动 UI 容器

```bash
# 复制启动脚本
cp /opt/temporal-offline/config/start-temporal-ui.sh /opt/temporal/

# 执行
bash /opt/temporal/start-temporal-ui.sh
```

#### 4.7.3 验证容器状态

```bash
docker ps | grep temporal-ui
```

预期输出显示 `temporal-ui` 容器处于 `Up` 状态，端口映射 `0.0.0.0:8233->8080`。

---

### 4.8 验证部署

#### 4.8.1 访问 Web UI

```bash
hostname -I  # 获取服务器 IP
```

在浏览器中打开：

```
http://<离线服务器IP>:8233
```

看到 Temporal Web UI 界面即说明部署成功。

#### 4.8.2 使用 temporal CLI 验证

```bash
# 验证 Server 连通性
temporal operator cluster describe --address 127.0.0.1:7233
```

预期输出集群信息，包含 `ClusterName`、`HostInfo` 等。

#### 4.8.3 创建默认 Namespace（可选）

```bash
# 创建默认命名空间
temporal operator namespace create default --address 127.0.0.1:7233
```

---

## 五、日常运维操作

### 5.1 服务启停

```bash
# Temporal Server
systemctl start temporal-server
systemctl stop temporal-server
systemctl restart temporal-server
systemctl status temporal-server

# PostgreSQL
systemctl start postgresql-15
systemctl stop postgresql-15
systemctl restart postgresql-15

# Temporal UI (Docker)
docker stop temporal-ui
docker start temporal-ui
docker restart temporal-ui
```

### 5.2 查看日志

```bash
# Temporal Server 日志（systemd journal）
journalctl -u temporal-server -f              # 实时日志
journalctl -u temporal-server --since today   # 今天的日志
journalctl -u temporal-server -n 200          # 最近 200 行

# PostgreSQL 日志
tail -f /var/lib/pgsql/15/data/log/postgresql-*.log

# Temporal UI 日志
docker logs -f temporal-ui
```

### 5.3 修改配置后重启

```bash
# 编辑配置
vi /etc/temporal/config/temporal-server.yaml

# 重启 Temporal Server 使配置生效
systemctl restart temporal-server
```

### 5.4 数据备份

```bash
# 备份全部数据库
su - postgres -c "pg_dumpall" > /opt/temporal-backup-$(date +%Y%m%d).sql

# 仅备份 temporal 库
su - postgres -c "pg_dump temporal" > /opt/temporal-db-$(date +%Y%m%d).sql

# 恢复
su - postgres -c "psql < /opt/temporal-backup-20260805.sql"
```

### 5.5 防火墙配置

```bash
# 放行 Temporal Server gRPC 端口（Worker 连接用）
firewall-cmd --add-port=7233/tcp --permanent

# 放行 Web UI 端口（浏览器访问）
firewall-cmd --add-port=8233/tcp --permanent

# 放行 PostgreSQL 端口（如果 Worker 在其他机器上需要连 DB）
firewall-cmd --add-port=5432/tcp --permanent

firewall-cmd --reload
```

---

## 六、常见问题排查

### Q1：PostgreSQL 安装报依赖冲突

**现象**：`yum localinstall` 安装 PG RPM 时报缺少 `libicu` 或其他依赖。

**解决**：从离线机的安装光盘中查找对应 RPM，或从联网机补充下载：

```bash
# 在联网机上补充下载缺失依赖
yumdownloader --resolve --destdir=/tmp/extra-rpms libicu
# 然后将 extra-rpms 中的 RPM 一起拷贝到离线机的 pg-rpms 目录
yum localinstall -y *.rpm
```

---

### Q2：temporal-sql-tool 报认证失败

**现象**：`init-temporal-db.sh` 执行时报 `authentication failed for user "temporal"`。

**原因**：PostgreSQL 的 `pg_hba.conf` 仍使用 `ident` 认证（默认不允许密码登录）。

**解决**：

```bash
# 确认 pg_hba.conf 中 127.0.0.1 的认证方式为 md5
cat /var/lib/pgsql/15/data/pg_hba.conf | grep -v "^#" | grep -v "^$"

# 如果是 ident，改为 md5
sed -i 's/ident/md5/g' /var/lib/pgsql/15/data/pg_hba.conf

# 重启 PostgreSQL
systemctl restart postgresql-15

# 重新执行初始化
bash /etc/temporal/init-temporal-db.sh
```

---

### Q3：Temporal Server 启动失败

**排查步骤**：

```bash
# 查看详细日志
journalctl -u temporal-server -n 100 --no-pager
```

**常见原因与解决**：

| 日志关键词 | 原因 | 解决 |
| --- | --- | --- |
| `connection refused` | PostgreSQL 未启动 | `systemctl start postgresql-15` |
| `authentication failed` | 配置文件中密码与 PG 不一致 | 检查 `temporal-server.yaml` 中 `password` 与 `init-temporal-db.sh` 中 `PG_PASS` 是否一致 |
| `database "temporal" does not exist` | 数据库未初始化 | 执行 `bash /etc/temporal/init-temporal-db.sh` |
| `schema version mismatch` | schema 版本不匹配 | 重新执行 schema 初始化脚本 |

---

### Q4：Web UI 访问白屏或无法连接 Temporal Server

**排查**：

```bash
# 1. 确认 UI 容器正常运行
docker ps | grep temporal-ui

# 2. 查看 UI 容器日志
docker logs temporal-ui

# 3. 确认 UI 能否连到 Temporal Server
docker exec temporal-ui wget -qO- host.docker.internal:7233 || echo "连接失败"

# 4. 确认 Temporal Server 7233 端口监听
ss -tlnp | grep 7233

# 5. 确认防火墙放行
firewall-cmd --list-ports
```

**常见原因**：`host.docker.internal` 未生效（CentOS 上需要 `--add-host=host.docker.internal:host-gateway` 参数）。确认启动脚本中包含此参数。

---

### Q5：Worker 无法连接 Temporal Server

**排查**：

```bash
# 1. 确认 7233 端口对外监听
ss -tlnp | grep 7233

# 2. 从 Worker 机器测试连通性
telnet <Temporal服务器IP> 7233

# 3. 确认防火墙放行
firewall-cmd --add-port=7233/tcp --permanent
firewall-cmd --reload
```

---

### Q6：想彻底重置 Temporal Server（清除所有数据）

```bash
# 1. 停止 Temporal Server
systemctl stop temporal-server

# 2. 删除数据库
su - postgres -c "dropdb temporal"
su - postgres -c "dropdb temporal_visibility"

# 3. 重新初始化
bash /etc/temporal/init-temporal-db.sh

# 4. 启动 Temporal Server
systemctl start temporal-server
```

---

### Q7：修改 PostgreSQL 密码

```bash
# 1. 修改 PostgreSQL 中的密码
su - postgres -c "psql -c \"ALTER USER temporal WITH PASSWORD 'new_password';\""

# 2. 修改 Temporal Server 配置文件
vi /etc/temporal/config/temporal-server.yaml
# 将两个 datastore 的 password 改为 new_password

# 3. 重启 Temporal Server
systemctl restart temporal-server
```

---

> **附录与关联文档**：
> - 整体业务方案：参见 [Temporal业务应用方案介绍.md](file:///Users/apple/Desktop/AdapterService/docs/Temporal业务应用方案介绍.md)
> - 基础在线部署指南：参见 [Temporal部署.md](file:///Users/apple/Desktop/AdapterService/docs/Temporal部署.md)
> - Worker 自动扩缩容方案：参见 [temporal-worker自动扩缩容方案.md](file:///Users/apple/Desktop/AdapterService/docs/temporal-worker自动扩缩容方案.md)
