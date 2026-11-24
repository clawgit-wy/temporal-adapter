# setup/packages 离线安装可行性评估与第三方依赖清单

> **评估对象**：`setup/packages/` 下的三个软件包
> **目标环境**：CentOS 7（x86_64，内核 3.10，glibc 2.17，已于 2024-06-30 EOL）
> **评估日期**：2026-08-18

---

## 一、软件包清单与真实类型

| # | 文件名 | 大小 | 真实类型 | 内容 | 架构 |
|---|--------|------|----------|------|------|
| 1 | `temporal_1.31.2_linux_amd64 (1).tar.gz` | 89 MB | GitHub Release 二进制包 | temporal-server + temporal-sql-tool + temporal-cassandra-tool + temporal-elasticsearch-tool + tdbg + config/ 目录 | **amd64** |
| 2 | `postgres-15-alpine.tar.gz` | 104 MB | `docker save` 镜像包 | `postgres:15-alpine`（PG 15.18） | **arm64** |
| 3 | `temporalio-ui-2.32.0.tar.gz` | 43 MB | `docker save` 镜像包 | `temporalio/ui:2.32.0` | **arm64** |

### 1.1 各包内容详情

**包 1 - Temporal 二进制（解压后）：**
```
temporal-server              # Temporal Server 核心服务（frontend/history/matching/worker）
temporal-sql-tool            # PostgreSQL/MySQL schema 初始化工具
temporal-cassandra-tool      # Cassandra schema 工具（本项目用 PG，不需要）
temporal-elasticsearch-tool  # ES schema 工具（可选）
tdbg                         # 调试工具
config/                      # 配置模板（含 docker.yaml、development-postgres12.yaml 等）
```

**包 2/3 - Docker 镜像：**
通过 `docker load` 导入即可，需 Docker 运行时。

### 1.2 二进制链接方式验证

对 `temporal-server` 执行 `file` 和 `objdump` 检查：

```
ELF 64-bit LSB executable, x86-64, version 1 (SYSV), statically linked,
Go BuildID=..., stripped
```
- **静态链接**（CGO_ENABLED=0，go1.26.4 编译）
- 无 `NEEDED` 动态依赖记录
- **不依赖 glibc**，内核 3.10 远超 Go 最低要求

---

## 二、架构匹配性分析（关键问题）

### 2.1 架构不匹配告警

| 软件包 | 架构 | x86_64 CentOS 7 | arm64 CentOS 7 |
|--------|------|-----------------|-----------------|
| temporal 二进制 | amd64 | ✅ 可直接运行 | ❌ 无法运行 |
| postgres:15-alpine 镜像 | **arm64** | ❌ Docker 报 `exec format error` | ✅ 可运行 |
| temporalio/ui:2.32.0 镜像 | **arm64** | ❌ Docker 报 `exec format error` | ✅ 可运行 |

> **致命问题**：两个 Docker 镜像为 **arm64** 架构。若目标 CentOS 7 服务器为常规 x86_64（Intel/AMD），**这两个镜像无法运行**，Docker 启动时报 `exec format error`。

### 2.2 可能原因

在 Apple Silicon（M1/M2/M3）Mac 上执行 `docker pull` 时，Docker 默认拉取 **arm64** 镜像。导出后即为 arm64 包。

### 2.3 解决方案

**方案 A（目标服务器为 x86_64，推荐）**：重新拉取 amd64 镜像
```bash
# 在联网的 x86_64 机器或 Mac 上强制指定平台
docker pull --platform linux/amd64 postgres:15-alpine
docker pull --platform linux/amd64 temporalio/ui:2.32.0
docker save -o postgres-15-alpine-amd64.tar postgres:15-alpine
docker save -o temporalio-ui-2.32.0-amd64.tar temporalio/ui:2.32.0
```

**方案 B（目标服务器为 arm64，如鲲鹏/飞腾）**：换 temporal 二进制
```bash
# 下载 arm64 版本 temporal
curl -LO https://github.com/temporalio/temporal/releases/download/v1.31.2/temporal_1.31.2_linux_arm64.tar.gz
```

**方案 C（混合架构不可行）**：不能同时使用 amd64 二进制 + arm64 镜像，必须统一架构。

