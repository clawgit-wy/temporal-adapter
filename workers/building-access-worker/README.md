# Building Access Worker

## 项目简介

基于 Temporal 的楼宇门禁业务工作流 Worker 服务，承担 safety-service-master 拆分后的门禁业务编排职责。对接富士、新开普两套门禁设备系统，提供大规模权限下发、设备数据同步、权限全量初始化、人员变更权限确认 4 大业务工作流。单 Worker 容器包含全部 4 个工作流场景，通过 `WORKER_SCENARIOS` 环境变量控制启动哪些场景，支持全量部署或按场景拆分实例独立扩缩容。

## 技术栈

- Java 8 + Spring Boot 2.7.18
- Temporal SDK 1.35.0
- OkHttp 4.12.0
- Jackson
- Lombok

## 项目结构

```
building-access-worker/
├── src/main/java/com/hik/worker/
│   ├── BuildingAccessWorkerApplication.java   # Spring Boot 启动类
│   ├── client/
│   │   ├── FujiApiClient.java                 # 富士 API 客户端（5个接口 + 注销/查询）
│   │   └── XinkaipuApiClient.java             # 新开普 API 客户端（5个接口）
│   ├── model/                                 # 数据模型（21个类）
│   │   ├── PermissionDistributionRequest/Result  # 权限下发请求/结果
│   │   ├── DeviceSyncRequest/Result/ActivityRequest  # 设备同步
│   │   ├── PermissionInitRequest/Result        # 权限初始化
│   │   ├── DepartConfirmRequest/Result         # 人员变更确认
│   │   ├── DistributionProgress                # 进度查询
│   │   ├── StaffInfo / StaffResult             # 人员/结果
│   │   ├── CheckTokenRequest/Response          # 发行校验
│   │   ├── FaceIssueRequest/Response           # 人脸发行
│   │   ├── FaceIncrementRequest                # 增量权限
│   │   ├── CardIssueRequest/Response           # 卡发行
│   │   └── UnregisterRequest/Response          # 权限注销
│   ├── activity/                              # Activity 层（12组接口+实现）
│   │   ├── CheckTokenActivity                 # 发行校验
│   │   ├── FaceIssueActivity                  # 人脸发行
│   │   ├── FaceModifyActivity                 # 人脸改写
│   │   ├── CardIssueActivity                  # 卡发行
│   │   ├── FaceIncrementActivity              # 增量权限
│   │   ├── PermissionCancelActivity           # 权限注销
│   │   ├── WebhookActivity                    # Webhook 回调
│   │   ├── DeviceSyncActivity                 # 设备同步
│   │   ├── PermissionQueryActivity            # 权限查询
│   │   ├── LogActivity                        # 日志管理
│   │   ├── NotificationActivity               # 消息通知
│   │   └── ValidateActivity                   # 白名单/权限校验
│   └── workflow/                              # Workflow 层（4组接口+实现）
│       └── impl/
│           ├── PermissionDistributionWorkflowImpl  # 大规模权限下发
│           ├── DeviceSyncWorkflowImpl              # 设备数据同步
│           ├── PermissionInitWorkflowImpl          # 权限全量初始化
│           └── DepartConfirmWorkflowImpl           # 人员变更权限确认
├── src/main/resources/
│   ├── application.yml                        # 主配置（端口 18092）
│   ├── application-dev.yml                    # 开发环境
│   ├── application-test.yml                   # 测试环境
│   └── application-prod.yml                   # 生产环境
├── Dockerfile                                 # 多阶段构建（Maven编译 -> JRE运行）
├── Jenkinsfile                                # Jenkins Pipeline（BUILD/DEPLOY/SCALE/START/STOP/RESTART/STATUS）
├── docs/
│   └── 楼宇门禁业务工作流.md                    # 详细工作流设计文档
├── pom.xml
└── README.md
```

## 工作流一览

| 工作流 | TaskQueue | 场景标识 | 并发/QPS | 说明 |
|--------|-----------|----------|----------|------|
| PermissionDistributionWorkflow | PERMISSION_DISTRIBUTION_TASK_QUEUE | permissionDistribution | 20 / 20 | 大规模权限下发：白名单校验 → 创建日志 → 并行下发(校验→人脸→卡) → 汇总 → 更新日志 → 通知 → Webhook |
| DeviceSyncWorkflow | DEVICE_SYNC_TASK_QUEUE | deviceSync | 10 | 设备数据同步：获取总数 → 并行分页拉取 → 合并 → 保存 → 标记移除 → 通知新设备 → Webhook |
| PermissionInitWorkflow | PERMISSION_INIT_TASK_QUEUE | permissionInit | 15 | 权限全量初始化：新开普拉取(并行) → 富士拉取 → 合并去重 → 创建日志 → Webhook |
| DepartConfirmWorkflow | DEPART_CONFIRM_TASK_QUEUE | departConfirm | 15 | 人员变更确认：按部门分组 → 并行处理(保留/取消) → 汇总 → Webhook |

