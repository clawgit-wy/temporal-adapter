针对你标准的 CI/CD 流程——**“GitLab 托管 Worker 代码 ➔ Jenkins 拉取代码并构建镜像 ➔ 依据仓库中的 `docker-compose.yml` 自动计算并执行扩缩容”**，这里为你提供一套完整且符合生产规范的 Jenkins Pipeline 实现方案。

---

## 一、 项目代码仓库准备（GitLab 仓库中的文件结构）

在你的 GitLab 仓库根目录下，准备以下两个核心配置文件：

### 1. `docker-compose.yml`

配置 Worker 服务。**切记：绝对不要配置宿主机端口映射（`ports`）**，这样 Compose 才能实现自由无冲突的扩缩容。

```yaml
version: '3.8'

services:
  temporal-worker:
    # 这里的 ${IMAGE_TAG} 会由 Jenkins 动态注入
    image: ${REGISTRY_URL}/temporal-worker:${IMAGE_TAG:-latest}
    build:
      context: .
      dockerfile: Dockerfile
    restart: always
    environment:
      - TEMPORAL_SERVER_HOST=192.168.1.100:7233 # 第一台服务器的 Temporal Server IP
      - TASK_QUEUE=HEAVY_TASK_QUEUE

```

### 2. `Dockerfile`（示例）

根目录放置编译/构建 Worker 镜像的 Dockerfile（以 Java/Go 为例，根据实际技术栈修改）：

```dockerfile
FROM openjdk:17-slim
WORKDIR /app
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

```

---

## 二、 Jenkins Pipeline 脚本方案

在 Jenkins 中新建一个 **Pipeline 任务**，绑定你的 GitLab 仓库地址。

这个 Pipeline 会按顺序执行 4 个步骤：

1. **从 GitLab 拉取代码**；
2. **在 Jenkins 节点本地构建/打包 Worker Docker 镜像**；
3. **将打包好的镜像发布/传输到目标 CentOS 服务器**（可通过本地镜像加载或私有镜像仓库）；
4. **通过 `docker compose up -d --scale` 自动对比当前容器数并扩缩容至指定目标（`DESIRED_WORKERS`）**。

### 完整 `Jenkinsfile` 脚本：