> **本文档后续步骤假设目标为 x86_64 CentOS 7，即需先按方案 A 重新准备 amd64 镜像。**

---

## 三、CentOS 7 离线安装可行性总评

| 组件 | 可行性 | 前提条件 | 风险点 |
|------|--------|----------|--------|
| Temporal v1.31.2 二进制 | ✅ 完全可行 | 无特殊要求 | 仅需 ca-certificates、tzdata |
| PostgreSQL 15（RPM 直装） | ✅ 可行 | libicu 50.x（系统自带） | PGDG 仓库仅 PG15 支持 rhel7 |
| Docker CE（运行 PG/UI 镜像） | ⚠️ 可行但需注意 | 内核 ≥ 3.10.0-693（即 7.4+） | EOL 后依赖包从 vault 取 |
| postgres:15-alpine 镜像 | ❌ 当前包不可用 | 需重新拉 amd64 版本 | 架构不匹配 |
| temporalio/ui:2.32.0 镜像 | ❌ 当前包不可用 | 需重新拉 amd64 版本 | 架构不匹配 |

### 3.1 CentOS 7 系统限制清单

| 限制项 | CentOS 7 现状 | 影响 |
|--------|--------------|------|
| 内核版本 | 3.10.0-1160（7.9） | Docker overlay2 需 7.4+，d_type 支持 |
| glibc 版本 | 2.17 | 静态 Go 二进制无影响；动态二进制受限 |
| cgroup | 仅 v1 | Docker 可运行，部分新特性受限 |
| iptables | 1.4.21（legacy） | Docker 网络正常，无 nft 冲突 |
| EOL 状态 | 2024-06-30 停服 | base/extras 迁至 vault.centos.org |
| 仓库可用性 | mirror.centos.org 已 404 | 必须用 vault.centos.org |

---

## 四、第三方依赖完整清单

### 4.1 组件 1 - Docker CE 及其依赖（运行 Docker 镜像用）

#### 4.1.1 Docker 仓库 RPM（来源：download.docker.com/linux/centos/7/x86_64/stable/Packages/）

| 包名 | 约束 | 用途 |
|------|------|------|
| `docker-ce` | 与 cli 同版本 | Docker 守护进程 |
| `docker-ce-cli` | 与 ce 同版本 | 客户端命令行 |
| `containerd.io` | ≥ 1.4.1 | 容器运行时 |
| `docker-compose-plugin` | v2.x | compose 子命令 |
| `docker-buildx-plugin` | - | buildx（docker-ce 可能硬依赖） |
| `docker-ce-rootless-extras` | - | rootless 模式（较新 docker-ce 可能硬依赖） |

#### 4.1.2 CentOS 7 系统依赖（来源：vault.centos.org/7.9.2009/）

| 包名 | 仓库 | 约束 | 用途 |
|------|------|------|------|
| `container-selinux` | extras | ≥ 2:2.9-1.el7 | **必需**，SELinux 容器策略，缺则容器启动 AVC 拒绝 |
| `slirp4netns` | extras | ≥ 0.4 | rootless 网络栈（rootless-extras 传递依赖） |
| `fuse-overlayfs` | extras | ≥ 0.7 | rootless 文件层（rootless-extras 传递依赖） |
| `fuse3-libs` | extras | 3.x | fuse-overlayfs 依赖（libfuse3.so.3） |
| `libseccomp` | base | ≥ 2.3.0（CentOS 7 为 2.3.1，满足） | containerd.io 依赖 |
| `iptables` | base | 1.4.21 | 容器网络 NAT/forward |
| `libcgroup` | base | 0.41 | cgroup 管理 |
| `systemd` / `systemd-libs` | base | 219 | docker.service 单元 |
| `runc` | docker 仓库 | 随 containerd 或独立 | 容器运行时（部分 containerd.io 内置） |

#### 4.1.3 docker-compose 独立二进制（可选，替代 plugin）

| 文件 | 来源 | 依赖 |
|------|------|------|
| `docker-compose-Linux-x86_64` | github.com/docker/compose/releases | **无**（静态 Go 二进制，无 glibc 依赖） |

### 4.2 组件 2 - PostgreSQL 15（RPM 直装，非 Docker）

#### 4.2.1 PGDG 仓库 RPM（来源：yum.postgresql.org/15/redhat/rhel-7-x86_64/）

