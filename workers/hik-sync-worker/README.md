# Hik Sync Worker

## 项目简介

基于 Temporal 的海康数据同步 Worker 服务，从安保系统 MySQL 数据库增量同步人员/人脸数据到海康中间库 PostgreSQL，供海康人脸识别设备消费。

## 技术栈

- Java 8 + Spring Boot 2.7.18
- Temporal SDK 1.35.0
- OkHttp 4.12.0
- Jackson
- Lombok
- Druid 连接池
- Guava

## 项目结构

```
hik-sync-worker/
├── src/main/java/com/hik/worker/
│   ├── HikSyncWorkerApplication.java   # Spring Boot 启动类
│   ├── activity/
│   │   ├── HikSyncActivity.java        # 海康同步 Activity 接口（6个方法）
│   │   └── HikSyncActivityImpl.java    # 海康同步 Activity 实现（双数据源查询+写入）
│   ├── cli/
│   │   └── TemporalCli.java            # Java CLI 工具（start/query/describe/list）
│   ├── config/
│   │   ├── HikDataSourceConfig.java    # 双数据源配置（MySQL + PostgreSQL）
│   │   └── TemporalWorkerConfig.java   # Temporal Worker 配置（注册海康同步工作流）
│   ├── model/
│   │   ├── HkMiddleTableVO.java        # 海康中间库数据传输对象
│   │   ├── HkSyncRequest.java          # 同步请求模型
│   │   └── HkSyncResult.java           # 同步结果模型
│   └── workflow/
│       ├── HikSyncWorkflow.java        # 海康同步工作流接口
│       └── impl/
│           └── HikSyncWorkflowImpl.java # 海康同步工作流实现
├── src/main/resources/
│   ├── application.yml                 # 主配置（端口 18092）
│   ├── application-dev.yml             # 开发环境
│   ├── application-test.yml            # 测试环境
│   └── application-prod.yml            # 生产环境
├── src/test/java/com/hik/worker/
│   ├── HikSyncWorkflowTest.java       # 工作流测试类
│   └── cli/
│       └── TemporalCliRunner.java      # CLI 测试工具
├── docs/
│   ├── 海康数据同步业务逻辑.md
│   └── 海康数据同步使用说明.md
├── tests/
│   └── 海康数据同步本地运行与测试指南.md
├── Dockerfile
├── Jenkinsfile
├── pom.xml
└── README.md
```

## 工作流信息

| 属性 | 值 |
|------|-----|
| 工作流接口 | `com.hik.worker.workflow.HikSyncWorkflow` |
| 实现类 | `com.hik.worker.workflow.impl.HikSyncWorkflowImpl` |
| TaskQueue | `HIK_SYNC_TASK_QUEUE` |
| 场景标识 | `hikSync` |
| 最大并发 | 10 |
| Activity | `HikSyncActivity`（6个方法） |
| 进度查询 | `@QueryMethod getSyncProgress()` |

## 同步业务流程

```
开始
  │
  ├─ Step 1: 计算时间窗口
  │    ├─ 增量模式: start = now - timeWindowMinutes, end = now
  │    └─ 自定义模式: 使用请求中的 customStartTime / customEndTime
  │
  ├─ Step 2: 并行查询4类增量数据 (Async.function)
  │    ├─ queryBlackMessageList()      → 黑名单数据
  │    ├─ queryEmployeeMessageList()   → 员工台账数据
  │    ├─ queryEnterRegisterList()     → 外来人员备案数据
  │    └─ queryVisitorHairpinList()    → 访客数据
  │
  ├─ Step 3: 合并所有数据
  │
  ├─ Step 4: 人脸图片处理 (processFaceImages)
  │    ├─ 必填字段校验 (jobNo, certificateNo, faceUrl, name)
  │    ├─ 图片URL处理:
  │    │    ├─ 照片扩展名 → 直接使用
  │    │    ├─ mp.yutong.com → 替换为 safetyimg.yutong.com/mpproxy
  │    │    ├─ sg.yutong.com → 替换为 safetyimg.yutong.com/sgproxy
  │    │    └─ 其他 → OkHttp下载 + Base64编码
  │    └─ 过滤无图片记录
  │
  ├─ Step 5: 按证件号去重
  │
  ├─ Step 6: 批量写入海康中间库 (batchSyncToHik)
  │    ├─ 按 batchSize (默认900) 分批
  │    └─ 每批执行:
  │         ├─ UPSERT tb_middle_person (ON CONFLICT certificate_no)
  │         └─ UPSERT tb_middle_face (ON CONFLICT certificate_no)
  │
  └─ 返回 HkSyncResult
```

