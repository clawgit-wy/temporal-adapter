# 在线 CentOS 服务器拉取并导出 Temporal Worker 基础镜像

> **用途**：在有网的 CentOS 服务器上拉取 Docker 基础镜像并导出为 `.tar` 文件，供离线服务器导入使用。

---

## 一、安装 Docker（如已安装可跳过）

### 1.1 安装 Docker Engine

```bash
# 安装 yum-utils（提供 yum-config-manager）
sudo yum install -y yum-utils

# 添加 Docker 官方仓库（国内可替换为阿里云镜像加速）
sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo

# 如国内网络较慢，使用阿里云镜像源
sudo yum-config-manager --add-repo https://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo

# 安装 Docker Engine 和 Docker Compose 插件
sudo yum install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

# 启动 Docker 并设置开机自启
sudo systemctl start docker
sudo systemctl enable docker

# 验证安装
docker version
docker compose version
```

### 1.2 配置镜像加速（可选，国内网络推荐）

```bash
sudo mkdir -p /etc/docker
sudo tee /etc/docker/daemon.json <<-'EOF'
{
  "registry-mirrors": [
    "https://docker.1ms.run",
    "https://docker.xuanyuan.me"
  ]
}
EOF

# 重新加载配置并重启 Docker
sudo systemctl daemon-reload
sudo systemctl restart docker
```

---

## 二、拉取基础镜像

### 2.1 拉取 Maven 构建镜像（多阶段构建第一阶段使用）

```bash
docker pull maven:3.9-eclipse-temurin-17
```

### 2.2 拉取 JRE 运行镜像（多阶段构建第二阶段使用）

```bash
docker pull eclipse-temurin:17-jre
```

### 2.3 验证镜像拉取成功

```bash
docker images | grep -E "maven|eclipse-temurin"
```

预期输出：

```
REPOSITORY              TAG                       IMAGE ID       CREATED        SIZE
maven                   3.9-eclipse-temurin-17    xxxxxxxxxxxx   xx ago         650MB
eclipse-temurin         17-jre                    xxxxxxxxxxxx   xx ago         280MB
```

---

## 三、导出镜像为 tar 文件

### 3.1 创建导出目录

```bash
mkdir -p /root/docker-images
cd /root/docker-images
```

### 3.2 导出 Maven 镜像

```bash
docker save maven:3.9-eclipse-temurin-17 -o maven-3.9-eclipse-temurin-17.tar
```

### 3.3 导出 JRE 镜像

```bash
docker save eclipse-temurin:17-jre -o eclipse-temurin-17-jre.tar
```

### 3.4 查看导出文件

```bash
ls -lh /root/docker-images/
```

预期输出：

```
-rw-r--r-- 1 root root 630M  maven-3.9-eclipse-temurin-17.tar
-rw-r--r-- 1 root root 270M  eclipse-temurin-17-jre.tar
```

### 3.5（可选）压缩导出文件以节省传输时间

```bash
cd /root/docker-images

# 使用 gzip 压缩（压缩率约 30%）
gzip maven-3.9-eclipse-temurin-17.tar
gzip eclipse-temurin-17-jre.tar

# 查看压缩后大小
ls -lh
```

压缩后预期大小：

```
-rw-r--r-- 1 root root 230M  maven-3.9-eclipse-temurin-17.tar.gz
-rw-r--r-- 1 root root 100M  eclipse-temurin-17-jre.tar.gz
```

---

## 四、传输到离线服务器

### 4.1 通过 SCP 直接传输

```bash
# 传输未压缩的 tar 文件（离线服务器直接 docker load）
scp /root/docker-images/maven-3.9-eclipse-temurin-17.tar root@<离线服务器IP>:/root/install/
scp /root/docker-images/eclipse-temurin-17-jre.tar root@<离线服务器IP>:/root/install/

# 或传输压缩文件（传输后在离线服务器上解压）
scp /root/docker-images/maven-3.9-eclipse-temurin-17.tar.gz root@<离线服务器IP>:/root/install/
scp /root/docker-images/eclipse-temurin-17-jre.tar.gz root@<离线服务器IP>:/root/install/
```

### 4.2 通过 U 盘 / 移动硬盘传输

如果两台服务器网络不可达，可将 tar 文件拷贝到移动存储设备，再手动拷贝到离线服务器。

---

## 五、在离线服务器上导入镜像

### 5.1 登录离线服务器

```bash
ssh root@<离线服务器IP>
cd /root/install
```

### 5.2 导入 Maven 镜像

```bash
# 如果传输的是 .tar 文件
docker load -i maven-3.9-eclipse-temurin-17.tar

# 如果传输的是 .tar.gz 压缩文件
gunzip maven-3.9-eclipse-temurin-17.tar.gz
docker load -i maven-3.9-eclipse-temurin-17.tar
```

### 5.3 导入 JRE 镜像

```bash
# 如果传输的是 .tar 文件
docker load -i eclipse-temurin-17-jre.tar

# 如果传输的是 .tar.gz 压缩文件
gunzip eclipse-temurin-17-jre.tar.gz
docker load -i eclipse-temurin-17-jre.tar
```

### 5.4 验证镜像导入成功

```bash
docker images | grep -E "maven|eclipse-temurin"
```

预期输出：

```
REPOSITORY              TAG                       IMAGE ID       CREATED        SIZE
maven                   3.9-eclipse-temurin-17    xxxxxxxxxxxx   xx ago         650MB
eclipse-temurin         17-jre                    xxxxxxxxxxxx   xx ago         280MB
```

---

## 六、验证镜像可用性

在离线服务器上快速验证镜像可以正常启动：

```bash
# 验证 Maven 镜像
docker run --rm maven:3.9-eclipse-temurin-17 mvn --version

# 预期输出：
# Apache Maven 3.9.x
# Maven home: /usr/share/maven
# Java version: 17.0.x

# 验证 JRE 镜像
docker run --rm eclipse-temurin:17-jre java -version

# 预期输出：
# openjdk version "17.0.x"
# OpenJDK Runtime Environment
```

---

## 七、清理（可选）

导入验证完成后，可清理在线服务器上的导出文件释放磁盘：

```bash
# 在在线服务器上清理
rm -f /root/docker-images/maven-3.9-eclipse-temurin-17.tar
rm -f /root/docker-images/eclipse-temurin-17-jre.tar
rm -f /root/docker-images/*.tar.gz
```

---

## 附：完整操作速查

```bash
# ===== 在线服务器 =====

# 1. 拉取镜像
docker pull maven:3.9-eclipse-temurin-17
docker pull eclipse-temurin:17-jre

# 2. 导出镜像
mkdir -p /root/docker-images && cd /root/docker-images
docker save maven:3.9-eclipse-temurin-17 -o maven-3.9-eclipse-temurin-17.tar
docker save eclipse-temurin:17-jre -o eclipse-temurin-17-jre.tar

# 3. 传输到离线服务器
scp maven-3.9-eclipse-temurin-17.tar root@<离线服务器IP>:/root/install/
scp eclipse-temurin-17-jre.tar root@<离线服务器IP>:/root/install/

# ===== 离线服务器 =====

# 4. 导入镜像
cd /root/install
docker load -i maven-3.9-eclipse-temurin-17.tar
docker load -i eclipse-temurin-17-jre.tar

# 5. 验证
docker images | grep -E "maven|eclipse-temurin"
docker run --rm maven:3.9-eclipse-temurin-17 mvn --version
docker run --rm eclipse-temurin:17-jre java -version
```