| 包名 | 约束 | 用途 |
|------|------|------|
| `postgresql15-server` | 15.x | 服务端（含 initdb） |
| `postgresql15` | 同 server 版本 | 客户端 |
| `postgresql15-libs` | 同 server 版本 | libpq.so.5 / libecpg.so.6 |
| `postgresql15-contrib` | 同 server 版本 | 扩展（可选） |

> **注**：PGDG 对 RHEL 7 **仅提供 PG15**，PG16/17/18 不提供 rhel7 构建。

#### 4.2.2 CentOS 7 系统依赖（来源：vault.centos.org/7.9.2009/os/x86_64/Packages/）

| 包名 | 版本（CentOS 7 自带） | 用途 |
|------|----------------------|------|
| `libicu` | 50.1.2（**系统自带，满足**） | ICU 排序，PG15 链接 libicuuc.so.50 |
| `libxml2` | 2.9.1 | XML 支持 |
| `libxslt` | 1.1.28 | XSLT |
| `openssl-libs` | 1.0.2k | TLS |
| `zlib` | 1.2.7 | 压缩 |
| `readline` | 6.2 | psql 行编辑 |
| `systemd-libs` | 219 | 服务管理 |
| `perl` | 5.16 | initdb 脚本 |

> **libicu 关键结论**：PGDG 的 rhel7 构建将 PG15 链接到 libicu 50（soname `libicuuc.so.50`），CentOS 7 自带 `libicu-50.1.2` **满足要求**，不需要额外 PGDG libicu。
> 建议拿到 RPM 后实测：`rpm -qpR postgresql15-libs-*.rhel7.rpm | grep libicu` 确认。

### 4.3 组件 3 - Temporal v1.31.2 二进制

| 依赖 | 是否必需 | 来源 | 说明 |
|------|---------|------|------|
| `ca-certificates` | **必需** | vault base | Go 读取 `/etc/pki/tls/certs/ca-bundle.crt` 校验 TLS |
| `tzdata` | 建议必需 | vault base | 时区数据（`/usr/share/zoneinfo`），Temporal 调度依赖 |
| `/etc/nsswitch.conf` | 已存在 | 系统默认 | 纯 Go DNS 解析器读取 |
| `/etc/resolv.conf` | 已存在 | 系统默认 | DNS 配置 |
| `/etc/hosts` | 已存在 | 系统默认 | 本机解析 |

> **无 RPM 依赖**。静态二进制不链接 glibc，内核 3.10 完全满足。

---

## 五、依赖关系图

```
┌─────────────────────────────────────────────────────────────────┐
│                    CentOS 7 离线安装依赖树                       │
└─────────────────────────────────────────────────────────────────┘

Temporal Server（裸机直装）
├── temporal_1.31.2_linux_amd64.tar.gz（静态二进制，无 glibc 依赖）
├── ca-certificates（vault base）           ← 必需
├── tzdata（vault base）                    ← 建议必需
└── PostgreSQL 15（数据库，见下）
    ├── postgresql15-server（PGDG）
    │   ├── postgresql15（PGDG）
    │   │   └── postgresql15-libs（PGDG）→ libpq.so.5
    │   ├── libicu 50.1.2（vault base）  ← 系统自带
    │   ├── libxml2（vault base）
    │   ├── libxslt（vault base）
    │   ├── openssl-libs（vault base）
    │   ├── zlib（vault base）
    │   ├── readline（vault base）
    │   └── systemd-libs（vault base）

Docker CE（运行 PG/UI 镜像用）
├── docker-ce（docker 仓库）
│   ├── docker-ce-cli（docker 仓库）
│   ├── containerd.io ≥1.4.1（docker 仓库）
│   │   └── libseccomp ≥2.3.0（vault base）
│   ├── docker-compose-plugin（docker 仓库）
│   ├── docker-buildx-plugin（docker 仓库）
│   └── docker-ce-rootless-extras（docker 仓库）
│       ├── slirp4netns ≥0.4（vault extras）
│       └── fuse-overlayfs ≥0.7（vault extras）
│           └── fuse3-libs（vault extras）
├── container-selinux ≥2.9（vault extras）   ← 必需
├── iptables（vault base）
├── libcgroup（vault base）
└── systemd（vault base）

Docker 镜像（需 amd64 版本！）
├── postgres-15-alpine-amd64.tar（当前包为 arm64，需重新拉取）
└── temporalio-ui-2.32.0-amd64.tar（当前包为 arm64，需重新拉取）
```