## 本地开发

### 环境要求

- JDK 8+
- Maven 3.8+
- Temporal Server（开发环境默认地址 `127.0.0.1:7233`）

### 编译运行

```bash
# 编译
mvn clean compile

# 本地运行（开发环境）
mvn spring-boot:run

# 指定环境
mvn spring-boot:run -Dspring-boot.run.profiles=test

# 指定场景
mvn spring-boot:run -Dspring-boot.run.arguments=--worker.scenarios=hikSync
```

### 使用 Java CLI 工具

```bash
# 启动增量同步
mvn compile exec:java -Pdev \
  -Dexec.args="start hik-sync-001 --syncType INCREMENTAL --timeWindowMinutes 123 --batchSize 900"

# 查询同步进度
mvn compile exec:java -Pdev \
  -Dexec.args="query hik-sync-001 getSyncProgress"

# 查看工作流详情
mvn compile exec:java -Pdev \
  -Dexec.args="describe hik-sync-001"

# 列出工作流
mvn compile exec:java -Pdev \
  -Dexec.args="list --workflowType HikSyncWorkflow"
```

### 编译打包

```bash
mvn clean package -DskipTests        # 开发环境
mvn clean package -DskipTests -Pprod # 生产环境
```

## Docker 镜像构建

```bash
docker build -t hik-sync-worker:1.0.0 .

docker run -d \
  -e TEMPORAL_ADDRESS=127.0.0.1:7233 \
  -e WORKER_SCENARIOS=hikSync \
  -e MYSQL_URL=jdbc:mysql://10.66.139.220:33306/safety?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false \
  -e MYSQL_USERNAME=BP_QUERY \
  -e MYSQL_PASSWORD=SdkWCNQ9 \
  -e PG_URL=jdbc:postgresql://10.40.0.1:7092/ctm02brss_ctm02brssdb \
  -e PG_USERNAME=hikface \
  -e PG_PASSWORD=zjk@111++ \
  -p 18092:18092 \
  hik-sync-worker:1.0.0
```

## 配置说明

### 多环境配置

| 环境 | Profile | MySQL | Temporal |
|------|---------|-------|----------|
| 开发 | dev（默认） | 10.66.229.187:33306/safety | 127.0.0.1:7233 |
| 测试 | test | 10.66.229.187:33306/safety | test-temporal:7233 |
| 生产 | prod | 10.66.139.220:33306/safety | prod-temporal:7233 |

PostgreSQL（所有环境）: `10.40.0.1:7092/ctm02brss_ctm02brssdb`

### 环境变量

| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| `TEMPORAL_ADDRESS` | Temporal Server 地址 | 127.0.0.1:7233 |
| `MYSQL_URL` | MySQL 连接 URL | 开发环境地址 |
| `MYSQL_USERNAME` | MySQL 用户名 | root |
| `MYSQL_PASSWORD` | MySQL 密码 | root |
| `PG_URL` | PostgreSQL 连接 URL | 海康中间库地址 |
| `PG_USERNAME` | PostgreSQL 用户名 | hikface |
| `PG_PASSWORD` | PostgreSQL 密码 | zjk@111++ |
| `WORKER_SCENARIOS` | 启动的场景 | hikSync |
| `JAVA_OPTS` | JVM 参数 | -Xms256m -Xmx512m |

## Activity 重试策略

| Activity 方法 | 超时 | 最大重试 | 初始退避 | 退避系数 |
|---------------|------|----------|----------|----------|
| queryBlackMessageList | 30s | 3 | 2s | 2.0 |
| queryEmployeeMessageList | 30s | 3 | 2s | 2.0 |
| queryEnterRegisterList | 30s | 3 | 2s | 2.0 |
| queryVisitorHairpinList | 30s | 3 | 2s | 2.0 |
| processFaceImages | 120s | 2 | 5s | 2.0 |
| batchSyncToHik | 60s | 3 | 5s | 2.0 |

## Jenkins 部署

Jenkinsfile 支持 BUILD / DEPLOY / SCALE / START / STOP / RESTART / STATUS 七种操作。

## 相关文档

- [海康数据同步业务逻辑](docs/海康数据同步业务逻辑.md)
- [海康数据同步使用说明](docs/海康数据同步使用说明.md)
- [海康数据同步本地运行与测试指南](tests/海康数据同步本地运行与测试指南.md)
