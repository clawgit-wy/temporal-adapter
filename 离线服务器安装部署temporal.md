# 离线服务器安装部署 Temporal

在完全离线的 CentOS 7.9 服务器上安装 Docker、Docker Compose 并拉起 Temporal 集群，需要分为 **三个步骤** 依次完成：

1. **安装 Docker & Docker Compose**
2. **还原并导入所有 Temporal 镜像**
3. **编写并启动 Docker Compose 编排**

---

## 第一步：安装 Docker 与 Docker Compose

假设你的安装包都在离线服务器的 `/root/install` 目录下：

```bash
cd /root/install

# 1. 解压 Docker 二进制包并复制到系统路径
tar -zxvf docker-*.tgz
cp docker/* /usr/bin/

# 2. 配置 Systemd 管理 Docker 服务
cat << 'EOF' > /etc/systemd/system/docker.service
[Unit]
Description=Docker Application Container Engine
Documentation=https://docs.docker.com
After=network-online.target firewalld.service
Wants=network-online.target

[Service]
Type=notify
ExecStart=/usr/bin/dockerd
ExecReload=/bin/kill -s HUP $MAINPID
LimitNOFILE=infinity
LimitNPROC=infinity
LimitCORE=infinity
TasksMax=infinity
TimeoutStartSec=0
Delegate=yes
KillMode=process
Restart=on-failure
StartLimitBurst=3
StartLimitInterval=60s

[Install]
WantedBy=multi-user.target
EOF

# 启动 Docker 并设置开机自启
systemctl daemon-reload
systemctl enable docker --now

# 3. 安装 Docker Compose
cp docker-compose /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose
ln -sf /usr/local/bin/docker-compose /usr/bin/docker-compose

# 4. 验证基础环境
docker version
docker-compose --version
```

---

## 第二步：合并切片包并导入 Docker 镜像

对于使用 `split -b 400m` 切割的 `temporal_pkg_part_*` 碎片，**最省磁盘空间的方法**是直接通过管道 `cat ... | docker load` 导入，无需在离线服务器上先合成一个 1G+ 的中间文件。

### 1. 合并切片并导入 temporal-auto-setup 镜像

利用管道将切片包直接恢复并流式加载入 Docker，不额外占用磁盘。进入存放切片的目录，执行：

```bash
cd /root/install

# 管道式直接导入 Docker（推荐：节省磁盘空间）
cat temporal_pkg_part_* | docker load
```

> **备选方案**：如果你想先校验文件完整性，也可以先合成为 `.tar` 再加载：
>
> ```bash
> cat temporal_pkg_part_* > temporal-auto-setup-1.29.7.tar
> docker load -i temporal-auto-setup-1.29.7.tar
> ```

### 2. 导入其余支撑镜像（PostgreSQL / Cassandra / UI）

如果你还下载了 PostgreSQL/Cassandra 及 Web UI 的 `.tar` 镜像，依次加载：

```bash
# 导入 PostgreSQL 镜像
docker load -i postgres-15.tar

# 导入 Temporal Web UI 镜像
docker load -i temporal-ui.tar
```

### 3. 检查镜像列表

运行以下命令查看已成功导入的镜像：

```bash
docker images
```

确保列表中包含了：

- `temporalio/auto-setup`（tag: `1.29.7`）
- 对应的数据库镜像（如 `postgres:15-alpine` 或 `cassandra:4.1`）
- Web UI 镜像（如 `temporalio/ui:latest`）

---

## 第三步：配置并启动 Temporal 集群

在离线服务器上创建一个工作目录，并编写 `docker-compose.yml` 文件。

### 1. 创建工作目录

```bash
mkdir -p /opt/temporal && cd /opt/temporal
```

### 2. 创建 `docker-compose.yml`

编写部署配置（以使用 PostgreSQL 为例，请根据你在腾讯云上拉取的数据库镜像修改）：

```yaml
version: '3.5'

services:
  postgresql:
    image: postgres:15-alpine
    container_name: temporal-postgresql
    environment:
      - POSTGRES_USER=temporal
      - POSTGRES_PASSWORD=temporal
      - POSTGRES_DB=temporal
    ports:
      - "5432:5432"
    restart: always

  temporal:
    image: temporalio/auto-setup:1.29.7
    container_name: temporal-server
    depends_on:
      - postgresql
    environment:
      - DB=postgres12
      - DB_PORT=5432
      - POSTGRES_USER=temporal
      - POSTGRES_PWD=temporal
      - POSTGRES_SEEDS=postgresql
      - DYNAMIC_CONFIG_FILE_PATH=config/dynamicconfig/development-sql.yaml
    ports:
      - "7233:7233"
    restart: always

  temporal-ui:
    image: temporalio/ui:latest
    container_name: temporal-ui
    depends_on:
      - temporal
    environment:
      - TEMPORAL_ADDRESS=temporal:7233
      - TEMPORAL_CORS_ORIGINS=http://localhost:8080
    ports:
      - "8080:8080"
    restart: always
```

### 3. 启动服务

```bash
# 后台启动所有容器
docker-compose up -d

# 查看容器运行状态
docker-compose ps

# 查看 Temporal 自动初始化日志（确保数据库 Schema 创建成功）
docker-compose logs -f temporal
```

---

## 验证与调试

1. **检查容器日志**：`temporalio/auto-setup` 镜像会在首次启动时自动执行数据库的 Schema 创建与迁移（`temporal-sql-tool`），日志出现 `Server template started` 代表启动成功。

2. **测试 CLI**：可以在服务器上直接通过容器访问 Temporal CLI：

   ```bash
   docker exec -it temporal-server temporal operator cluster health
   ```

3. **访问 Web UI**：如果离线服务器与你的局域网办公机可达，打开浏览器访问 `http://<离线服务器IP>:8080` 即可查看 Temporal Dashboard。