---

## 六、安装顺序

```
步骤 1: 验证服务器架构与内核版本
  └─ 确认 x86_64 + 内核 ≥ 3.10.0-693（CentOS 7.4+）

步骤 2: 安装 ca-certificates + tzdata（Temporal 运行前置）
  └─ 从 vault base 下载 RPM

步骤 3: 安装 PostgreSQL 15（数据库前置）
  ├─ 安装 vault base 依赖（libicu/libxml2/libxslt/openssl-libs/zlib/readline）
  └─ 安装 PGDG RPM（postgresql15-libs → postgresql15 → postgresql15-server）

步骤 4: 安装 Docker CE（运行 UI 镜像用）
  ├─ 安装 vault extras 依赖（container-selinux/slirp4netns/fuse-overlayfs/fuse3-libs）
  ├─ 安装 vault base 依赖（libseccomp/iptables/libcgroup/systemd）
  └─ 安装 docker 仓库 RPM（containerd.io → docker-ce-cli → docker-ce → plugins）

步骤 5: 部署 Temporal 二进制
  ├─ 解压 temporal_1.31.2_linux_amd64.tar.gz
  ├─ 放置 temporal-server/temporal-sql-tool 到 /usr/local/bin/
  ├─ 初始化 PG schema（用 temporal-sql-tool）
  └─ 配置 systemd 启动

步骤 6: 导入并运行 Docker 镜像（需 amd64 版本）
  ├─ docker load -i postgres-15-alpine-amd64.tar（若不用裸机 PG）
  ├─ docker load -i temporalio-ui-2.32.0-amd64.tar
  └─ docker run temporalio/ui:2.32.0
```

> **关键顺序**：`ca-certificates` → `PG 依赖` → `PG RPM` → `Docker 系统依赖` → `Docker RPM` → `Temporal 二进制` → `Docker 镜像`

---

## 七、手动安装步骤

### 7.1 步骤 1 - 验证环境

```bash
# 架构（必须 x86_64）
uname -m
# 预期: x86_64

# 内核版本（Docker overlay2 需 7.4+，即 ≥ 3.10.0-693）
uname -r
# 预期: 3.10.0-1160.el7.x86_64（CentOS 7.9）

# CentOS 版本
cat /etc/redhat-release
# 预期: CentOS Linux release 7.9.2009 (Core)

# glibc 版本
ldd --version | head -1
# 预期: ldd (GNU libc) 2.17

# 文件系统 d_type（Docker overlay2 需要）
xfs_info / 2>/dev/null | grep ftype
# 预期: ftype=1（若为 xfs）；ext4 默认支持
```

### 7.2 步骤 2 - 安装 ca-certificates + tzdata

```bash
# 从 vault.centos.org 下载（联网机操作）
# 放入 ca-certificates-rpms/ 目录

# 离线机安装
cd /opt/temporal-offline/depends-rpms
yum localinstall -y ca-certificates-*.rpm tzdata-*.rpm

# 验证
ls -l /etc/pki/tls/certs/ca-bundle.crt
ls -l /usr/share/zoneinfo/Asia/Shanghai
```

### 7.3 步骤 3 - 安装 PostgreSQL 15

```bash
# 3.1 安装 base 依赖
cd /opt/temporal-offline/pg-deps-rpms   # libicu/libxml2/libxslt/openssl-libs/zlib/readline/systemd-libs
yum localinstall -y *.rpm

# 3.2 安装 PGDG RPM（注意顺序：libs → client → server）
cd /opt/temporal-offline/pg-rpms
rpm -ivh postgresql15-libs-15.*.rpm          # 先装 libs（提供 libpq.so.5）
rpm -ivh postgresql15-15.*.rpm                 # 再装 client
rpm -ivh postgresql15-server-15.*.rpm          # 最后装 server

# 3.3 初始化数据库
/usr/pgsql-15/bin/postgresql-15-setup initdb

# 3.4 配置认证（允许密码登录）
cp /var/lib/pgsql/15/data/pg_hba.conf /var/lib/pgsql/15/data/pg_hba.conf.bak
sed -i 's/ident/md5/g' /var/lib/pgsql/15/data/pg_hba.conf
echo "host    all    all    0.0.0.0/0    md5" >> /var/lib/pgsql/15/data/pg_hba.conf

# 3.5 启动
systemctl enable --now postgresql-15
systemctl status postgresql-15

# 3.6 验证
su - postgres -c "psql -c '\l'"
```