```groovy
pipeline {
    agent any

    // 1. 参数化构建：支持传入期望的 Worker 数量
    parameters {
        string(
            name: 'DESIRED_WORKERS', 
            defaultValue: '2', 
            description: '目标 Worker 实例数量（如输入 2：当前 1 个则扩至 2 个，当前 5 个则缩至 2 个）'
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
    }

    environment {
        // 部署的目标目录（远程服务器上）
        REMOTE_APP_DIR = '/opt/apps/temporal-worker'
        // SSH 免密登录凭据（在 Jenkins "Credentials" 中配置）
        SSH_CRED_ID = 'centos-worker-ssh-key'
        // GitLab 认证凭据 ID
        GITLAB_CRED_ID = 'gitlab-repo-credentials'
        // 自动提取全局唯一的 构建 Tag (如：build-15)
        IMAGE_TAG = "build-${BUILD_NUMBER}"
    }

    stages {
        stage('1. 从 GitLab 源码拉取') {
            steps {
                // 自动拉取 GitLab 仓库源码
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: "*/${params.BRANCH}"]],
                    userRemoteConfigs: [[
                        credentialsId: "${GITLAB_CRED_ID}",
                        url: 'git@gitlab.your-company.com:project/temporal-worker.git' // 替换你的 GitLab 仓库地址
                    ]]
                ])
                echo "成功拉取代码，分支: ${params.BRANCH}"
            }
        }

        stage('2. 构建 Docker 镜像') {
            steps {
                script {
                    echo "正在构建 Worker 镜像，Tag: ${IMAGE_TAG}..."
                    // 先进行代码编译（如果是 Java 可先 sh 'mvn clean package -DskipTests'，如果是 Go/Node 可跳过直接 build）
                    sh "docker build -t temporal-worker:${IMAGE_TAG} ."
                    sh "docker tag temporal-worker:${IMAGE_TAG} temporal-worker:latest"
                }
            }
        }

        stage('3. 同步镜像与 Compose 文件到目标服务器') {
            steps {
                script {
                    sshagent([SSH_CRED_ID]) {
                        // 1. 在目标服务器创建工作目录
                        sh "ssh -o StrictHostKeyChecking=no root@${params.TARGET_HOST} 'mkdir -p ${REMOTE_APP_DIR}'"
                        
                        // 2. 将最新的 docker-compose.yml 拷贝至目标服务器
                        sh "scp -o StrictHostKeyChecking=no docker-compose.yml root@${params.TARGET_HOST}:${REMOTE_APP_DIR}/"
                        
                        // 3. 将本地构建好的镜像打包并传输到目标服务器（若有私有镜像仓库，可用 docker push/pull 代替）
                        sh """
                            docker save temporal-worker:latest | ssh -o StrictHostKeyChecking=no root@${params.TARGET_HOST} 'docker load'
                        """
                    }
                }
            }
        }

        stage('4. 自动对比并动态扩/缩容 Worker') {
            steps {
                script {
                    sshagent([SSH_CRED_ID]) {
                        sh """
                            ssh -o StrictHostKeyChecking=no root@${params.TARGET_HOST} '
                                cd ${REMOTE_APP_DIR} && \
                                
                                # 动态传入镜像 Tag 环境变量
                                export IMAGE_TAG=latest
                                
                                # 获取变更前的容器数量
                                CURRENT_COUNT=\$(docker compose ps -q temporal-worker 2>/dev/null | wc -l)
                                
                                echo "==============================================="
                                echo "【扩缩容评估】"
                                echo " 当前运行中的 Worker 实例数: \$CURRENT_COUNT"
                                echo " 目标期望运行的 Worker 实例数: ${params.DESIRED_WORKERS}"
                                echo "==============================================="
                                
                                # 执行 Compose Scale 原生增缩容指令
                                docker compose up -d --scale temporal-worker=${params.DESIRED_WORKERS} --remove-orphans
                                
                                echo "==============================================="
                                echo "【调整结果确认】当前存活的 Worker 实例列表："
                                docker compose ps
                                echo "==============================================="
                            '
                        """
                    }
                }
            }
        }
    }

    post {
        success {
            echo "✅ Worker 部署与动态扩缩容成功！当前状态已满足 ${params.DESIRED_WORKERS} 个实例的要求。"
        }
        failure {
            echo "❌ 构建或扩缩容失败，请检查 Jenkins 日志！"
        }
    }
}

```

---

## 三、 这套方案的完整工作机制说明

1. **自动识别并对比现状（目标与实际）：**
在 `docker compose up -d --scale temporal-worker=${DESIRED_WORKERS}` 执行时，Docker Compose 会自动感知目标 CentOS 上当前服务名对应的容器数量：
* **情景 A（当前 1 个，参数传 2）**：Compose 会在保留现有容器的同时，立刻拉起 `temporal-worker-2`，瞬间实现**扩容**。
* **情景 B（当前 5 个，参数传 2）**：Compose 会平滑停掉并销毁 `temporal-worker-3`、`4`、`5`，留下 `1` 和 `2`，瞬间实现**缩容**。
* **情景 C（当前 2 个，参数传 2）**：若代码有更新，Compose 会以滚动重载（Rolling Update）方式刷新镜像；若代码无变化，则保持现状不变（具有完美的**幂等性**）。


2. **零阻断与优雅停机（Graceful Shutdown）：**
当 Jenkins 下发命令缩容（如从 5 个缩到 2 个）时，Docker 会对被销毁的 3 个容器发送 `SIGTERM` 信号。若有正在执行的 Temporal Activity，Worker 有时间平滑处理；即使超时被强制杀掉，Temporal Server 也会将未完成的 Task 重试并自动转移到存活的 2 个 Worker 上，**绝不丢数据**。