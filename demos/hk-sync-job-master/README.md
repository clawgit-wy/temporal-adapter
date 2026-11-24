# 海康同步定时任务 - 使用说明

## 项目说明

这是一个独立的海康中间库数据增量同步项目，从原 safety-job 项目中提取了
`syncToHkAdd` 同步任务，并将其改造为支持 XXL-JOB 调用的独立 Java 项目。

## 项目结构

```
hk-sync-job/
├── pom.xml                          # Maven 依赖配置
├── src/main/java/com/yutong/hksync/
│   ├── HkSyncJobApplication.java    # 启动类
│   ├── config/
│   │   ├── DataSourceConfig.java    # 主数据源配置（业务库）
│   │   ├── HikDataSourceConfig.java # 海康中间库数据源配置
│   │   └── XxlJobConfig.java        # XXL-JOB 配置
│   ├── entity/                      # 实体类
│   │   ├── SafetyBlackMessageDO.java
│   │   ├── SafetyEmployeeMessageDO.java
│   │   ├── SafetyGroupInfoDO.java
│   │   ├── SafetyEnterRegisterRt.java
│   │   └── SafetyVisitorHairpinDO.java
│   ├── mapper/                      # MyBatis Mapper 接口
│   │   ├── SafetyBlackMessageMapper.java
│   │   ├── SafetyEmployeeMessageMapper.java
│   │   ├── SafetyGroupInfoMapper.java
│   │   ├── SafetyEnterRegisterMapper.java
│   │   └── SafetyVisitorHairpinMapper.java
│   ├── service/                     # 业务逻辑服务
│   │   ├── HkSyncService.java       # 同步主服务
│   │   ├── HkMiddleTableService.java # 海康中间库操作
│   │   └── HkDataSourceService.java # 海康数据源操作
│   ├── vo/
│   │   └── HkMiddleTableVO.java     # 海康中间库 VO
│   └── xxljob/
│       └── HkSyncXxlJob.java        # XXL-JOB 定时任务处理器
└── src/main/resources/
    ├── application.yml              # 应用配置文件
    └── mapper/                      # MyBatis XML 映射文件
```

## XXL-JOB 配置步骤

### 1. 部署 XXL-JOB Admin

如果还没有部署 XXL-JOB Admin，请参考官方文档部署。

### 2. 配置执行器

在 XXL-JOB Admin 中新增执行器：
- 应用名称：`hk-sync-job`
- 注册方式：自动注册
- 路由策略：第一个或轮询

### 3. 配置任务

在 XXL-JOB Admin 中新增任务：
- 执行器：选择上面配置的 `hk-sync-job`
- 任务描述：海康中间库数据增量同步
- Cron 表达式：根据需求设置（如 `0 0 2 * * ?` 表示每天凌晨2点执行）
- 运行模式：BEAN
- JobHandler：`hkSyncJobHandler`
- 任务参数（可选）：`开始时间，结束时间`，格式为 `yyyy-MM-dd HH:mm:ss`
  - 如果不传参数，默认同步最近123分钟的数据

### 4. 启动应用

```bash
# 修改 application.yml 中的配置后启动
java -jar hk-sync-job.jar
```

## 配置说明

### application.yml 关键配置项

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| spring.datasource.url | 业务数据库连接 | - |
| spring.datasource.username | 业务数据库用户名 | - |
| spring.datasource.password | 业务数据库密码 | - |
| hik.datasource.url | 海康中间库连接 | - |
| hik.datasource.username | 海康中间库用户名 | - |
| hik.datasource.password | 海康中间库密码 | - |
| hik.artemis.host | 海康 API 地址 | - |
| hik.artemis.appKey | 海康 API Key | - |
| hik.artemis.appSecret | 海康 API Secret | - |
| xxl.job.admin.addresses | XXL-JOB Admin 地址 | http://127.0.0.1:8090/xxl-job-admin |
| xxl.job.executor.appname | 执行器应用名称 | hk-sync-job |
| xxl.job.executor.port | 执行器端口 | 9999 |

## 同步逻辑说明

该任务每 N 分钟执行一次，默认同步最近123分钟内发生变化的数据：

1. **黑名单同步** - 同步 `safety_black_message` 表中更新的数据到海康中间库
2. **员工台账同步** - 同步 `safety_employee_message` 表中更新的数据到海康中间库
3. **组织架构同步** - 同步 `safety_group_info` 表中更新的数据到海康中间库
4. **外来人员同步** - 同步 `safety_enter_register` 表中更新的数据到海康中间库
5. **访客同步** - 同步 `safety_visitor_hairpin` 表中更新的数据到海康中间库

## 注意事项

1. 海康中间库需要有三张表：`TB_MIDDLE_PERSON`、`TB_MIDDLE_FACE`、`TB_MIDDLE_ORG`
2. 海康 API 需要配置正确的地址和认证信息
3. 照片同步依赖于中台接口获取照片地址，如果没有照片地址会跳过该记录
4. 项目默认使用 MyBatis 的驼峰命名映射，数据库字段使用下划线命名