### 7.4 步骤 4 - 安装 Docker CE

```bash
# 4.1 清理旧版本
yum remove -y docker docker-client docker-common docker-latest 2>/dev/null || true

# 4.2 安装 extras 依赖（container-selinux 等，必须先装）
cd /opt/temporal-offline/docker-extras-rpms   # container-selinux/slirp4netns/fuse-overlayfs/fuse3-libs
yum localinstall -y *.rpm

# 4.3 安装 base 依赖
cd /opt/temporal-offline/docker-base-rpms      # libseccomp/iptables/libcgroup/systemd
yum localinstall -y *.rpm

# 4.4 安装 Docker RPM（注意顺序）
cd /opt/temporal-offline/docker-rpms
rpm -ivh containerd.io-*.rpm                   # 先 containerd
rpm -ivh docker-ce-cli-*.rpm                   # 再 cli
rpm -ivh docker-ce-rootless-extras-*.rpm       # 再 rootless-extras（若 docker-ce 硬依赖）
rpm -ivh docker-buildx-plugin-*.rpm            # plugins
rpm -ivh docker-compose-plugin-*.rpm
rpm -ivh docker-ce-*.rpm                       # 最后 docker-ce

# 4.5 配置存储驱动
mkdir -p /etc/docker
cat > /etc/docker/daemon.json <<'EOF'
{
  "storage-driver": "overlay2",
  "log-opts": { "max-size": "100m", "max-file": "3" }
}
EOF

# 4.6 启动
systemctl enable --now docker
docker version
docker info | grep -E "Storage|Cgroup"

# 4.7（可选）安装独立 docker-compose
cp /opt/temporal-offline/bin/docker-compose /usr/local/bin/
chmod +x /usr/local/bin/docker-compose
docker-compose version
```

### 7.5 步骤 5 - 部署 Temporal 二进制

```bash
# 5.1 解压
cd /opt/temporal-offline
tar -xzf "setup/packages/temporal_1.31.2_linux_amd64 (1).tar.gz" -C temporal-bin/

# 5.2 放置二进制
cp temporal-bin/temporal-server    /usr/local/bin/
cp temporal-bin/temporal-sql-tool  /usr/local/bin/
chmod +x /usr/local/bin/temporal-server /usr/local/bin/temporal-sql-tool

# 5.3 放置 schema 文件（需从 admin-tools 镜像提取，见 7.6）
# 或从源码 v1.31.2 的 temporal/schema/ 目录获取

# 5.4 初始化 PG schema
export SQL_PASSWORD=temporal
temporal-sql-tool --plugin postgres12 --ep 127.0.0.1 -u temporal -p 5432 \
    --db temporal create
temporal-sql-tool --plugin postgres12 --ep 127.0.0.1 -u temporal -p 5432 \
    --db temporal setup-schema -v 0.0
temporal-sql-tool --plugin postgres12 --ep 127.0.0.1 -u temporal -p 5432 \
    --db temporal update-schema -d /etc/temporal/schema/postgresql/v12/temporal/versioned

# 5.5 配置 systemd（参考已有 docs/Temporal-Server离线部署教程.md）
```

### 7.6 步骤 6 - 导入 Docker 镜像（需 amd64！）

```bash
# ⚠️ 当前 packages 中的镜像是 arm64，x86_64 服务器必须先按"方案 A"重新准备 amd64 包
# 确认架构后再 load
docker load -i postgres-15-alpine-amd64.tar       # 若不用裸机 PG
docker load -i temporalio-ui-2.32.0-amd64.tar

# 验证架构
docker inspect postgres:15-alpine | grep Architecture
# 预期: "Architecture": "amd64"

docker inspect temporalio/ui:2.32.0 | grep Architecture
# 预期: "Architecture": "amd64"

# 启动 UI
docker run -d --name temporal-ui --restart always \
    --add-host=host.docker.internal:host-gateway \
    -p 8233:8080 \
    -e TEMPORAL_ADDRESS=host.docker.internal:7233 \
    temporalio/ui:2.32.0
```

