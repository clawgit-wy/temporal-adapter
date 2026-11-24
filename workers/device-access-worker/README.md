# Device Access Worker

## 项目简介

基于 Temporal 的楼宇门禁权限下发 Worker 服务，负责向富士、新开普等门禁设备下发人员权限。单 Worker 包含全部 6 个工作流场景，通过 `WORKER_SCENARIOS` 环境变量控制启动哪些场景。

## 技术栈

- Java 17 + Spring Boot 3.2.5
- Temporal SDK 1.35.0
- OkHttp 4.12.0
- Lombok

## 项目结构

```
device-access-worker/
├── src/main/java/com/hik/worker/
│   ├── DeviceAccessWorkerApplication.java   # Spring Boot 启动类
│   ├── config/
│   │   └── TemporalWorkerConfig.java        # Temporal Worker 配置（6个场景）
│   ├── client/
│   │   ├── FujiApiClient.java               # 富士 API 客户端（5个接口）
│   │   └── XinkaipuApiClient.java           # 新开普 API 客户端（5个接口）
│   ├── model/                               # 数据模型（15个类）
│   ├── activity/                            # Activity 层（7组接口+实现）
│   └── workflow/                            # Workflow 层（6组接口+实现）
│       └── impl/
├── src/main/resources/
│   ├── application.yml                      # 主配置
│   ├── application-dev.yml                  # 开发环境
│   ├── application-test.yml                 # 测试环境
│   └── application-prod.yml                 # 生产环境
├── src/test/resources/                      # temporal-cli 测试脚本
├── Dockerfile                               # 多阶段构建（Maven编译 -> JRE运行）
├── pom.xml
└── README.md
```

## 本地开发

### 环境要求

- JDK 17+
- Maven 3.8+
- Temporal Server（开发环境默认地址 `127.0.0.1:7233`）

### 编译运行

```bash
# 编译
mvn clean compile

# 本地运行（开发环境，默认启动所有场景）
mvn spring-boot:run

# 指定环境
mvn spring-boot:run -Dspring-boot.run.profiles=test

# 指定场景
mvn spring-boot:run -Dspring-boot.run.arguments=--worker.scenarios=permissionIssue,batchIssue
```

### 编译打包

```bash
mvn clean package -DskipTests        # 开发环境
mvn clean package -DskipTests -Pprod # 生产环境
```

## Docker 镜像构建

```bash
# 在 workers/device-access-worker/ 目录下执行
docker build -t device-access-worker:1.0.0 .

# 单独运行容器
docker run -d \
  -e TEMPORAL_ADDRESS=127.0.0.1:7233 \
  -e FUJI_API_BASE_URL=http://10.66.32.54:60009 \
  -e XINKAIPU_API_BASE_URL=http://10.66.32.110 \
  -e WORKER_SCENARIOS=all \
  -p 18091:18091 \
  device-access-worker:1.0.0
```

## 配置说明

### 多环境配置

| 环境 | Profile | 富士API | 新开普API | Temporal |
|------|---------|---------|----------|----------|
| 开发 | dev（默认） | http://10.66.230.210:60009 | http://10.66.226.90 | 127.0.0.1:7233 |
| 测试 | test | http://10.66.230.210:60009 | http://10.66.226.90 | test-temporal:7233 |
| 生产 | prod | http://10.66.32.54:60009 | http://10.66.32.110 | prod-temporal:7233 |

### 环境变量

| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| `TEMPORAL_ADDRESS` | Temporal Server 地址 | 127.0.0.1:7233 |
| `FUJI_API_BASE_URL` | 富士 API 地址 | http://10.66.230.210:60009 |
| `XINKAIPU_API_BASE_URL` | 新开普 API 地址 | http://10.66.226.90 |
| `WEBHOOK_CALLBACK_URL` | Webhook 回调地址 | - |
| `WORKER_SCENARIOS` | 启动的场景 | all |
| `JAVA_OPTS` | JVM 参数 | -Xms256m -Xmx512m |

### WORKER_SCENARIOS 可选值

| 值 | 说明 | TaskQueue |
|----|------|-----------|
| `all` | 启动所有场景（默认） | - |
| `permissionIssue` | 权限下发 | PERMISSION_ISSUE_TASK_QUEUE |
| `permissionModify` | 权限改写 | PERMISSION_MODIFY_TASK_QUEUE |
| `permissionCancel` | 权限注销 | PERMISSION_CANCEL_TASK_QUEUE |
| `permissionIncrement` | 增量权限 | PERMISSION_INCREMENT_TASK_QUEUE |
| `batchIssue` | 批量下发（20QPS限流） | BATCH_ISSUE_TASK_QUEUE |
| `visitorIssue` | 访客下发（涉密过滤） | VISITOR_ISSUE_TASK_QUEUE |

可组合使用，如 `permissionIssue,batchIssue`。

## 开发指南

### 新增工作流

1. 在 `workflow/` 下创建 `@WorkflowInterface` 接口
2. 在 `workflow/impl/` 下创建实现类，通过 `Workflow.newActivityStub()` 创建 Activity 代理
3. 在 `TemporalWorkerConfig` 中注册：新增 TaskQueue 常量 + Worker 注册块

### 新增第三方接口

1. 在 `client/` 下创建 API Client，封装 HTTP 调用
2. 在 `activity/` 下创建 `@ActivityInterface` 接口和 `@Component` 实现类
3. 在 `application-*.yml` 中添加接口配置
4. 在 `TemporalWorkerConfig` 中将 Activity 注册到对应 Worker

### 开发规范

- **Workflow**: 纯逻辑编排，不直接调用外部服务；使用 `Workflow.getLogger()` 和 `Workflow.currentTimeMillis()`
- **Activity**: 必须保证幂等性；异常通过 `Activity.wrap(e)` 包装后抛出
- **Client**: 必须设置连接超时和读取超时；HTTP 5xx 抛出异常触发重试
- **命名**: Workflow 接口 `{业务名}Workflow`，Activity 接口 `{业务名}Activity`，Client `{系统名}ApiClient`

## 相关文档

- [楼宇门禁权限下发业务逻辑](../docs/楼宇门禁权限下发业务逻辑.md)
- [楼宇门禁权限下发工作流设计](../docs/楼宇门禁权限下发工作流.md)
- [测试说明](src/test/resources/README.md)