## 本地开发

### 环境要求

- JDK 8+
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
mvn spring-boot:run -Dspring-boot.run.arguments=--worker.scenarios=permissionDistribution,deviceSync
```

### 编译打包

```bash
mvn clean package -DskipTests        # 开发环境
mvn clean package -DskipTests -Pprod # 生产环境
```

## Docker 镜像构建

```bash
# 在 workers/building-access-worker/ 目录下执行
docker build -t building-access-worker:1.0.0 .

# 单独运行容器
docker run -d \
  -e TEMPORAL_ADDRESS=127.0.0.1:7233 \
  -e FUJI_API_BASE_URL=http://10.66.32.54:60009 \
  -e XINKAIPU_API_BASE_URL=http://10.66.32.110 \
  -e WEBHOOK_CALLBACK_URL=https://your-business-system.com/api/webhook/callback \
  -e WORKER_SCENARIOS=all \
  -p 18092:18092 \
  building-access-worker:1.0.0
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
| `WEBHOOK_CALLBACK_URL` | Webhook 回调地址 | https://your-business-system.com/api/webhook/callback |
| `WORKER_SCENARIOS` | 启动的场景 | all |
| `JAVA_OPTS` | JVM 参数 | -Xms256m -Xmx512m |

### WORKER_SCENARIOS 可选值

| 值 | 说明 | TaskQueue | Jenkins Worker 名 |
|----|------|-----------|-------------------|
| `all` | 启动所有 4 个场景（默认） | - | - |
| `permissionDistribution` | 大规模权限下发（并发20/QPS20） | PERMISSION_DISTRIBUTION_TASK_QUEUE | worker-permission-distribution |
| `deviceSync` | 设备数据同步（并发10） | DEVICE_SYNC_TASK_QUEUE | worker-device-sync |
| `permissionInit` | 权限全量初始化（并发15） | PERMISSION_INIT_TASK_QUEUE | worker-permission-init |
| `departConfirm` | 人员变更权限确认（并发15） | DEPART_CONFIRM_TASK_QUEUE | worker-depart-confirm |

可组合使用，如 `permissionDistribution,deviceSync`。

## Jenkins 部署

Jenkinsfile 支持 BUILD / DEPLOY / SCALE / START / STOP / RESTART / STATUS 七种操作，通过 `WORKER_NAME` 参数选择具体 Worker（对应 docker-compose 服务名）。详见 `Jenkinsfile` 注释。

## 开发指南

### 新增工作流

1. 在 `workflow/` 下创建 `@WorkflowInterface` 接口（如需人工干预增加 `@SignalMethod`，如需进度查询增加 `@QueryMethod`）
2. 在 `workflow/impl/` 下创建实现类，通过 `Workflow.newActivityStub()` 创建 Activity 代理
3. 在 `TemporalWorkerConfig` 中注册：新增 TaskQueue 常量 + Worker 注册块（含限流配置）
4. 在 `application.yml` 的 `worker.scenarios` 默认值中追加新场景标识

### 新增第三方接口

1. 在 `client/` 下创建 API Client，封装 HTTP 调用（OkHttpClient 设置 connectTimeout=5s, readTimeout=15s）
2. 在 `activity/` 下创建 `@ActivityInterface` 接口和 `@Component` 实现类
3. 在 `application-*.yml` 中添加接口配置（base-url、路径、认证参数）
4. 在 `TemporalWorkerConfig` 中将 Activity 注册到对应 Worker

### 开发规范

- **Workflow**: 纯逻辑编排，不直接调用外部服务；使用 `Workflow.getLogger()` 和 `Workflow.currentTimeMillis()`
- **Activity**: 必须保证幂等性；异常通过 `Activity.wrap(e)` 包装后抛出
- **Client**: 必须设置连接超时（5s）和读取超时（15s）；HTTP 5xx 抛出异常触发重试
- **命名**: Workflow 接口 `{业务名}Workflow`，Activity 接口 `{业务名}Activity`，Client `{系统名}ApiClient`，TaskQueue 常量 `TASK_QUEUE_{大写业务名}`，场景标识 `{小驼峰业务名}`

## 相关文档

- [楼宇门禁业务工作流设计](docs/楼宇门禁业务工作流.md)