---

## 八、常见问题与解决方法

### 8.1 架构相关

| 问题 | 现象 | 原因 | 解决 |
|------|------|------|------|
| 镜像无法启动 | `exec format error` | 镜像 arm64，服务器 x86_64 | 重新拉 `--platform linux/amd64` 镜像 |
| temporal 二进制无法执行 | `cannot execute binary file` | 二进制 amd64，服务器 arm64 | 下载 `temporal_1.31.2_linux_arm64.tar.gz` |

### 8.2 Docker 相关

| 问题 | 现象 | 原因 | 解决 |
|------|------|------|------|
| container-selinux 缺失 | 容器启动 AVC 拒绝，`docker run` 报权限错误 | 未装 container-selinux | `yum install container-selinux` |
| overlay2 不可用 | `docker info` 显示 devicemapper，启动告警 | 内核 < 3.10.0-693 或 xfs ftype=0 | 升级到 7.9；xfs 需 `ftype=1` |
| containerd.io 依赖失败 | `rpm -ivh` 报缺 libseccomp | 未先装 libseccomp | 先装 `libseccomp-2.3.1` |
| rootless-extras 依赖失败 | 报缺 slirp4netns/fuse-overlayfs | 未装 extras 依赖 | 先装 slirp4netns/fuse-overlayfs/fuse3-libs |
| docker-ce 安装失败 | 报缺 iptables/libcgroup | 未装 base 依赖 | 先装 iptables/libcgroup/systemd |
| 镜像 load 后看不到 | `docker images` 为空 | 镜像架构不匹配被拒绝 | 确认镜像为 amd64 |
| 仓库 404 | `yum install` 报 mirror.centos.org 404 | CentOS 7 EOL | 改用 vault.centos.org |

### 8.3 PostgreSQL 相关

| 问题 | 现象 | 原因 | 解决 |
|------|------|------|------|
| PGDG repo 签名失败 | `repomd.xml signature could not be verified` | repo_gpgcheck=1 且密钥变更 | 设 `repo_gpgcheck=0`，保留 `gpgcheck=1` |
| pgdg16/17 仓库 404 | `[pgdg16] 404` | PG16/17 无 rhel7 构建 | repo 文件设 `enabled=0` |
| libicu 缺失 | `libicuuc.so.50()(64bit) is needed` | 系统未装 libicu | `yum install libicu`（50.1.2） |
| 认证失败 | `authentication failed for user "temporal"` | pg_hba.conf 用 ident | `sed -i 's/ident/md5/g' pg_hba.conf` |
| libpq 冲突 | 安装时报与 postgresql-libs(9.2) 冲突 | CentOS 7 自带 PG9.2 libs | 先卸载旧版 `yum remove postgresql-libs` 或用 `--nodeps` |

### 8.4 Temporal 相关

| 问题 | 现象 | 原因 | 解决 |
|------|------|------|------|
| TLS 证书错误 | `x509: certificate signed by unknown authority` | ca-certificates 缺失/过期 | `yum install ca-certificates` |
| 时区异常 | 时间相关 API 行为异常 | tzdata 缺失 | `yum install tzdata` |
| schema 初始化失败 | `relation "schema_version" does not exist` | 未执行 setup-schema | 先 `setup-schema -v 0.0` 再 `update-schema` |
| 连接 PG 失败 | `connection refused 127.0.0.1:5432` | PG 未启动或 pg_hba 未配置 | `systemctl start postgresql-15`；检查 pg_hba.conf |

### 8.5 CentOS 7 EOL 相关

| 问题 | 现象 | 解决 |
|------|------|------|
| mirror.centos.org 404 | `yum install` 报 404 | 改 baseurl 为 `https://vault.centos.org/7.9.2009/...` |
| 无法联网验证 GPG | `GPG key retrieval failed` | `rpm --import` 预置密钥文件 |
| 依赖包缺失 | `No package xxx available` | 从 vault 对应目录手动下载 RPM |

---

## 九、依赖包下载来源汇总

### 9.1 vault.centos.org（CentOS 7 base/extras，EOL 后镜像）

