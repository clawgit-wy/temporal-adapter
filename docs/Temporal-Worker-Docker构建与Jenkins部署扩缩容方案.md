# Temporal Worker Docker 构建与 Jenkins 部署扩缩容方案

> **文档定位**：基于 [Temporal业务应用方案介绍.md](file:///Users/apple/Desktop/AdapterService/docs/Temporal业务应用方案介绍.md) 第 6.5 节的 CI/CD 方案，结合实际 `temporal-workers` 项目代码，提供一套完整可落地的 Jenkins + Docker 自动部署与扩缩容详细流程。
>
> **核心链路**：GitLab 托管 Worker 代码 -> Jenkins 拉取源码并用 Maven 编译 JAR -> 推送 JAR 到部署服务器 -> 部署服务器构建 Docker 镜像并自动扩缩容

---

## 一、方案总览

### 1.1 方案架构

本方案采用 **"Jenkins 编译 JAR + 部署服务器构建镜像"** 的分层构建模式：

```
GitLab 仓库                    Jenkins 节点                       目标 CentOS 服务器
┌──────────────────┐         ┌──────────────────┐             ┌──────────────────┐
│ temporal-workers │         │                  │             │                  │
│ ├── src/         │  ①拉取   │  ②Maven 编译     │  ③传输      │  ④构建 Docker    │
│ ├── pom.xml      │────────▶│     JAR 包       │───────────▶│     镜像          │
│ ├── Dockerfile   │         │  mvn package     │  JAR+配置  │  docker build    │
│ │   (简化版)      │         │                  │  scp/ssh   │  ⑤扩缩容          │
│ └── docker-      │         │  (需 JDK17+Maven │  (~40MB)   │  docker compose  │
│    compose.yml   │         │   无需 Docker)   │             │  up -d --scale   │
└──────────────────┘         └──────────────────┘             │                  │
                                                              │  Worker N 实例    │
                                                              └──────────────────┘
```

### 1.2 方案设计思路

| 设计决策 | 说明 |
| --- | --- |
| **Jenkins 只编译 JAR** | Jenkins 节点需要 JDK 17 + Maven，但**不需要安装 Docker** |
| **部署服务器构建镜像** | 部署服务器用简化版 Dockerfile（单阶段，仅 COPY JAR），只需预装 `eclipse-temurin:17-jre` 一个基础镜像 |
| **传输内容最小化** | 仅传输 JAR 包（~40MB）+ Dockerfile + docker-compose.yml，远小于完整镜像传输（~400MB） |
| **离线服务器友好** | 部署服务器不需要 Maven 依赖下载，不需要 `maven:3.9-eclipse-temurin-17` 镜像，只需一个 JRE 基础镜像 |

### 1.3 与原方案的关键差异

> 原文档 [temporal-worker自动扩缩容方案.md](file:///Users/apple/Desktop/AdapterService/docs/temporal-worker自动扩缩容方案.md) 中的配置为通用示例，与实际项目代码存在以下差异，本方案已全部修正：

| 差异点 | 原文档示例 | 实际项目代码 | 本方案修正 |
| --- | --- | --- | --- |
| **Dockerfile** | 单阶段 `FROM openjdk:17-slim` | 多阶段 `maven:3.9-...` -> `eclipse-temurin:17-jre` | 采用**简化版单阶段** Dockerfile（JAR 由 Jenkins 预编译） |
| **环境变量** | `TEMPORAL_SERVER_HOST`、`TASK_QUEUE` | `TEMPORAL_ADDRESS`、`WORKER_SCENARIOS` | 与 [application.yml](file:///Users/apple/Desktop/AdapterService/temporal-workers/src/main/resources/application.yml) 完全对齐 |
| **镜像名称** | `temporal-worker` | `temporal-workers`（复数） | 与 [pom.xml](file:///Users/apple/Desktop/AdapterService/temporal-workers/pom.xml) 的 `artifactId` 一致 |
| **JAR 名称** | 未指定 | `temporal-workers-1.0.0.jar` | 与 pom.xml 的 `artifactId-version` 一致 |
| **compose 版本** | `version: '3.8'` | - | 移除已废弃的 `version` 字段，使用 Compose V2 规范 |

---

## 二、环境要求

### 2.1 Jenkins 节点环境要求

Jenkins 节点负责拉取源码和 Maven 编译，**不需要安装 Docker**。

| 组件 | 版本要求 | 用途 | 验证命令 |
| --- | --- | --- | --- |
| **JDK 17** | ≥17（推荐 Eclipse Temurin 17） | 编译 Spring Boot 3.2.5 项目 | `java -version` |
| **Maven** | ≥3.8 | 执行 `mvn package` 编译 JAR | `mvn -version` |
| **Git** | ≥2.0 | 从 GitLab 拉取源码 | `git --version` |
| **SSH 客户端** | 系统自带 | 传输文件到部署服务器 | `ssh -V` |

**Jenkins 全局工具配置**（`Manage Jenkins` -> `Global Tool Configuration`）：

| 配置项 | 值 | 说明 |
| --- | --- | --- |
| JDK 名称 | `jdk17` | 安装路径指向 JDK 17 实际目录 |
| Maven 名称 | `maven3.9` | Maven 安装路径 |

> **重要**：Jenkins 节点不需要安装 Docker。Docker 构建在部署服务器上完成。

### 2.2 部署服务器环境要求

部署服务器负责构建 Docker 镜像和运行 Worker 容器。

| 组件 | 版本要求 | 用途 | 验证命令 |
| --- | --- | --- | --- |
| **Docker Engine** | ≥17.05（支持多阶段构建），推荐 20.10+ | 构建镜像 + 运行容器 | `docker version` |
| **Docker Compose V2** | ≥2.0（`docker compose` 子命令） | 扩缩容编排 | `docker compose version` |
| **openssh-server** | 系统自带 | 接收 Jenkins SSH 连接 | `systemctl status sshd` |
| **磁盘空间** | ≥2GB | 镜像存储 + 容器运行 | `df -h` |

### 2.3 离线部署服务器额外要求

如果部署服务器是**完全离线**的，需要提前导入基础镜像：

| 必须预装的 Docker 镜像 | 用途 | 导入方式 |
| --- | --- | --- |
| `eclipse-temurin:17-jre` | 简化版 Dockerfile 的基础镜像 | `docker load -i eclipse-temurin-17-jre.tar` |

> **离线镜像准备**：参考 [在线拉取并导出Temporal-Worker基础镜像.md](file:///Users/apple/Desktop/AdapterService/docs/在线拉取并导出Temporal-Worker基础镜像.md)，在有网机器上拉取并导出，然后传输到离线服务器导入。
>
> **本方案离线优势**：部署服务器只需预装 `eclipse-temurin:17-jre` 一个镜像（~280MB），不需要 `maven:3.9-eclipse-temurin-17` 镜像（因为 Maven 编译在 Jenkins 上完成）。

### 2.4 网络连通性要求

| 源 -> 目标 | 端口 | 用途 | 验证命令 |
| --- | --- | --- | --- |
| Jenkins -> GitLab | 22 / 443 | 拉取源码 | `ssh -T git@gitlab.your-company.com` |
| Jenkins -> 部署服务器 | 22 | SSH 传输 JAR + 远程执行命令 | `ssh root@<部署服务器IP> 'echo ok'` |
| 部署服务器 -> Temporal Server | 7233 | Worker gRPC 长轮询 | `telnet <Temporal_IP> 7233` |

### 2.5 环境要求总结

| 要求项 | Jenkins 节点 | 部署服务器（在线） | 部署服务器（离线） |
| --- | --- | --- | --- |
| JDK 17 | 需要 | 不需要 | 不需要 |
| Maven | 需要 | 不需要 | 不需要 |
| Docker | 不需要 | 需要 | 需要 |
| Docker Compose V2 | 不需要 | 需要 | 需要 |
| `eclipse-temurin:17-jre` 镜像 | 不需要 | 不需要（首次构建自动拉取） | **需要预装** |
| 外网访问 | 需要（GitLab + Maven Central） | 需要（首次拉取基础镜像） | 不需要 |
| Git | 需要 | 不需要 | 不需要 |

---

## 三、项目代码仓库准备

### 3.1 仓库文件结构

GitLab 仓库需要包含以下文件：

```
temporal-workers/
├── src/                          # Worker 业务源码
│   └── main/
│       ├── java/com/hik/worker/
│       │   ├── WorkerApplication.java        # Spring Boot 启动类
│       │   ├── config/TemporalWorkerConfig.java  # Worker 注册配置
│       │   ├── activity/                      # Activity 实现（业务执行单元）
│       │   ├── workflow/                      # Workflow 接口与实现
│       │   ├── client/                        # API 客户端
│       │   └── model/                         # 数据模型
│       └── resources/application.yml           # 配置文件（环境变量占位符）
├── pom.xml                       # Maven 构建配置
├── Dockerfile.deploy             # 简化版 Dockerfile（部署服务器用，需新增）
└── docker-compose.yml            # Compose 编排（扩缩容用，需新增）
```

> **注意**：项目原有的 [Dockerfile](file:///Users/apple/Desktop/AdapterService/temporal-workers/Dockerfile) 是多阶段构建版本（包含 Maven 编译），本方案不使用它。需要新增一个 `Dockerfile.deploy` 简化版。

### 3.2 新增 Dockerfile.deploy（简化版单阶段构建）

在项目根目录新建 `Dockerfile.deploy`，**不含 Maven 编译步骤**，仅 COPY Jenkins 编译好的 JAR：

```dockerfile
# 简化版 Dockerfile - 用于部署服务器构建（JAR 由 Jenkins 预编译）
FROM eclipse-temurin:17-jre

WORKDIR /app

# COPY Jenkins 编译好的 JAR（Jenkins 会重命名为 app.jar 传输）
COPY app.jar app.jar

EXPOSE 18090

# 环境变量默认值（docker-compose.yml 会覆盖）
ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENV TEMPORAL_ADDRESS="temporal:7233"
ENV WORKER_SCENARIOS="all"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

**与原 Dockerfile 的对比**：

| 对比项 | 原 Dockerfile（多阶段） | Dockerfile.deploy（简化版） |
| --- | --- | --- |
| 构建阶段 | 2 阶段（Maven 编译 + JRE 运行） | 1 阶段（仅 JRE 运行） |
| 基础镜像 | `maven:3.9-eclipse-temurin-17` + `eclipse-temurin:17-jre` | 仅 `eclipse-temurin:17-jre` |
| Maven 编译 | Docker 构建时执行 `mvn package` | 不执行（Jenkins 预编译） |
| 需要外网 | 需要（下载 Maven 依赖） | 不需要 |
| 构建耗时 | 3-8 分钟 | 10-20 秒 |
| 适用场景 | Jenkins 有 Docker | Jenkins 无 Docker / 离线部署服务器 |

### 3.3 新增 docker-compose.yml

> **重要**：不要配置宿主机端口映射（`ports`），否则多个 Worker 实例会产生端口冲突，无法自由扩缩容。Worker 通过 gRPC 长轮询与 Temporal Server 通信，无需对外暴露端口。

在项目根目录新建 `docker-compose.yml`：

```yaml
services:
  temporal-workers:
    # Jenkins 动态注入 IMAGE_TAG 环境变量
    image: temporal-workers:${IMAGE_TAG:-latest}
    build:
      context: .
      dockerfile: Dockerfile.deploy
    restart: always
    # 不配置 ports，避免多实例端口冲突
    environment:
      - TEMPORAL_ADDRESS=192.168.1.100:7233
      - WORKER_SCENARIOS=all
      - JAVA_OPTS=-Xms256m -Xmx512m
```

**环境变量对照表**（与 [application.yml](file:///Users/apple/Desktop/AdapterService/temporal-workers/src/main/resources/application.yml) 一一对应）：

| 环境变量 | application.yml 占位符 | 默认值 | 用途 |
| --- | --- | --- | --- |
| `TEMPORAL_ADDRESS` | `${TEMPORAL_ADDRESS:127.0.0.1:7233}` | `127.0.0.1:7233` | Temporal Server gRPC 地址 |
| `WORKER_SCENARIOS` | `${WORKER_SCENARIOS:all}` | `all` | 启动的场景（all/demo/scenario1-5，逗号分隔） |
| `JAVA_OPTS` | Dockerfile ENV | `-Xms256m -Xmx512m` | JVM 堆内存参数 |

> **部署前需修改**：将 `docker-compose.yml` 中的 `192.168.1.100` 替换为实际 Temporal Server 的 IP 地址。

---

## 四、Jenkins 环境准备

### 4.1 Jenkins 节点 JDK 17 + Maven 安装

**方式 A：手动安装（推荐）**

```bash
# 1. 下载 JDK 17（Eclipse Temurin）
cd /opt
wget https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.11%2B9/OpenJDK17U-jdk_x64_linux_hotspot_17.0.11_9.tar.gz
tar -xzf OpenJDK17U-jdk_x64_linux_hotspot_17.0.11_9.tar.gz
ln -s /opt/jdk-17.0.11+9 /opt/jdk17

# 2. 下载 Maven 3.9
wget https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz
tar -xzf apache-maven-3.9.6-bin.tar.gz
ln -s /opt/apache-maven-3.9.6 /opt/maven

# 3. 配置环境变量
cat >> /etc/profile.d/jenkins-tools.sh << 'EOF'
export JAVA_HOME=/opt/jdk17
export MAVEN_HOME=/opt/maven
export PATH=$JAVA_HOME/bin:$MAVEN_HOME/bin:$PATH
EOF
source /etc/profile.d/jenkins-tools.sh

# 4. 验证
java -version   # 应显示 17.0.x
mvn -version    # 应显示 3.9.x
```

**方式 B：Jenkins 自动安装**

在 `Manage Jenkins` -> `Global Tool Configuration` 中配置自动安装（Jenkins 会自动下载）。

### 4.2 Jenkins 凭据配置

在 Jenkins 管理界面 `Manage Jenkins` -> `Credentials` 中添加两个凭据：

| 凭据类型 | ID | 说明 |
| --- | --- | --- |
| SSH Username with private key | `centos-worker-ssh-key` | Jenkins 访问部署服务器的 SSH 私钥 |
| Username with password | `gitlab-repo-credentials` | GitLab 仓库的访问账号密码 |

### 4.3 创建 Jenkins Pipeline 任务

1. Jenkins 首页 -> `New Item`
2. 输入任务名称 `temporal-worker-deploy`，选择 `Pipeline`
3. 在 `Pipeline` 配置页：
   - Definition: `Pipeline script`
   - 将下方第五章的 Jenkinsfile 脚本粘贴进去（或选择 `Pipeline script from SCM`，将 Jenkinsfile 存入 GitLab 仓库）

### 4.4 SSH 免密配置验证

```bash
# 在 Jenkins 节点上验证 SSH 免密登录到部署服务器
ssh root@<部署服务器IP> 'echo "SSH连接正常"'

# 如果提示需要密码，需要配置 SSH 密钥
ssh-keygen -t rsa -b 4096                    # 在 Jenkins 节点生成密钥
ssh-copy-id root@<部署服务器IP>               # 将公钥拷贝到部署服务器
```

---

## 五、Jenkins Pipeline 完整脚本

### 5.1 Jenkinsfile

```groovy
pipeline {
    agent any

    // 使用 Jenkins 全局工具配置的 JDK 和 Maven
    tools {
        jdk 'jdk17'
        maven 'maven3.9'
    }

    // 参数化构建：支持传入期望的 Worker 数量和部署配置
    parameters {
        string(
            name: 'DESIRED_WORKERS',
            defaultValue: '2',
            description: '目标 Worker 实例数量（如输入 3：当前 1 个则扩至 3 个，当前 5 个则缩至 3 个）'
        )
        string(
            name: 'BRANCH',
            defaultValue: 'main',
            description: 'GitLab 代码分支'
        )
        string(
            name: 'TARGET_HOST',
            defaultValue: '192.168.1.101',
            description: '部署 Worker 的目标 CentOS 服务器 IP'
        )
        choice(
            name: 'SCENARIOS',
            choices: ['all', 'demo', 'scenario1', 'scenario2', 'scenario3', 'scenario4', 'scenario5'],
            description: 'Worker 启动场景（all=全部, demo=基础演示, scenario1=限流, scenario2=重试, scenario3=崩溃恢复, scenario4=人工审批, scenario5=Webhook回调）'
        )
    }

    environment {
        // 部署目标目录（部署服务器上）
        REMOTE_APP_DIR = '/opt/apps/temporal-workers'
        // SSH 免密登录凭据 ID
        SSH_CRED_ID = 'centos-worker-ssh-key'
        // GitLab 认证凭据 ID
        GITLAB_CRED_ID = 'gitlab-repo-credentials'
        // 镜像名称（与 pom.xml artifactId 一致）
        IMAGE_NAME = 'temporal-workers'
        // 全局唯一构建 Tag（如：build-15）
        IMAGE_TAG = "build-${BUILD_NUMBER}"
        // GitLab 仓库地址（替换为实际地址）
        GITLAB_REPO_URL = 'git@gitlab.your-company.com:project/temporal-workers.git'
        // JAR 包名称（与 pom.xml 中的 artifactId-version 一致）
        JAR_NAME = 'temporal-workers-1.0.0.jar'
    }

    stages {

        stage('1. 拉取源码') {
            steps {
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: "*/${params.BRANCH}"]],
                    userRemoteConfigs: [[
                        credentialsId: "${GITLAB_CRED_ID}",
                        url: "${GITLAB_REPO_URL}"
                    ]]
                ])
                echo "成功拉取代码，分支: ${params.BRANCH}"
            }
        }

        stage('2. Maven 编译 JAR') {
            steps {
                script {
                    echo "正在使用 Maven 编译 JAR 包..."
                    // 编译打包，跳过测试（测试在开发阶段已完成）
                    sh 'mvn clean package -DskipTests -B'
                    // 验证 JAR 包是否生成
                    sh "ls -lh target/${JAR_NAME}"
                    echo "JAR 编译完成: target/${JAR_NAME}"
                }
            }
        }

        stage('3. 传输 JAR 与配置到部署服务器') {
            steps {
                script {
                    sshagent([SSH_CRED_ID]) {
                        // 3.1 创建远程工作目录
                        sh "ssh -o StrictHostKeyChecking=no root@${params.TARGET_HOST} 'mkdir -p ${REMOTE_APP_DIR}'"

                        // 3.2 传输 JAR 包（重命名为 app.jar，与 Dockerfile.deploy 中的 COPY 一致）
                        echo "正在传输 JAR 包到 ${params.TARGET_HOST} ..."
                        sh "scp -o StrictHostKeyChecking=no target/${JAR_NAME} root@${params.TARGET_HOST}:${REMOTE_APP_DIR}/app.jar"

                        // 3.3 传输 Dockerfile.deploy
                        sh "scp -o StrictHostKeyChecking=no Dockerfile.deploy root@${params.TARGET_HOST}:${REMOTE_APP_DIR}/Dockerfile.deploy"

                        // 3.4 传输 docker-compose.yml
                        sh "scp -o StrictHostKeyChecking=no docker-compose.yml root@${params.TARGET_HOST}:${REMOTE_APP_DIR}/docker-compose.yml"

                        echo "文件传输完成：app.jar + Dockerfile.deploy + docker-compose.yml"
                    }
                }
            }
        }

        stage('4. 远程构建 Docker 镜像') {
            steps {
                script {
                    sshagent([SSH_CRED_ID]) {
                        echo "正在部署服务器上构建 Docker 镜像..."
                        sh """
                            ssh -o StrictHostKeyChecking=no root@${params.TARGET_HOST} '
                                cd ${REMOTE_APP_DIR}

                                # 使用简化版 Dockerfile 构建镜像（单阶段，仅 COPY JAR）
                                docker build -f Dockerfile.deploy -t ${IMAGE_NAME}:${IMAGE_TAG} .
                                docker tag ${IMAGE_NAME}:${IMAGE_TAG} ${IMAGE_NAME}:latest

                                echo "镜像构建完成: ${IMAGE_NAME}:${IMAGE_TAG}"
                                docker images | grep ${IMAGE_NAME}
                            '
                        """
                    }
                }
            }
        }

        stage('5. 动态扩缩容 Worker') {
            steps {
                script {
                    sshagent([SSH_CRED_ID]) {
                        sh """
                            ssh -o StrictHostKeyChecking=no root@${params.TARGET_HOST} '
                                cd ${REMOTE_APP_DIR} && \

                                # 注入镜像 Tag 和场景配置
                                export IMAGE_TAG=latest
                                export WORKER_SCENARIOS=${params.SCENARIOS}

                                # 获取变更前的容器数量
                                CURRENT_COUNT=\$(docker compose ps -q temporal-workers 2>/dev/null | wc -l)

                                echo "==============================================="
                                echo "【扩缩容评估】"
                                echo " 镜像: ${IMAGE_NAME}:latest"
                                echo " 场景: ${params.SCENARIOS}"
                                echo " 当前运行中的 Worker 实例数: \$CURRENT_COUNT"
                                echo " 目标期望 Worker 实例数: ${params.DESIRED_WORKERS}"
                                echo "==============================================="

                                # 执行 Compose Scale 扩缩容
                                docker compose up -d --scale temporal-workers=${params.DESIRED_WORKERS} --remove-orphans

                                # 等待 3 秒让容器稳定
                                sleep 3

                                echo "==============================================="
                                echo "【调整结果】当前存活的 Worker 实例列表："
                                docker compose ps
                                echo "==============================================="
                            '
                        """
                    }
                }
            }
        }

        stage('6. 健康检查') {
            steps {
                script {
                    sshagent([SSH_CRED_ID]) {
                        // 检查 Worker 是否成功连接 Temporal Server
                        sh """
                            ssh -o StrictHostKeyChecking=no root@${params.TARGET_HOST} '
                                cd ${REMOTE_APP_DIR}

                                echo "【健康检查】检查 Worker 日志是否出现 WorkerFactory 启动信息..."

                                # 获取第一个 Worker 容器名称
                                CONTAINER_NAME=\$(docker compose ps -q temporal-workers | head -1)

                                if [ -z "\$CONTAINER_NAME" ]; then
                                    echo "ERROR: 未找到运行中的 Worker 容器"
                                    exit 1
                                fi

                                # 检查日志中是否包含启动成功标志
                                docker logs \$CONTAINER_NAME 2>&1 | grep -q "WorkerFactory 已启动"
                                if [ \$? -eq 0 ]; then
                                    echo "OK: Worker 启动成功，已连接 Temporal Server"
                                else
                                    echo "WARN: 未检测到启动成功日志，请检查容器日志"
                                    docker logs --tail 20 \$CONTAINER_NAME
                                fi
                            '
                        """
                    }
                }
            }
        }
    }

    post {
        success {
            echo "Worker 部署与扩缩容成功！目标实例数: ${params.DESIRED_WORKERS}，场景: ${params.SCENARIOS}"
        }
        failure {
            echo "构建或扩缩容失败，请检查 Jenkins 日志"
        }
        always {
            // 清理 Jenkins 节点上的构建产物（释放磁盘）
            sh 'mvn clean -q 2>/dev/null || true'
        }
    }
}
```

### 5.2 Pipeline 阶段说明

| 阶段 | 执行位置 | 动作 | 耗时参考 | 失败处理 |
| --- | --- | --- | --- | --- |
| **1. 拉取源码** | Jenkins | 从 GitLab 拉取指定分支代码 | 数秒 | 检查 GitLab 凭据和仓库地址 |
| **2. Maven 编译 JAR** | Jenkins | `mvn clean package -DskipTests` 编译 JAR | 2-5 分钟（首次较慢，后续有 `.m2` 缓存） | 检查 pom.xml 和源码编译错误 |
| **3. 传输 JAR 与配置** | Jenkins -> 部署服务器 | `scp` 传输 JAR + Dockerfile + compose 文件 | 5-15 秒 | 检查 SSH 连接和磁盘空间 |
| **4. 远程构建镜像** | 部署服务器 | SSH 执行 `docker build -f Dockerfile.deploy` | 10-20 秒（仅 COPY JAR，无 Maven 编译） | 检查基础镜像是否已导入 |
| **5. 动态扩缩容** | 部署服务器 | `docker compose up -d --scale` 自动增减容器 | 10-30 秒 | 检查 Docker Compose V2 |
| **6. 健康检查** | 部署服务器 | 检查 Worker 日志确认连接 Temporal Server | 5 秒 | 查看容器日志排查启动失败 |

### 5.3 各阶段传输内容明细

```
阶段 2 产出（Jenkins 节点）:
├── target/temporal-workers-1.0.0.jar   # ~40MB（Spring Boot Fat JAR）

阶段 3 传输（Jenkins -> 部署服务器）:
├── app.jar                              # ~40MB（重命名后的 JAR）
├── Dockerfile.deploy                    # ~300B（简化版 Dockerfile）
└── docker-compose.yml                   # ~300B（编排配置）
总计传输: ~40MB（远小于镜像传输的 ~400MB）

阶段 4 产出（部署服务器）:
└── temporal-workers:latest              # ~320MB（JRE 基础镜像 + JAR）
```

---

## 六、扩缩容工作机制

### 6.1 Docker Compose Scale 原理

`docker compose up -d --scale temporal-workers=N` 执行时，Compose 自动感知当前容器数量并调整：

```
情景 A：扩容（当前 1 个 -> 目标 3 个）
┌──────────────┐
│ worker-1 ✓   │  保留
├──────────────┤
│ worker-2 NEW │  新建
│ worker-3 NEW │  新建
└──────────────┘

情景 B：缩容（当前 5 个 -> 目标 2 个）
┌──────────────┐
│ worker-1 ✓   │  保留
│ worker-2 ✓   │  保留
├──────────────┤
│ worker-3 X   │  SIGTERM -> 销毁
│ worker-4 X   │  SIGTERM -> 销毁
│ worker-5 X   │  SIGTERM -> 销毁
└──────────────┘

情景 C：无变化（当前 2 个 -> 目标 2 个，代码有更新）
┌──────────────┐
│ worker-1 ⟳   │  滚动更新（旧容器停 -> 新容器起）
│ worker-2 ⟳   │  滚动更新
└──────────────┘
```

### 6.2 优雅停机与零丢失

缩容时 Docker 向被销毁的容器发送 `SIGTERM` 信号：

```
Docker 发送 SIGTERM
    |
    v
Worker 收到信号，停止领取新任务
    |
    v
正在执行的 Activity 继续执行（最多等待 stop_timeout）
    |
    ├── Activity 执行完成 -> 正常汇报结果
    |
    └── 超时被强制终止
            |
            v
        Temporal Server 检测到 Worker 心跳中断
            |
            v
        将未完成的 Task 重新分配给存活的 Worker
            |
            v
        存活 Worker 从断点继续执行（零丢失）
```

### 6.3 Worker 即插即用

新 Worker 启动后只需知道两件事即可工作：

1. **Temporal Server 地址**（`TEMPORAL_ADDRESS` 环境变量）
2. **Task Queue 名称**（代码中已硬编码，见 [TemporalWorkerConfig.java](file:///Users/apple/Desktop/AdapterService/temporal-workers/src/main/java/com/hik/worker/config/TemporalWorkerConfig.java)）

新 Worker 自动通过 gRPC 长轮询连接 Server，领取任务，**无需在 Server 端做任何配置**。

---

## 七、操作指南

### 7.1 首次部署

**步骤 1：准备代码仓库**

确保 GitLab 仓库根目录包含以下文件：

```bash
temporal-workers/
├── src/                    # 业务源码
├── pom.xml                 # Maven 构建配置
├── Dockerfile.deploy       # 新增：简化版 Dockerfile
└── docker-compose.yml      # 新增：编排文件
```

**步骤 2：修改配置**

编辑 `docker-compose.yml`，将 IP 地址替换为实际环境地址：

```yaml
environment:
  - TEMPORAL_ADDRESS=<Temporal服务器IP>:7233
```

**步骤 3：配置 Jenkins 环境**

- 在 Jenkins 节点安装 JDK 17 + Maven（参考第 4.1 节）
- 在 Jenkins Credentials 中配置 SSH 密钥和 GitLab 凭据（参考第 4.2 节）
- 创建 Pipeline 任务并粘贴 Jenkinsfile（参考第 4.3 节）
- 修改 `GITLAB_REPO_URL` 为实际仓库地址

**步骤 4：离线服务器预装基础镜像（仅离线环境）**

```bash
# 在离线部署服务器上导入 JRE 基础镜像
docker load -i eclipse-temurin-17-jre.tar

# 验证
docker images | grep eclipse-temurin
```

**步骤 5：执行构建**

- 点击 `Build with Parameters`
- 设置参数：
  - `DESIRED_WORKERS` = `2`（首次部署 2 个实例）
  - `BRANCH` = `main`
  - `TARGET_HOST` = 部署服务器 IP
  - `SCENARIOS` = `all`
- 点击 `Build`，等待 Pipeline 执行完成

**步骤 6：验证**

```bash
# SSH 到部署服务器
ssh root@<部署服务器IP>

# 查看容器运行状态
cd /opt/apps/temporal-workers
docker compose ps

# 查看 Worker 日志
docker compose logs -f temporal-workers

# 确认日志中出现以下关键信息：
# "连接 Temporal Server: <IP>:7233"
# "启动Worker场景: [all]"
# "Demo Worker已注册, taskQueue: HIK_BATCH_TASK_QUEUE"
# "WorkerFactory 已启动"
```

### 7.2 日常扩容

当 Task Queue 积压、需要提升处理能力时：

1. Jenkins -> `temporal-worker-deploy` -> `Build with Parameters`
2. 将 `DESIRED_WORKERS` 从 `2` 改为 `4`
3. 其他参数保持不变
4. 点击 `Build`

Pipeline 会自动在部署服务器上新增 2 个 Worker 实例，整个过程约 30 秒。

> **扩容时是否需要重新编译 JAR？** 不需要。Pipeline 每次都会执行完整流程（编译 -> 传输 -> 构建 -> 扩缩容），但如果代码没变化，Maven 编译会很快（有 `.m2` 缓存），Docker 构建也很快（有层缓存）。如果只想扩容不重新编译，可以跳过阶段 1-4，直接在部署服务器执行 `docker compose up -d --scale temporal-workers=4`。

### 7.3 日常缩容

流量回落后缩减实例：

1. Jenkins -> `Build with Parameters`
2. 将 `DESIRED_WORKERS` 从 `4` 改为 `2`
3. 点击 `Build`

Docker Compose 会优雅停掉 2 个多余实例，正在执行的任务自动转移至存活的 2 个 Worker。

### 7.4 代码更新发布

当 Worker 业务代码更新后：

1. 开发者将代码推送到 GitLab
2. Jenkins -> `Build with Parameters`
3. `DESIRED_WORKERS` 保持当前数量不变
4. 点击 `Build`

Pipeline 会重新编译 JAR、传输、构建镜像，Docker Compose 检测到镜像变化后执行滚动更新。

### 7.5 按场景单独部署

通过 `SCENARIOS` 参数控制只启动特定场景的 Worker：

| 参数值 | 启动的场景 | Task Queue |
| --- | --- | --- |
| `all` | 全部场景 | 所有队列 |
| `demo` | 基础演示（顺序/并发/超时批量校验） | `HIK_BATCH_TASK_QUEUE` |
| `scenario1` | 限流场景（20 QPS） | `RATE_LIMIT_TASK_QUEUE` |
| `scenario2` | 重试场景（指数退避，最多 5 次） | `RETRY_TASK_QUEUE` |
| `scenario3` | 崩溃恢复（断点续传） | `CRASH_RECOVERY_TASK_QUEUE` |
| `scenario4` | 人工审批（Signal 等待） | `HUMAN_LOOP_TASK_QUEUE` |
| `scenario5` | Webhook 回调 | `WEBHOOK_TASK_QUEUE` |

> 可以在不同服务器上部署不同场景的 Worker，实现资源隔离。例如：计算密集型场景（scenario1 限流）单独部署在 CPU 较强的服务器上。

---

## 八、多服务器部署方案

当单台服务器资源不足时，可将 Worker 分散到多台服务器。每台服务器执行一次 Jenkins Pipeline 即可：

```
服务器 D (192.168.1.101)     服务器 E (192.168.1.102)     服务器 F (192.168.1.103)
┌──────────────────┐       ┌──────────────────┐       ┌──────────────────┐
│ temporal-workers │       │ temporal-workers │       │ temporal-workers │
│   ├── worker-1   │       │   ├── worker-1   │       │   ├── worker-1   │
│   └── worker-2   │       │   └── worker-2   │       │   └── worker-2   │
└────────┬─────────┘       └────────┬─────────┘       └────────┬─────────┘
         |                          |                          |
         +--------------------------+--------------------------+
                                    |
                                    v
                         ┌──────────────────┐
                         │ Temporal Server   │
                         │ 192.168.1.100:7233│
                         └──────────────────┘
```

**操作方式**：在 Jenkins 构建时修改 `TARGET_HOST` 参数，分别为每台服务器执行一次部署。

> 所有服务器上的 Worker 只要 `TEMPORAL_ADDRESS` 指向同一个 Temporal Server，就会自动加入同一个 Worker 池，共同消费 Task Queue 中的任务。

---

## 九、故障排查

### 9.1 常见问题与解决

| 问题 | 可能原因 | 解决方案 |
| --- | --- | --- |
| Maven 编译失败 | JDK 版本不对或依赖下载失败 | 确认 Jenkins 使用 JDK 17；检查网络访问 Maven Central |
| JAR 传输失败 | SSH 连接超时或磁盘空间不足 | 验证 SSH 免密；清理部署服务器磁盘 `df -h` |
| Docker 构建失败 | 基础镜像未导入（离线环境） | 执行 `docker load -i eclipse-temurin-17-jre.tar` 导入基础镜像 |
| Worker 启动后立即退出 | Temporal Server 不可达 | 检查 `TEMPORAL_ADDRESS` 配置；确认 Temporal Server 已启动 |
| Worker 日志报 `Connection refused` | 网络不通或端口错误 | 在部署服务器执行 `telnet <Temporal_IP> 7233` 验证连通性 |
| `docker compose scale` 报端口冲突 | compose 文件配置了 `ports` | 删除 `docker-compose.yml` 中的 `ports` 配置 |
| Worker 注册但未消费任务 | Task Queue 名称不匹配 | 确认 Worker 代码中的 Task Queue 名称与 Workflow Starter 一致 |

### 9.2 日志排查命令

```bash
# 查看所有 Worker 容器状态
cd /opt/apps/temporal-workers
docker compose ps

# 查看实时日志（所有实例）
docker compose logs -f

# 查看指定实例日志
docker compose logs -f --index 1 temporal-workers

# 查看 Worker 启动关键日志
docker compose logs temporal-workers | grep -E "Worker|taskQueue|连接|启动"

# 进入容器排查
docker exec -it <容器名> sh

# 检查 Temporal Server 连通性
docker exec -it <容器名> sh -c 'echo "测试连接: $TEMPORAL_ADDRESS"'
```

### 9.3 验证 Worker 健康

```bash
# 在 Temporal Server 所在机器执行
# 查看 Task Queue 的 Worker Poller 信息
docker exec -it temporal-server temporal task-queue describe --task-queue HIK_BATCH_TASK_QUEUE

# 输出中 "Pollers" 部分应列出所有活跃的 Worker 实例
```

### 9.4 手动重新部署（绕过 Jenkins）

如果 Jenkins 故障需要手动部署：

```bash
# 1. 在开发机编译 JAR
cd /path/to/temporal-workers
mvn clean package -DskipTests

# 2. 传输到部署服务器
scp target/temporal-workers-1.0.0.jar root@<部署服务器IP>:/opt/apps/temporal-workers/app.jar
scp Dockerfile.deploy root@<部署服务器IP>:/opt/apps/temporal-workers/
scp docker-compose.yml root@<部署服务器IP>:/opt/apps/temporal-workers/

# 3. 在部署服务器构建并启动
ssh root@<部署服务器IP>
cd /opt/apps/temporal-workers
docker build -f Dockerfile.deploy -t temporal-workers:latest .
docker compose up -d --scale temporal-workers=2
```

---

## 十、附录

### 10.1 配置文件引用关系

```
docker-compose.yml (environment)
        |
        v
  环境变量 (TEMPORAL_ADDRESS, WORKER_SCENARIOS, ...)
        |
        v
  application.yml (${ENV_VAR:default})
        |
        v
  Java 代码 (@Value("${temporal.server.address}"))
        |
        v
  TemporalWorkerConfig.java (Worker 注册与启动)
```

### 10.2 文件传输清单

每次 Jenkins 构建会向部署服务器传输以下文件：

| 文件 | 源（Jenkins 节点） | 目标（部署服务器） | 大小 | 用途 |
| --- | --- | --- | --- | --- |
| `app.jar` | `target/temporal-workers-1.0.0.jar` | `${REMOTE_APP_DIR}/app.jar` | ~40MB | Worker 应用 JAR |
| `Dockerfile.deploy` | 仓库根目录 | `${REMOTE_APP_DIR}/Dockerfile.deploy` | ~300B | Docker 构建脚本 |
| `docker-compose.yml` | 仓库根目录 | `${REMOTE_APP_DIR}/docker-compose.yml` | ~300B | Compose 编排配置 |

### 10.3 相关文档

| 文档 | 说明 |
| --- | --- |
| [Temporal业务应用方案介绍.md](file:///Users/apple/Desktop/AdapterService/docs/Temporal业务应用方案介绍.md) | 业务场景痛点与 Temporal 解决方案总览 |
| [temporal-worker自动扩缩容方案.md](file:///Users/apple/Desktop/AdapterService/docs/temporal-worker自动扩缩容方案.md) | 原始 Jenkins Pipeline 方案（通用示例版） |
| [在线拉取并导出Temporal-Worker基础镜像.md](file:///Users/apple/Desktop/AdapterService/docs/在线拉取并导出Temporal-Worker基础镜像.md) | 在线服务器拉取并导出 Docker 基础镜像指南 |
| [离线服务器安装部署temporal.md](file:///Users/apple/Desktop/AdapterService/离线服务器安装部署temporal.md) | 离线服务器 Temporal 集群安装部署指南 |
| [Temporal迁移技术分析报告.md](file:///Users/apple/Desktop/AdapterService/docs/Temporal迁移技术分析报告.md) | 迁移优先级评估与分阶段路线图 |