| 仓库 | URL |
|------|-----|
| base | `https://vault.centos.org/7.9.2009/os/x86_64/Packages/` |
| extras | `https://vault.centos.org/7.9.2009/extras/x86_64/Packages/` |
| updates | `https://vault.centos.org/7.9.2009/updates/x86_64/Packages/` |

**base 需下载**：ca-certificates、tzdata、libicu、libxml2、libxslt、openssl-libs、zlib、readline、systemd-libs、libseccomp、iptables、libcgroup、systemd、perl

**extras 需下载**：container-selinux、slirp4netns、fuse-overlayfs、fuse3-libs

### 9.2 download.docker.com（Docker CE）

| URL |
|-----|
| `https://download.docker.com/linux/centos/7/x86_64/stable/Packages/` |

**需下载**：docker-ce、docker-ce-cli、containerd.io、docker-compose-plugin、docker-buildx-plugin、docker-ce-rootless-extras

### 9.3 yum.postgresql.org（PGDG）

| URL |
|-----|
| `https://yum.postgresql.org/15/redhat/rhel-7-x86_64/` |

**需下载**：postgresql15-server、postgresql15、postgresql15-libs、postgresql15-contrib

> **GPG 密钥**：`https://yum.postgresql.org/rpm-gpg/PGDG-RPM-KEY`（离线预置）

### 9.4 github.com（Temporal + docker-compose）

| URL |
|-----|
| `https://github.com/temporalio/temporal/releases/download/v1.31.2/temporal_1.31.2_linux_amd64.tar.gz` |
| `https://github.com/docker/compose/releases/download/v2.29.2/docker-compose-linux-x86_64` |

---

## 十、处理报告

### 10.1 发现的问题类型与数量

| 问题类型 | 数量 | 严重程度 |
|----------|------|----------|
| 架构不匹配 | 2 | **致命** - 两个 Docker 镜像为 arm64，x86_64 无法运行 |
| 软件包类型 | 3 | 已确认（2 个 Docker 镜像 + 1 个二进制包） |
| 系统兼容性 | 5 | CentOS 7 EOL、内核/glibc/cgroup/iptables/overlay2 限制 |
| 依赖缺失 | 20+ | Docker/PG15/Temporal 三方依赖链 |
| 仓库配置 | 3 | PGDG 签名验证、pgdg16/17 404、mirror.centos.org 404 |

### 10.2 处理结果

| 问题 | 处理方法 | 结果 |
|------|----------|------|
| 镜像架构不匹配 | 重新拉 `--platform linux/amd64` | 待用户执行 |
| Temporal 二进制静态链接 | 验证 CGO_ENABLED=0，无 glibc 依赖 | ✅ CentOS 7 可直接运行 |
| Docker CE 依赖链 | 梳理 6 个 Docker RPM + 8 个系统 RPM | ✅ 完整清单已列出 |
| PG15 依赖链 | 梳理 4 个 PGDG RPM + 8 个系统 RPM | ✅ 完整清单已列出 |
| libicu 版本 | 确认 CentOS 7 自带 50.1.2 满足 PG15 | ✅ 无需额外安装 |
| CentOS 7 EOL | base/extras 迁至 vault.centos.org | ✅ 来源已明确 |
| Temporal 运行依赖 | 仅需 ca-certificates + tzdata | ✅ 无 RPM 依赖 |

### 10.3 最终结论

1. **Temporal v1.31.2 二进制**：✅ **完全可行**，静态链接无 glibc 依赖，CentOS 7 可直接运行。
2. **PostgreSQL 15（RPM 直装）**：✅ **可行**，PGDG 提供 rhel7 构建，依赖均可从 vault 获取。
3. **Docker CE**：✅ **可行但需注意**，需内核 7.4+，依赖较多但均可从 vault + docker.com 获取。
4. **postgres:15-alpine 镜像**：❌ **当前包不可用**（arm64），需重新拉 amd64 版本。
5. **temporalio/ui:2.32.0 镜像**：❌ **当前包不可用**（arm64），需重新拉 amd64 版本。

> **首要行动项**：在 x86_64 目标服务器场景下，**必须先按"方案 A"重新拉取 amd64 镜像**，否则 Docker 镜像无法使用。Temporal 二进制可直接使用。
