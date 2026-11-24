# Temporal 迁移技术分析报告

> **文档版本**: v1.0
> **分析日期**: 2026-08-05
> **分析范围**: `safety-service-master`（后端 Java）与 `safety-web-master`（前端 Vue3）
> **分析目标**: 识别第三方 API 集成与 XXL-JOB 定时任务实现，评估 Temporal 迁移可行性

---

## 目录

- [1. 执行摘要](#1-执行摘要)
- [2. 第三方 API 集成清单](#2-第三方-api-集成清单)
  - [2.1 后端集成总览](#21-后端集成总览)
  - [2.2 后端集成详情](#22-后端集成详情)
  - [2.3 前端集成总览](#23-前端集成总览)
  - [2.4 前端集成详情](#24-前端集成详情)
  - [2.5 性能与可靠性问题汇总](#25-性能与可靠性问题汇总)
- [3. XXL-JOB 定时任务完整映射](#3-xxl-job-定时任务完整映射)
  - [3.1 调度架构说明](#31-调度架构说明)
  - [3.2 Job Controller 清单](#32-job-controller-清单)
  - [3.3 各 Job 任务详解](#33-各-job-任务详解)
- [4. Temporal 迁移技术评估](#4-temporal-迁移技术评估)
  - [4.1 评估方法论](#41-评估方法论)
  - [4.2 第三方 API 集成的 Temporal 迁移评估](#42-第三方-api-集成的-temporal-迁移评估)
  - [4.3 XXL-JOB 任务的 Temporal 迁移评估](#43-xxl-job-任务的-temporal-迁移评估)
- [5. 优先级排序与迁移建议](#5-优先级排序与迁移建议)
  - [5.1 迁移优先级矩阵](#51-迁移优先级矩阵)
  - [5.2 分阶段实施路线图](#52-分阶段实施路线图)
  - [5.3 风险与应对](#53-风险与应对)
- [6. 附录](#6-附录)

---

## 1. 执行摘要

本次分析覆盖了宇通安保管理平台的后端（`safety-service-master`，4 个 Maven 模块）和前端（`safety-web-master`，Vben Admin / JeecgBoot Vue3）两个项目。

### 关键发现

| 维度 | 发现 |
|------|------|
| **后端第三方 API** | 共识别 **30+ 个外部系统集成点**，涵盖海康威视、BPM、EIP、EHR、IAM、CRK、Kafka、Dubbo、新开普门禁、宇信通、微信、SAP 等 |
| **前端第三方集成** | 共识别 **16 类集成**，含海康 H5Player、蓝凌 BPM、SSO、WebSocket、TinyMCE、ECharts、Matomo 等 |
| **XXL-JOB 任务** | 共识别 **7 个 Job Controller**，包含 **35+ 个定时任务端点**，覆盖楼宇门禁、消防巡检、海康同步、访客授权、黑名单、园区权限、入厂备案 |
| **重试机制缺陷** | 仅楼宇门禁（重试 1 次）和文件上传（重试 3 次）有重试逻辑；BPM/EIP/IAM/CRK/海康 等核心集成**均无重试** |
| **OOM 风险** | 海康人脸识别使用 `@Async` + 有限线程池（5-10 线程，队列 200），大批量调用时存在积压与 OOM 风险 |
| **定时任务可靠性** | 所有定时任务通过 Controller HTTP 端点触发，依赖外部调度器（XXL-JOB），无断点续传能力，故障即丢任务 |

### 核心结论

**最高优先迁移目标**为海康威视批量操作（人脸分组同步、权限下发）和定时权限生效场景（访客授权、备案权限下发），这两个场景与已有 Temporal 设计文档（`Temporal业务应用方案介绍.md`、`业务定时任务.md`）高度吻合，且当前实现的可靠性债务最为严重。

---

## 2. 第三方 API 集成清单

### 2.1 后端集成总览

| # | 外部系统 | API 模式 | 核心文件 | 用途 | 重试 | 超时 |
|---|---------|----------|---------|------|------|------|
| 1 | 海康威视（视频监控） | REST (Artemis SDK) | `SafetyHotWorkService.java` | 监控点查询、直播/回放流 | 无 | SDK 内部 |
| 2 | 海康威视（人脸识别） | REST (Artemis SDK) | `SafetyArtemisService.java` | 人脸分组/添加/检索/比对 | 无 | SDK 内部 |
| 3 | 海康中台数据库 | PostgreSQL (JDBC) | `database-common.xml` | 海康数据直接查询 | - | Druid 配置 |
| 4 | BPM 流程引擎 | SOAP WebService | `SafetyBpmProcessService.java` | 流程创建/审批/传阅 | 无 | socket=30s |
| 5 | EIP 系统 | SOAP WebService | `WebserviceUtil.java` | 流程触发/状态更新 | 无 | socket=10s |
| 6 | EHR 人力资源 | REST (OAuth2) | `SafetyBaseService.java` | 黑名单查询 | 无 | - |
| 7 | IAM 身份中台 | REST | `SafetyBaseService.java` 等 | 员工/组织/SSO/短信/邮件 | 无 | socket=10s |
| 8 | CRK 出入口系统 | REST | `SafetyDataSendLogService.java` | 权限下发/注销 | 无 | - |
| 9 | CRK 数据库 | SQL Server (JDBC) | `database-common.xml` | CRK 数据直接查询 | - | Druid 配置 |
| 10 | 宇信通机器人 | REST | `YxtRebotUtils.java` | IM 消息推送 | 无 | - |
| 11 | SMS 短信中台 | REST | `SafetyGeneralConfigService.java` | 短信通知 | 无 | - |
| 12 | 邮件系统 (IAM) | REST | `MailCenterUtils.java` | 模板邮件发送 | 无 | - |
| 13 | Kafka | Producer/Consumer | `KafkaMessageListener.java` | LDAP 同步事件 | Producer retries=3 | - |
| 14 | Dubbo RPC (LDAP) | Dubbo | `dubbo-consumer.xml` | 人员/组织信息查询 | 无 | 6000ms |
| 15 | Dubbo RPC (权限) | Dubbo | `dubbo-consumer.xml` | 用户资源权限查询 | 无 | 2000ms |
| 16 | 新开普楼宇门禁 | REST (3DES 加密) | `BuildHttpUtil.java` | 门禁权限下发 | 1 次 | Hutool 默认 |
| 17 | 微信小程序 | REST | `application.properties` | 消息推送/OpenID | 无 | - |
| 18 | SAP | REST | `application.properties` | 预约信息同步 | 无 | - |
| 19 | Environ 环境系统 | REST | `EnvironSyncRegisterHandler.java` | 入厂环境检测登记 | 无 | - |
| 20 | TMS | REST | `application.properties` | 入厂申请保存 | 无 | - |
| 21 | DMS | REST | `application.properties` | 车辆离场时间查询 | 无 | - |
| 22 | 友商 CRM | REST | `application.properties` | 客户身份判断 | 无 | - |
| 23 | OCR 身份证识别 | REST | `application.properties` | 身份证 OCR | 无 | - |
| 24 | Captcha 验证码 | REST | `application.properties` | 图文验证二次校验 | 无 | - |
| 25 | Vision 视觉监控 | REST | `application.properties` | 数据采集 | 无 | - |
| 26 | SSO 单点登录 | REST + Redis | `SSOLoginFilter.java` | 登录认证/Token 验证 | 无 | - |
| 27 | 文件存储 (zoffice) | HTTP multipart | `HttpFileUtil.java` | 文件上传 | 3 次 | connect=15s, socket=60s |
| 28 | MySQL 主库 | JDBC (Druid) | `database-common.xml` | 核心业务数据 | - | Druid 配置 |
| 29 | SQL Server (ykt) | JDBC | `database-common.xml` | 一卡通数据 | - | Druid 配置 |
| 30 | Redis 集群 | Redis | `application.properties` | 缓存/SSO Token | - | - |

### 2.2 后端集成详情

#### 2.2.1 海康威视 Artemis SDK 集成

**SDK 依赖**: `com.hikvision.ga:artemis-http-client:1.1.3`（[safety-core/pom.xml](file:///Users/apple/Desktop/AdapterService/demos/safety-service-master/safety-core/pom.xml)）

**视频监控服务** — [SafetyHotWorkService.java](file:///Users/apple/Desktop/AdapterService/demos/safety-service-master/safety-core/src/main/java/com/yutong/safety/core/service/hot/SafetyHotWorkService.java)
- 调用方式: `ArtemisHttpUtil.doPostStringArtemis()`
- 功能: 监控点列表查询、RTSP 直播流获取、回放流获取、设备状态查询
- 配置: `hot.host=10.40.0.1:443`、`hot.appKey`、`hot.appSecret`
- 认证: Artemis SDK 内置 appKey/appSecret 签名认证
- 错误处理: 解析返回 JSON 的 `code` 字段，无自动重试

**人脸识别服务** — [SafetyArtemisService.java](file:///Users/apple/Desktop/AdapterService/demos/safety-service-master/safety-core/src/main/java/com/yutong/safety/core/service/hik/SafetyArtemisService.java)
- 功能: 人脸分组 CRUD、人脸添加/删除、1:N 检索、1:1 比对、监控点查询、陌生人事件查询
- 异步模式: `@Async` + `batchExecutorService` 线程池（核心 5 / 最大 10 / 队列 200 / CallerRunsPolicy）
- 性能风险: 人脸图片 Base64 传输有网络开销；大批量人脸操作时线程池队列积压可能导致 OOM

**海康中台数据库**: PostgreSQL 直连（Druid 连接池），通过 [database-common.xml](file:///Users/apple/Desktop/AdapterService/demos/safety-service-master/safety-web/src/main/resources/spring/database-common.xml) 配置，用于黑名单/组织/员工台账数据增量同步

#### 2.2.2 BPM/EIP SOAP WebService 集成

**底层工具**: [WebserviceUtil.java](file:///Users/apple/Desktop/AdapterService/demos/safety-service-master/safety-common/src/main/java/com/yutong/safety/common/utils/WebserviceUtil.java)
- 实现: Apache HttpClient + `PoolingHttpClientConnectionManager`
- 连接池: max-total=1000, max-per-route=20
- 超时: socket=10000ms, connect=5000ms（部分场景 20000ms）
- SSL: 信任所有证书（`NoopHostnameVerifier`）— **安全风险**
- 重试: **无**

**SOAP 专用工具**: [HttpWS.java](file:///Users/apple/Desktop/AdapterService/demos/safety-service-master/safety-common/src/main/java/com/yutong/safety/common/utils/HttpWS.java)
- 超时: socket=30000ms, connect=30000ms
- 连接池: **无**（每次创建新连接）— **性能瓶颈**
- 认证: 支持 Basic Auth

**BPM 流程服务** — [SafetyBpmProcessService.java](file:///Users/apple/Desktop/AdapterService/demos/safety-service-master/safety-core/src/main/java/com/yutong/safety/core/service/SafetyBpmProcessService.java)
- 调用: `WebserviceUtil.doWebservicePostNew()`
- 功能: 创建流程、审批流程、获取节点信息/处理人、传阅、获取审批记录
- 配置: `bpm.flowWebService`（WSDL）、`bpm.createAndApprove`、`bpm.selfFlowInfo.url`
- 性能: SOAP XML 报文解析开销大

#### 2.2.3 新开普楼宇门禁集成

**加密工具**: [BuildEncryptUtil.java](file:///Users/apple/Desktop/AdapterService/demos/safety-service-master/safety-common/src/main/java/com/yutong/safety/common/utils/BuildEncryptUtil.java)
- 加密: 3DES（`SecureUtil.desede`）— **已不推荐，建议升级 AES-256**
- 签名: SHA256
- 配置: `build.encryption.key`、`build.signature.key`、`build.appid`、`build.dpcode`

**HTTP 工具**: [BuildHttpUtil.java](file:///Users/apple/Desktop/AdapterService/demos/safety-service-master/safety-common/src/main/java/com/yutong/safety/common/utils/BuildHttpUtil.java)
- 实现: Hutool `HttpUtil`
- 重试: **1 次**（`retcode != 200` 时重试）
- 每次请求执行 3DES 加密 + SHA256 签名计算

#### 2.2.4 IAM / EHR / CRK REST 集成

**IAM 身份中台** — 通过 [SafetyBaseService.java](file:///Users/apple/Desktop/AdapterService/demos/safety-service-master/safety-core/src/main/java/com/yutong/safety/core/service/SafetyBaseService.java) 等调用
- 端点: `iam.empInfo.url`、`iam.group.url`、`iam.sso.tockenUrl`、`iam.findSeparatedEmployees.url`、`iam.employee.workNoConvert.url`、`iam.mail.url`、`iam.sendMsgSms.url`
- 用途: 员工/组织信息查询、SSO Token 验证、离职人员查询、工号转换、邮件/短信发送
- 重试: **无**

**EHR 人力资源** — [SafetyBaseService.java](file:///Users/apple/Desktop/AdapterService/demos/safety-service-master/safety-core/src/main/java/com/yutong/safety/core/service/SafetyBaseService.java)
- 认证: OAuth2 `client_credentials` 模式获取 Token
- 端点: `ehr.token`、`ehr.blank.search`、`ehr.promise`
- 用途: 黑名单查询、承诺接口
- Token 管理: 获取失败返回 null，**无 Token 缓存/刷新机制**

**CRK 出入口系统** — [SafetyDataSendLogService.java](file:///Users/apple/Desktop/AdapterService/demos/safety-service-master/safety-core/src/main/java/com/yutong/safety/core/service/SafetyDataSendLogService.java)
- 端点: `sendData.qrUrl`、`visitor.card.tcmid`（目标: `10.66.230.209/210:60009`）
- 用途: 访客二维码注册、访客卡下发
- 重试: **无**

#### 2.2.5 Kafka 集成

**消费者** — [KafkaMessageListener.java](file:///Users/apple/Desktop/AdapterService/demos/safety-service-master/safety-kafka/src/main/java/com/yutong/safety/publicKafka/KafkaMessageListener.java)
- 消费组: `safety_group`
- Topic: beta=`ldapsync_topic`, prod=`ldap-sync-topic`
- 反序列化: Kryo 自定义序列化器
- 功能: 监听 LDAP 人员变更事件，处理员工离职/组织变更，发送宇信通通知
- 异步: Kafka 消费者异步处理 + `batchDataConsumerExecutor` 线程池
- 错误处理: 捕获异常记录日志，**无死信队列**

**生产者** — KafkaMessageSender（测试工具）
- 配置: `acks=all`、`retries=3`

#### 2.2.6 Dubbo RPC 集成

配置文件: [dubbo-consumer.xml](file:///Users/apple/Desktop/AdapterService/demos/safety-service-master/safety-web/src/main/resources/spring/dubbo-consumer.xml)

| 服务引用 | 接口 | 超时 | 用途 |
|----------|------|------|------|
| `rpcLadpService` | `IRpcLdapService` | 6000ms | LDAP 人员/组织信息查询 |
| `userResourceService` | `IUserResourceService` | 2000ms | 用户资源权限查询 |
| 注册中心 | ZooKeeper | - | `dubbo.address` 配置 |

#### 2.2.7 其他通知类集成

| 系统 | 文件 | 用途 | 重试 |
|------|------|------|------|
| 宇信通 IM | [YxtRebotUtils.java](file:///Users/apple/Desktop/AdapterService/demos/safety-service-master/safety-common/src/main/java/com/yutong/safety/common/utils/YxtRebotUtils.java) | 群聊/个人消息推送 | 无 |
| SMS 短信 | SafetyGeneralConfigService | 短信通知 | 无 |
| 邮件 (IAM) | [MailCenterUtils.java](file:///Users/apple/Desktop/AdapterService/demos/safety-service-master/safety-common/src/main/java/com/yutong/safety/common/utils/MailCenterUtils.java) | 模板邮件 | 无 |
| 微信 | application.properties | 消息推送/OpenID | 无 |

#### 2.2.8 线程池配置

[ThreadPoolConfig.java](file:///Users/apple/Desktop/AdapterService/demos/safety-service-master/safety-common/src/main/java/com/yutong/safety/common/config/ThreadPoolConfig.java)

| 线程池 | 核心 | 最大 | 队列 | 拒绝策略 | 用途 |
|--------|------|------|------|----------|------|
| `batchDataConsumerExecutor` | 5 | 10 | 200 | CallerRunsPolicy | Kafka 消费数据处理 |
| `batchExecutorService` | 5 | 10 | 200 | CallerRunsPolicy | 海康 Artemis 异步操作 |

### 2.3 前端集成总览

| # | 集成对象 | 模式 | 核心文件 | 状态 |
|---|---------|------|---------|------|
| 1 | 后端 REST API | Axios (MD5 签名) | `src/utils/http/axios/index.ts` | 活跃 |
| 2 | 全局 WebSocket 通知 | WebSocket (VueUse) | `src/hooks/web/useWebSocket.ts` | 活跃 |
| 3 | JVxeTable WebSocket | 原生 WebSocket | `src/components/jeecg/JVxeTable/src/hooks/useWebSocket.ts` | 活跃 |
| 4 | 本地硬件 WebSocket | 原生 WebSocket | `src/views/registration/visitor/index.vue` | 活跃 |
| 5 | CAS 单点登录 | CAS 协议 (重定向) | `src/hooks/web/useSso.ts` | 活跃 |
| 6 | 第三方登录 | OAuth 弹窗 + postMessage | `src/hooks/system/useThirdLogin.ts` | 活跃 |
| 7 | 蓝凌 BPM | iframe + postMessage | `src/assets/bpm/domain.js` | 活跃 |
| 8 | 海康 H5 视频播放器 | JSPlugin SDK | `public/h5player.min.js` | 活跃 |
| 9 | TinyMCE 富文本 | npm + 静态资源 | `src/components/Tinymce/` | 活跃 |
| 10 | ECharts 图表 | npm | `src/components/chart/` | 活跃 |
| 11 | Matomo 统计 | vue-matomo 插件 | `src/utils/matomo/index.js` | 活跃 |
| 12 | KkFileView 文件预览 | window.open | `src/components/jeecg/comment/useComment.ts` | 活跃 |
| 13 | EIP 门户 | 链接跳转 | 多个视图组件 | 活跃 |
| 14 | 设备深链接 | QR 码深链接 | `src/views/facilities/` | 活跃 |
| 15 | qiankun 微前端 | Micro-frontend | `src/qiankun/` | 已禁用 |
| 16 | 二维码 | qrcode npm | `src/components/Qrcode/` | 活跃 |

### 2.4 前端集成详情

#### 2.4.1 HTTP 客户端配置

**文件**: [src/utils/http/axios/index.ts](file:///Users/apple/Desktop/AdapterService/demos/safety-web-master/src/utils/http/axios/index.ts)
- 基础库: Axios v0.26.1
- 超时: 100 秒
- 请求拦截: JWT Token（Authorization + X-TOKEN）、MD5 签名（X-Sign）、时间戳（X-TIMESTAMP）、多租户 ID、Vue3 版本标记
- 响应拦截: code=0 成功；超时/401/402 清除 Token 并登出
- 请求取消: 基于 Map 的重复请求自动取消
- 防缓存: GET 请求自动追加时间戳

**签名加密**: [src/utils/encryption/signMd5Utils.js](file:///Users/apple/Desktop/AdapterService/demos/safety-web-master/src/utils/encryption/signMd5Utils.js)
- 算法: MD5 签名（密钥硬编码 `dd05f1c54d63749eda95f9fa6d49v442a`）

**缓存加密**: [src/settings/encryptionSetting.ts](file:///Users/apple/Desktop/AdapterService/demos/safety-web-master/src/settings/encryptionSetting.ts)
- 算法: AES-ECB-PKCS7（密钥硬编码）

#### 2.4.2 WebSocket 集成

**全局通知 WebSocket** — [useWebSocket.ts](file:///Users/apple/Desktop/AdapterService/demos/safety-web-master/src/hooks/web/useWebSocket.ts)
- 认证: Token 作为子协议
- 重连: 自动重连，最多 10 次，延迟 5000ms
- 心跳: 每 55000ms 发送 "ping"
- 消息分发: 全局 Map 存储回调

**JVxeTable WebSocket** — [useWebSocket.ts](file:///Users/apple/Desktop/AdapterService/demos/safety-web-master/src/components/jeecg/JVxeTable/src/hooks/useWebSocket.ts)
- URL: `${domain}/vxeSocket/${userId}/${pageId}`
- 心跳: 10000ms 间隔，6000ms 超时触发重连
- 重连: lockReconnect 锁防重复，5000ms 延迟

**本地硬件 WebSocket** — [visitor/index.vue](file:///Users/apple/Desktop/AdapterService/demos/safety-web-master/src/views/registration/visitor/index.vue)
- 地址: `ws://127.0.0.1:18890`（本地读卡器/发卡设备）
- 重连: **无**

#### 2.4.3 SSO / 第三方登录

**CAS 单点登录** — [useSso.ts](file:///Users/apple/Desktop/AdapterService/demos/safety-web-master/src/hooks/web/useSso.ts)
- IAM 平台: `https://mp.yutong.com/mpapi/iam-web/authz/`
- 流程: 检查 Token → 检查 URL code 参数 → ticket 校验 → 重定向到 CAS

**第三方登录** — [useThirdLogin.ts](file:///Users/apple/Desktop/AdapterService/demos/safety-web-master/src/hooks/system/useThirdLogin.ts)
- 支持: GitHub、微信、企业微信、钉钉
- 模式: 弹窗 + `window.postMessage` 通信

#### 2.4.4 轮询/定时模式

| 模式 | 文件 | 间隔 | 用途 |
|------|------|------|------|
| 二维码扫码轮询 | `MiniCodelogin.vue` | 1500ms | 轮询 `getQrcodeToken` 接口 |
| SMS 验证码倒计时 | `useCountdown.ts` | 1000ms | 倒计时显示 |
| 锁屏时钟 | `useNow.ts` | 1000ms | 时间显示更新 |
| 视频 resize 防抖 | `VideoPlayer.vue` | 200ms | 窗口 resize 防抖 |

### 2.5 性能与可靠性问题汇总

| 问题类型 | 涉及组件 | 影响 | 严重度 |
|----------|---------|------|--------|
| **无重试机制** | BPM/EIP/IAM/CRK/海康 等核心集成 | 临时故障导致请求失败，业务中断 | 高 |
| **OOM 风险** | 海康人脸识别 `@Async` + 有限线程池 | 大批量人脸操作时队列积压、GC 频繁 | 高 |
| **SSL 信任所有证书** | `WebserviceUtil` (NoopHostnameVerifier) | 中间人攻击风险 | 中 |
| **JWT 密钥硬编码** | `TokenUtils` (密钥 "SAFFETY") | 密钥泄露风险 | 中 |
| **3DES 加密** | 新开普楼宇门禁 | 已过时，建议升级 AES-256 | 低 |
| **旧版 HTTP 客户端** | commons-httpclient 3.1 | 已停止维护，存在已知漏洞 | 中 |
| **HttpWS 无连接池** | SOAP 专用工具 | 高频调用性能差 | 中 |
| **5 种 HTTP 客户端并存** | HttpClient/OkHttp/RestTemplate/commons-httpclient/HttpURLConnection | 维护负担重 | 中 |
| **Token 无缓存** | EHR OAuth2 Token | 每次请求重新获取 Token | 低 |
| **Kafka 无死信队列** | KafkaMessageListener | 消费失败消息丢失 | 中 |

---

## 3. XXL-JOB 定时任务完整映射

### 3.1 调度架构说明

```
XXL-JOB 调度中心
    │
    ├── HTTP 调用 ──→ Job Controller (Spring REST 端点)
    │                    │
    │                    └── 委托 ──→ Handler (业务逻辑)
    │                                   │
    │                                   └── 调用 ──→ Service (外部 API / DB)
    │
    └── 记录执行日志
```

**关键特征**:
- 所有定时任务以 Spring REST Controller 端点形式暴露，由 XXL-JOB 调度中心通过 HTTP 调用触发
- 项目代码中**未使用** `@Scheduled` 注解，也**未引入** XXL-JOB SDK 依赖（`com.xxl` 无匹配）
- 调度配置（cron 表达式、重试次数等）存储在 XXL-JOB 调度中心，不在项目代码中
- 任务执行无断点续传能力，故障即丢任务
- 错误处理统一为 `try-catch` + `LogUtils.error()` 记录日志，部分抛出 `BizException`

### 3.2 Job Controller 清单

| # | Controller | 模块 | 路由前缀 | 任务数 | 核心外部依赖 |
|---|-----------|------|---------|--------|-------------|
| 1 | [SafetyBuildJobController](file:///Users/apple/Desktop/AdapterService/demos/safety-service-master/safety-web/src/main/java/com/yutong/safety/web/controller/app/build/SafetyBuildJobController.java) | 楼宇门禁 | `/app/build/safety/buildJob` | 17 | 新开普门禁、宇信通、BPM |
| 2 | [SafetyFireInspectionJobController](file:///Users/apple/Desktop/AdapterService/demos/safety-service-master/safety-web/src/main/java/com/yutong/safety/web/controller/app/fireInspection/SafetyFireInspectionJobController.java) | 消防巡检 | `/app/fireInspection/safety/job` | 1 | 内部 DB |
| 3 | [SafetyHikJobController](file:///Users/apple/Desktop/AdapterService/demos/safety-service-master/safety-web/src/main/java/com/yutong/safety/web/controller/app/hik/SafetyHikJobController.java) | 海康同步 | `/app/safety/hik/job` | 5 | 海康 Artemis SDK |
| 4 | [SafetyVisitorJobController](file:///Users/apple/Desktop/AdapterService/demos/safety-service-master/safety-web/src/main/java/com/yutong/safety/web/controller/app/visitor/SafetyVisitorJobController.java) | 访客授权 | `/app/safety/visitor/job` | 1 | CRK、海康 |
| 5 | [safetyBlackMessageJobController](file:///Users/apple/Desktop/AdapterService/demos/safety-service-master/safety-web/src/main/java/com/yutong/safety/web/controller/app/blackmessage/safetyBlackMessageJobController.java) | 黑名单 | `/app/blackMessage/safety/job` | 3 | 海康中台、EHR |
| 6 | [SafetyParkJobController](file:///Users/apple/Desktop/AdapterService/demos/safety-service-master/safety-web/src/main/java/com/yutong/safety/web/controller/app/park/SafetyParkJobController.java) | 园区权限 | `/app/park/safety/parkJob` | 3 | 内部 DB |
| 7 | [SafetyEntryJobController](file:///Users/apple/Desktop/AdapterService/demos/safety-service-master/safety-web/src/main/java/com/yutong/safety/web/controller/app/entry/SafetyEntryJobController.java) | 入厂备案 | `/app/entry/safety/job` | 6 | EHR、CRK、Redis |

### 3.3 各 Job 任务详解

#### 3.3.1 SafetyBuildJobController — 楼宇门禁定时任务

| 端点 | 说明 | 调度频率(推断) | 外部调用 | 错误处理 |
|------|------|---------------|---------|----------|
| `/initEmployeePower` | 初始化员工楼宇门禁权限 | 手动/一次性 | 新开普门禁 API | catch + 日志 |
| `/initEmpDutyDept` | 初始化员工责任部门 | 手动/一次性 | - | - |
| `/updateDeviceTask` | 定时更新门禁设备 | 每天一次 | 新开普门禁 API + 宇信通 | catch + 日志(返回0) |
| `/updatePowerTask` | 根据组织自动授权更新权限 | 每日 | 新开普门禁 API | catch + BizException |
| `/modifyRecordsTask` | 更新门禁出入记录 | 定时 | - | catch + BizException |
| `/initBuildLongTerm` | 更新长期未进出门禁信息 | 定时(在 modifyRecordsTask 之后) | - | catch + BizException |
| `/sendDepartToDo` | 定时发送组织变更代办 | 早9:30 | 宇信通/BPM | catch + BizException |
| `/autoConfirmChangePower` | 自动确认部门变更门禁权限 | 定时 | 新开普门禁 API | catch + BizException |
| `/autoDepartConfirmPower` | 自动确认调岗门禁权限变更 | 定时 | 新开普门禁 API | catch + BizException |
| `/sendOwnerLongToDo` | 定时发送长期未进出代办(个人) | 早9:10 | 宇信通/BPM | catch + BizException |
| `/autoChangeFactoryAreaPower` | 定时下发和注销厂区门禁 | 定时 | 新开普门禁 API | catch + BizException |
| `/initCrkBuildPower` | 富士出入口门禁权限初始化 | 一次性 | 新开普门禁 API | catch + BizException |
| `/testInitAutoBuild` | 测试-初始化指定员工自动授权 | 手动 | - | catch + 日志 |
| `/autoCloseCarPower` | 定时关闭租赁到期车辆权限 | 定时 | - | catch + 日志 |
| `/closeOwnerLongToDo` | 超7天自动消除待办和取消门禁 | 定时 | 新开普门禁 API | catch + BizException |
| `/issueDoorRulePower` | 大门规则权限下发 | 定时 | 新开普门禁 API | - |
| `/backupInterfaceLog` | 日志表定时备份 | 定时 | - | - |
| `/delTodo` | 消除长期未进出代办 | 手动 | BPM | - |

**执行特征**:
- 多任务间存在**执行顺序依赖**（如 `modifyRecordsTask` → `initBuildLongTerm`），需通过 XXL-JOB 调度顺序保证
- 多个任务调用新开普门禁 API（3DES 加密，重试 1 次），批量权限操作可能耗时较长
- 部分任务有固定时间窗口要求（早9:10、早9:30）

#### 3.3.2 SafetyFireInspectionJobController — 消防巡检定时任务

| 端点 | 说明 | 调度频率 | 外部调用 | 错误处理 |
|------|------|---------|---------|----------|
| `/autoInspectionTask` | 定时下发巡检任务 | 周一和周四 8:00 前 | 内部 DB | catch + BizException |

**执行特征**: 调度频率明确（周一/周四），任务逻辑相对简单，主要为 DB 操作生成巡检任务

#### 3.3.3 SafetyHikJobController — 海康定时任务

| 端点 | 说明 | 调度频率(推断) | 外部调用 | 错误处理 |
|------|------|---------------|---------|----------|
| `/syncUpdateInfo` | 定时更新抓拍机信息 | 定时 | 海康 Artemis SDK | SDK 返回码 |
| `/syncUpdateGroupInfo` | 定时更新分组数据 | 定时 | 海康 Artemis SDK | SDK 返回码 |
| `/batchCreateGroupInfo` | 批量创建分组数据 | 定时 | 海康 Artemis SDK | SDK 返回码 |
| `/saveOperationGroup` | 定时更新在职人员分组数据 | 定时 | 海康 Artemis SDK | SDK 返回码 |
| `/updateHikGroupFaces` | 外来人员定时变更人脸分组 | 每30分钟 | 海康 Artemis SDK | SDK 返回码 |

**执行特征**:
- 所有任务调用海康 Artemis SDK，**无重试机制**
- `updateHikGroupFaces` 每 30 分钟执行一次，涉及外来人员人脸分组批量变更
- `saveOperationGroup` 涉及在职人员全量分组数据更新，可能产生大量 API 调用
- 使用 `@Async` + `batchExecutorService` 线程池（5-10 线程），大批量操作存在积压与 OOM 风险
- **无速率限制**，可能打垮海康接口

#### 3.3.4 SafetyVisitorJobController — 访客授权定时任务

| 端点 | 说明 | 调度频率(推断) | 外部调用 | 错误处理 |
|------|------|---------------|---------|----------|
| `/taskVisitorApproval` | 访客授权审批定时任务 | 定时 | CRK、海康 | Handler 内部处理 |

**执行特征**:
- 涉及访客权限下发（CRK 出入口系统 + 海康人脸）
- 可能包含"访客预约入场前 10 分钟下发权限"的场景（与 `业务定时任务.md` 描述吻合）
- 当前依赖 XXL-JOB 定时扫库实现，存在 DB 慢查询和漏单风险

#### 3.3.5 safetyBlackMessageJobController — 黑名单定时任务

| 端点 | 说明 | 调度频率(推断) | 外部调用 | 错误处理 |
|------|------|---------------|---------|----------|
| `/syncToHkAdd` | 黑名单/组织/员工台账/外来人员增量同步至中间库 | 定时 | 海康中台 PostgreSQL | Handler 内部 |
| `/updateHkiGroup` | 定时变更海康分组 | 定时 | 海康 Artemis SDK | Handler 内部 |
| `/updateBlackMessageStatus` | 定时变更黑名单状态 | 定时 | - | Handler 内部 |

**执行特征**:
- `syncToHkAdd` 已标记 `@Deprecated`，但仍可能被执行
- 涉及多系统数据同步（EHR → 海康中台），数据一致性要求高
- `updateHkiGroup` 调用海康 API 变更分组，无重试

#### 3.3.6 SafetyParkJobController — 园区权限定时任务

| 端点 | 说明 | 调度频率(推断) | 外部调用 | 错误处理 |
|------|------|---------------|---------|----------|
| `/doTaskByChange` | 人员默认权限调整(根据变更记录) | 定时 | 内部 DB | catch + 日志 |
| `/syncCarParkPower` | 根据人员厂区权限同步车权限 | 定时 | 内部 DB | catch + 日志 |
| `/autoGeneralClassifiedData` | 定时生成无涉密业务关联员工数据 | 定时 | 内部 DB | catch + 日志 |

**执行特征**: 主要为内部 DB 操作，外部依赖较少，任务逻辑相对独立

#### 3.3.7 SafetyEntryJobController — 入厂备案定时任务

| 端点 | 说明 | 调度频率 | 外部调用 | 错误处理 |
|------|------|---------|---------|----------|
| `/syncEhrBlank` | 同步 EHR 黑名单 | 定时(带时间范围) | EHR REST API | Handler 内部 |
| `/entryOverdueTask` | 备案过期任务提醒 | 每天 17:00 | 邮件系统 | Handler 内部 |
| `/entryPermissionDelegationTask` | 备案权限下发 | 每30分钟 | CRK API | Handler 内部 |
| `/entryBpmToSafety` | 黑名单入场备案测试 | 手动 | BPM | Handler 内部 |
| `/clearEntryConfig` | 定时清理无效备案信息 | 定时 | - | Handler 内部 |
| `/clearRedis` | 清理消息发送的 Redis | 定时 | Redis | Handler 内部 |

**执行特征**:
- `entryPermissionDelegationTask` 每 30 分钟执行一次，下发数据为**当前时间 + 33 分钟**（预留 3 分钟时间差）— 这是典型的"定时权限生效"场景，与 `业务定时任务.md` 描述完全吻合
- `entryOverdueTask` 每天 17:00 邮件提醒，有固定时间窗口
- `syncEhrBlank` 涉及 EHR OAuth2 Token 获取 + REST 查询，Token 管理不当可能导致失败
- 多个任务间存在数据依赖关系

---

## 4. Temporal 迁移技术评估

### 4.1 评估方法论

每个组件按以下 5 个维度评分（1-5 分，5 分为最适宜迁移）：

| 维度 | 说明 |
|------|------|
| **工作流复杂度** | 多步骤编排、条件分支、顺序依赖的复杂程度 |
| **容错需求** | 对重试、断点续传、补偿事务的需求强度 |
| **可扩展性需求** | 批量操作规模、并发控制、速率限制的需求 |
| **维护挑战** | 当前实现的维护成本、故障排查难度 |
| **Temporal 契合度** | 与 Temporal 核心能力（Durable Execution、Sleep、Signal）的匹配程度 |

**迁移优先级** = 各维度评分加权总和（满分 25）

### 4.2 第三方 API 集成的 Temporal 迁移评估

#### 4.2.1 海康威视 Artemis SDK（人脸识别 + 视频监控）

| 维度 | 评分 | 理由 |
|------|------|------|
| 工作流复杂度 | 5 | 批量人脸分组同步（全量在职人员）、1:N 检索、多步骤编排 |
| 容错需求 | 5 | 当前无重试，SDK 故障导致数据不一致；需要断点续传 |
| 可扩展性需求 | 5 | 全量人员分组更新可能产生数万次 API 调用，需速率限制（15 QPS） |
| 维护挑战 | 4 | `@Async` + 有限线程池存在 OOM 风险，故障排查困难 |
| Temporal 契合度 | 5 | 完美匹配：Activity 限流、RetryPolicy 指数退避、Event Sourcing 断点续传 |
| **总分** | **24/25** | |

**迁移可行性**: 高
- **潜在收益**: 消除 OOM 风险、自动重试、速率限制、可视化进度追踪、断点续传
- **实现方案**:
  - Workflow: `HikSyncWorkflow` — 遍历人员列表，逐人/批量发起 Activity
  - Activity: `HikFaceActivity` — 封装 Artemis SDK 调用（分组变更、人脸添加/删除）
  - Worker 配置: `setMaxConcurrentActivityExecutionSize(15)` + `setMaxWorkerActivityTasksPerSecond(20)`
  - RetryPolicy: `setBackoffCoefficient(2.0)`，初始 1s，最大 5 次
- **预估工作量**: 2-3 周（含 PoC 验证）
- **关联文档**: `Hikvision_Batch_Adapter_Design.md`（手写中转服务方案，Temporal 直接替代）、`Temporal业务应用方案介绍.md`、`验证场景.md`

#### 4.2.2 BPM/EIP SOAP WebService

| 维度 | 评分 | 理由 |
|------|------|------|
| 工作流复杂度 | 4 | 流程创建→审批→传阅多步骤编排，涉及人工审批等待 |
| 容错需求 | 4 | SOAP 故障无重试，流程创建失败需补偿 |
| 可扩展性需求 | 2 | 并发量不大，主要是定时触发 |
| 维护挑战 | 3 | SOAP XML 解析复杂，HttpWS 无连接池性能差 |
| Temporal 契合度 | 4 | 适合多步骤流程编排，Signal 可用于人工审批回调 |
| **总分** | **17/25** | |

**迁移可行性**: 中
- **潜在收益**: 自动重试 SOAP 调用、流程创建失败补偿、人工审批等待用 Signal 实现
- **实现方案**:
  - Workflow: `BpmProcessWorkflow` — 创建流程 → `Workflow.await()` 等待审批 Signal → 后续操作
  - Activity: `CreateBpmProcessActivity`、`ApproveBpmProcessActivity` — 封装 SOAP 调用
- **预估工作量**: 2-3 周
- **注意事项**: SOAP 超时 30s 需调整 Activity `StartToCloseTimeout`；HttpWS 无连接池问题需同步修复

#### 4.2.3 CRK 出入口系统

| 维度 | 评分 | 理由 |
|------|------|------|
| 工作流复杂度 | 3 | 权限下发/注销为单步操作，但与访客/备案流程关联 |
| 容错需求 | 4 | 权限下发失败需重试和补偿，当前无重试 |
| 可扩展性需求 | 3 | 批量访客/备案权限下发有一定并发需求 |
| 维护挑战 | 3 | 失败需人工干预，审计追踪困难 |
| Temporal 契合度 | 4 | Activity 重试 + 补偿 Workflow 适合权限管理 |
| **总分** | **17/25** | |

**迁移可行性**: 中
- **潜在收益**: 权限下发自动重试、失败补偿（注销已下发权限）、完整审计日志
- **实现方案**: 作为访客/入厂备案 Workflow 的 Activity 嵌入
- **预估工作量**: 1-2 周（作为子流程嵌入）

#### 4.2.4 EHR 人力资源 API

| 维度 | 评分 | 理由 |
|------|------|------|
| 工作流复杂度 | 2 | 主要是 Token 获取 + 黑名单查询，逻辑简单 |
| 容错需求 | 3 | Token 获取失败影响后续所有操作 |
| 可扩展性需求 | 2 | 查询频率不高 |
| 维护挑战 | 3 | Token 无缓存/刷新机制 |
| Temporal 契合度 | 3 | Token 获取可作为独立 Activity，查询作为后续 Activity |
| **总分** | **13/25** | |

**迁移可行性**: 低-中
- **建议**: 不单独迁移，作为黑名单同步 Workflow 的 Activity 嵌入

#### 4.2.5 新开普楼宇门禁

| 维度 | 评分 | 理由 |
|------|------|------|
| 工作流复杂度 | 4 | 楼宇门禁权限初始化/更新涉及多步骤（设备同步→权限计算→下发→通知） |
| 容错需求 | 4 | 权限下发仅重试 1 次，3DES 加密增加故障面 |
| 可扩展性需求 | 3 | 全量员工权限初始化可能涉及数千人 |
| 维护挑战 | 3 | 3DES 加密已过时，5 种 HTTP 客户端并存 |
| Temporal 契合度 | 4 | 多步骤编排 + 重试 + 批量限流 |
| **总分** | **18/25** | |

**迁移可行性**: 中-高
- **潜在收益**: 统一重试策略、批量限流、权限下发可视化追踪
- **实现方案**: 作为楼宇门禁 Job Controller 对应 Workflow 的 Activity
- **预估工作量**: 2-3 周

#### 4.2.6 Kafka LDAP 同步

| 维度 | 评分 | 理由 |
|------|------|------|
| 工作流复杂度 | 3 | 消息消费→人员变更处理→通知发送 |
| 容错需求 | 4 | 消费失败无死信队列，消息丢失 |
| 可扩展性需求 | 3 | LDAP 变更事件量可控 |
| 维护挑战 | 3 | 消费失败排查困难 |
| Temporal 契合度 | 3 | 可用 Workflow 包装消息处理，但 Kafka 消费者模式与 Temporal 长轮询模式有架构差异 |
| **总分** | **16/25** | |

**迁移可行性**: 中
- **建议**: 保持 Kafka 消费者架构，但将消息处理逻辑包装为 Temporal Workflow 启动（`WorkflowClient.start()`），利用 Temporal 的重试和补偿能力
- **预估工作量**: 1-2 周

### 4.3 XXL-JOB 任务的 Temporal 迁移评估

#### 4.3.1 SafetyHikJobController — 海康同步任务（优先级: P0）

| 维度 | 评分 | 理由 |
|------|------|------|
| 工作流复杂度 | 5 | 5 个任务涉及全量人员分组同步、批量人脸变更，API 调用量大 |
| 容错需求 | 5 | 无重试、无断点续传，海康 API 故障直接导致数据不一致 |
| 可扩展性需求 | 5 | 全量在职人员分组更新 = 数万次 API 调用，亟需限流 |
| 维护挑战 | 4 | `@Async` 线程池 OOM 风险，故障不可视化 |
| Temporal 契合度 | 5 | 完美匹配现有 Temporal 设计文档场景 |
| **总分** | **24/25** | |

**迁移方案**:
```
HikSyncWorkflow
├── Activity: syncMonitorInfo (抓拍机信息同步)
├── Activity: syncGroupInfo (分组数据同步)
├── Activity: batchCreateGroup (批量创建分组)
├── Activity: syncEmployeeGroup (在职人员分组更新) ← 限流 15 QPS
│   └── 循环: 逐人/批量调用 Artemis SDK
└── Activity: updateVisitorFaces (外来人员人脸分组变更) ← 限流 15 QPS
    └── 循环: 逐人调用 Artemis SDK
```

**关键设计**:
- Worker: `setMaxConcurrentActivityExecutionSize(15)` + `setMaxWorkerActivityTasksPerSecond(20)`
- RetryPolicy: `setBackoffCoefficient(2.0)`，初始 1s，最大 5 次
- WorkflowId: `hik-sync-{type}-{date}` 实现防重
- 预估工作量: 2-3 周

#### 4.3.2 SafetyEntryJobController — 入厂备案任务（优先级: P0）

| 维度 | 评分 | 理由 |
|------|------|------|
| 工作流复杂度 | 5 | 6 个任务含定时权限下发（+33分钟）、EHR 同步、过期提醒，多步骤编排 |
| 容错需求 | 5 | 权限下发失败无重试，EHR Token 管理缺陷 |
| 可扩展性需求 | 4 | 每 30 分钟执行，涉及批量备案人员 |
| 维护挑战 | 4 | 定时扫库 + Redis 清理逻辑复杂 |
| Temporal 契合度 | 5 | `Workflow.sleep()` 完美替代"当前时间+33分钟"定时下发 |
| **总分** | **23/25** | |

**迁移方案**:
```
EntryPermissionWorkflow (按备案记录维度)
├── Activity: getEntryConfig (获取备案配置)
├── Workflow.sleep(计算至目标时间) ← 替代"每30分钟扫库+33分钟时间差"
├── Activity: delegatePermission (CRK 权限下发)
│   └── RetryPolicy: 指数退避，最大 5 次
├── Activity: syncHikFace (海康人脸下发) ← 限流
└── Activity: sendNotification (通知发送)

EntryOverdueWorkflow
├── Activity: queryOverdueEntries (查询过期备案)
├── Workflow.sleep(计算至 17:00) ← 替代定时任务
└── Activity: sendEmailReminder (邮件提醒)

EhrBlacklistSyncWorkflow
├── Activity: getEhrToken (Token 获取) ← 可缓存
├── Activity: queryEhrBlack (黑名单查询)
└── Activity: syncToHikMiddle (同步至海康中台)
```

**关键设计**:
- `Workflow.sleep()` 实现精确定时，零资源占用，替代"每30分钟扫库"
- EHR Token 获取作为独立 Activity，可配置短缓存
- 预估工作量: 2-3 周

#### 4.3.3 SafetyVisitorJobController — 访客授权任务（优先级: P1）

| 维度 | 评分 | 理由 |
|------|------|------|
| 工作流复杂度 | 4 | 访客授权审批涉及多系统（CRK + 海康） |
| 容错需求 | 5 | 权限下发失败影响访客入场 |
| 可扩展性需求 | 4 | 批量访客预约场景 |
| 维护挑战 | 4 | 定时扫库实现"前10分钟下发"有漏单风险 |
| Temporal 契合度 | 5 | `Workflow.sleep()` 完美替代定时扫库 |
| **总分** | **22/25** | |

**迁移方案**:
```
VisitorAccessWorkflow (按访客预约维度)
├── Activity: createApproval (创建审批)
├── Workflow.await(() -> isApproved) ← 等待审批 Signal
├── Workflow.sleep(计算至入场前10分钟) ← 替代定时扫库
├── Activity: grantCrkPermission (CRK 权限下发)
├── Activity: grantHikFace (海康人脸下发) ← 限流
└── Activity: sendVisitorNotification (通知访客)
```

- 预估工作量: 2 周
- **关联文档**: `业务定时任务.md`（描述了完全相同场景的 Temporal 实现）

#### 4.3.4 SafetyBuildJobController — 楼宇门禁任务（优先级: P1）

| 维度 | 评分 | 理由 |
|------|------|------|
| 工作流复杂度 | 5 | 17 个任务，多步骤编排，任务间有顺序依赖 |
| 容错需求 | 4 | 新开普门禁仅重试 1 次，权限下发失败影响员工通行 |
| 可扩展性需求 | 4 | 全量员工权限初始化涉及数千人 |
| 维护挑战 | 4 | 17 个端点维护成本高，顺序依赖靠调度中心保证 |
| Temporal 契合度 | 4 | 多步骤 Workflow + ChildWorkflow 编排 |
| **总分** | **21/25** | |

**迁移方案**: 将 17 个任务拆分为多个独立 Workflow:
```
BuildDeviceSyncWorkflow (设备同步)
├── Activity: updateAreaTask (更新区域)
├── Activity: updateDeviceTask (更新设备)
├── Activity: removeDevicePower (移除无效权限)
└── Activity: sendNewDeviceNotification (通知新增设备)

BuildPowerUpdateWorkflow (权限更新)
├── Activity: updatePowerByDate (按日期更新权限) ← 限流
├── Activity: autoConfirmPower (自动确认权限)
└── Activity: autoDepartConfirmPower (调岗确认)

BuildLongTermWorkflow (长期未进出)
├── Activity: updateRecordTask (更新出入记录)
├── Activity: initLongTerm (初始化长期未进出)
├── Activity: sendOwnerLongToDo (发送个人代办) ← 定时 9:10
└── Activity: closeOwnerLongToDo (超7天自动消除) ← 定时
```

- 预估工作量: 3-4 周（任务数量多，需逐个验证）

#### 4.3.5 safetyBlackMessageJobController — 黑名单任务（优先级: P2）

| 维度 | 评分 | 理由 |
|------|------|------|
| 工作流复杂度 | 3 | 3 个任务，数据同步为主 |
| 容错需求 | 4 | 多系统数据一致性要求高 |
| 可扩展性需求 | 3 | 增量同步，量可控 |
| 维护挑战 | 3 | 含 deprecated 任务 |
| Temporal 契合度 | 3 | 数据同步适合 Workflow，但逻辑相对简单 |
| **总分** | **16/25** | |

**迁移方案**:
```
BlacklistSyncWorkflow
├── Activity: getEhrToken (EHR Token)
├── Activity: queryEhrBlack (查询 EHR 黑名单)
├── Activity: syncToHikMiddle (同步至海康中台 PostgreSQL)
├── Activity: updateHikGroup (变更海康分组) ← 限流
└── Activity: updateBlackStatus (变更黑名单状态)
```

- 预估工作量: 1-2 周

#### 4.3.6 SafetyFireInspectionJobController — 消防巡检任务（优先级: P3）

| 维度 | 评分 | 理由 |
|------|------|------|
| 工作流复杂度 | 2 | 单任务，内部 DB 操作为主 |
| 容错需求 | 2 | DB 操作有事务保证 |
| 可扩展性需求 | 2 | 数据量可控 |
| 维护挑战 | 2 | 逻辑简单 |
| Temporal 契合度 | 3 | 可用 `Workflow.sleep()` 替代 cron，但收益有限 |
| **总分** | **11/25** | |

**迁移建议**: **暂不迁移**。任务逻辑简单，DB 操作有事务保证，XXL-JOB 足够。如果需要统一调度入口可后续考虑。

#### 4.3.7 SafetyParkJobController — 园区权限任务（优先级: P3）

| 维度 | 评分 | 理由 |
|------|------|------|
| 工作流复杂度 | 2 | 3 个任务，内部 DB 操作为主 |
| 容错需求 | 2 | DB 操作有事务保证 |
| 可扩展性需求 | 2 | 数据量可控 |
| 维护挑战 | 2 | 逻辑简单 |
| Temporal 契合度 | 2 | 无外部 API 调用，Temporal 优势不明显 |
| **总分** | **10/25** | |

**迁移建议**: **暂不迁移**。理由同消防巡检。

---

## 5. 优先级排序与迁移建议

### 5.1 迁移优先级矩阵

| 优先级 | 组件 | 评分 | 核心理由 |
|--------|------|------|---------|
| **P0** | 海康威视批量操作（SafetyHikJobController + SafetyArtemisService） | 24/25 | OOM 风险、无重试、无限流、完美匹配 Temporal 场景 |
| **P0** | 入厂备案权限下发（SafetyEntryJobController） | 23/25 | 定时权限生效场景、`Workflow.sleep()` 完美替代扫库 |
| **P1** | 访客授权审批（SafetyVisitorJobController） | 22/25 | 定时权限下发、多系统协作、漏单风险 |
| **P1** | 楼宇门禁权限管理（SafetyBuildJobController） | 21/25 | 17 个任务、多步骤编排、顺序依赖 |
| **P2** | 黑名单数据同步（safetyBlackMessageJobController） | 16/25 | 多系统数据一致性、EHR Token 管理 |
| **P2** | BPM 流程编排（SafetyBpmProcessService） | 17/25 | SOAP 重试、人工审批 Signal |
| **P2** | CRK 出入口权限管理 | 17/25 | 权限下发重试与补偿 |
| **P2** | Kafka LDAP 同步 | 16/25 | 消费失败消息丢失 |
| **P3** | 新开普楼宇门禁 API | 18/25 | 随楼宇门禁 Workflow 一同迁移 |
| **P3** | 消防巡检（SafetyFireInspectionJobController） | 11/25 | 逻辑简单，XXL-JOB 足够 |
| **P3** | 园区权限（SafetyParkJobController） | 10/25 | 内部 DB 操作，Temporal 优势不明显 |
| **P3** | EHR API | 13/25 | 作为黑名单 Workflow 子组件迁移 |

### 5.2 分阶段实施路线图

#### 第一阶段: PoC 验证（1-2 周）

**目标**: 验证 Temporal 基础设施可用性，跑通核心场景

**范围**:
- Docker Compose 部署 Temporal Server + PostgreSQL + Web UI
- 实现 `HikSyncWorkflow` 基础版本（限流 + 重试）
- 执行 `验证场景.md` 中的 5 个破坏性测试场景
- 主服务通过 `WorkflowClient.start()` 提交任务

**验证点**:
- 限流 15 QPS 平滑消费
- Activity 指数退避重试（1s→2s→4s）
- Worker Kill 后断点续传
- `Workflow.sleep()` 零资源占用
- Webhook 结果通知自动重试

**关联文档**: `Temporal部署.md`、`验证场景.md`

#### 第二阶段: 核心业务迁移（3-4 周）

**目标**: 迁移 P0 优先级组件

**范围**:
1. **海康批量操作迁移**（2 周）
   - 实现 `HikSyncWorkflow` 完整版（5 个任务对应 5 个 Activity）
   - Worker 配置限流 + RetryPolicy
   - 替代 `SafetyHikJobController` 的 5 个端点
   - 替代 `SafetyArtemisService` 的 `@Async` 线程池
   - 主服务通过 `WorkflowClient.start()` 替代 HTTP 调用

2. **入厂备案权限下发迁移**（2 周）
   - 实现 `EntryPermissionWorkflow`（`Workflow.sleep()` 替代 30 分钟扫库）
   - 实现 `EntryOverdueWorkflow`（`Workflow.sleep()` 替代每天 17:00 定时）
   - 实现 `EhrBlacklistSyncWorkflow`（EHR Token + 黑名单同步）
   - 替代 `SafetyEntryJobController` 的 6 个端点

**关联文档**: `Temporal业务应用方案介绍.md`、`业务定时任务.md`、`Hikvision_Batch_Adapter_Design.md`

#### 第三阶段: 扩展迁移（3-4 周）

**目标**: 迁移 P1 优先级组件

**范围**:
1. **访客授权审批迁移**（2 周）
   - 实现 `VisitorAccessWorkflow`（审批 Signal + 定时下发 Sleep）
   - 替代 `SafetyVisitorJobController`

2. **楼宇门禁权限管理迁移**（2-3 周）
   - 实现 3 个 ChildWorkflow（设备同步、权限更新、长期未进出）
   - 替代 `SafetyBuildJobController` 的 17 个端点
   - 新开普门禁 API 作为 Activity 嵌入

#### 第四阶段: UI 二次开发与生产部署（3-4 周）

**目标**: 自研 Web UI 替代官方页面，生产环境四层分离部署

**范围**:
- BFF 模式实现（Vue3 + Spring Boot 3 + Temporal Java SDK）
- 四层分离部署（Nginx/UI + Server/BFF + PostgreSQL + Worker 集群）
- 监控告警集成
- 灰度切流

**关联文档**: `Temporal-WebUI二次开发与部署设计文档.md`、`temporal二次开发及部署设计.md`

#### 第五阶段: 可选扩展（按需）

**目标**: 迁移 P2 优先级组件

**范围**:
- 黑名单数据同步 Workflow
- BPM 流程编排 Workflow（Signal 审批回调）
- CRK 权限补偿 Workflow
- Kafka 消息处理包装为 Workflow 启动

### 5.3 风险与应对

| 风险 | 影响 | 应对措施 |
|------|------|---------|
| Temporal Server 与 DB 网络延迟 | 定时器精度下降、整体性能劣化 | 严格保证 Server 与 DB 同可用区（RTT < 1ms） |
| Worker 资源争抢 | Activity 执行延迟 | Worker 独立部署，Docker/K8s 资源 limits |
| 海康 SDK 兼容性 | Activity 内部调用失败 | PoC 阶段验证 SDK 在 Worker 进程中的兼容性 |
| XXL-JOB 与 Temporal 共存期 | 双重调度导致重复执行 | 灰度切流：先在 XXL-JOB 中禁用对应 cron，再启用 Temporal Workflow |
| 数据一致性 | 迁移期间数据不一致 | WorkflowId 防重 + 幂等设计 + 数据校对脚本 |
| Temporal 版本升级 | gRPC API 变更 | 锁定 SDK 版本，建立回归测试 |
| SSL 证书信任问题 | WebserviceUtil 安全风险 | 迁移时修复，使用正规证书验证 |

---

## 6. 附录

### 6.1 文件路径索引

#### 后端关键文件

| 文件 | 路径 |
|------|------|
| 海康视频服务 | `safety-core/src/main/java/com/yutong/safety/core/service/hot/SafetyHotWorkService.java` |
| 海康人脸服务 | `safety-core/src/main/java/com/yutong/safety/core/service/hik/SafetyArtemisService.java` |
| BPM 流程服务 | `safety-core/src/main/java/com/yutong/safety/core/service/SafetyBpmProcessService.java` |
| 基础服务 | `safety-core/src/main/java/com/yutong/safety/core/service/SafetyBaseService.java` |
| 定时任务服务 | `safety-core/src/main/java/com/yutong/safety/core/service/SafetyTaskService.java` |
| 数据下发服务 | `safety-core/src/main/java/com/yutong/safety/core/service/SafetyDataSendLogService.java` |
| 通用配置服务 | `safety-core/src/main/java/com/yutong/safety/core/service/SafetyGeneralConfigService.java` |
| WebService 工具 | `safety-common/src/main/java/com/yutong/safety/common/utils/WebserviceUtil.java` |
| SOAP 工具 | `safety-common/src/main/java/com/yutong/safety/common/utils/HttpWS.java` |
| 楼宇门禁 HTTP | `safety-common/src/main/java/com/yutong/safety/common/utils/BuildHttpUtil.java` |
| 楼宇门禁加密 | `safety-common/src/main/java/com/yutong/safety/common/utils/BuildEncryptUtil.java` |
| 邮件工具 | `safety-common/src/main/java/com/yutong/safety/common/utils/MailCenterUtils.java` |
| 宇信通工具 | `safety-common/src/main/java/com/yutong/safety/common/utils/YxtRebotUtils.java` |
| 线程池配置 | `safety-common/src/main/java/com/yutong/safety/common/config/ThreadPoolConfig.java` |
| Kafka 监听器 | `safety-kafka/src/main/java/com/yutong/safety/publicKafka/KafkaMessageListener.java` |
| Dubbo 消费者配置 | `safety-web/src/main/resources/spring/dubbo-consumer.xml` |
| 数据源配置 | `safety-web/src/main/resources/spring/database-common.xml` |
| 应用配置(beta) | `safety-web/src/main/profiles/beta/application.properties` |
| 应用配置(prod) | `safety-web/src/main/profiles/prod/application.properties` |

#### Job Controller 文件

| Controller | 路径 |
|-----------|------|
| 楼宇门禁 | `safety-web/src/main/java/com/yutong/safety/web/controller/app/build/SafetyBuildJobController.java` |
| 消防巡检 | `safety-web/src/main/java/com/yutong/safety/web/controller/app/fireInspection/SafetyFireInspectionJobController.java` |
| 海康同步 | `safety-web/src/main/java/com/yutong/safety/web/controller/app/hik/SafetyHikJobController.java` |
| 访客授权 | `safety-web/src/main/java/com/yutong/safety/web/controller/app/visitor/SafetyVisitorJobController.java` |
| 黑名单 | `safety-web/src/main/java/com/yutong/safety/web/controller/app/blackmessage/safetyBlackMessageJobController.java` |
| 园区权限 | `safety-web/src/main/java/com/yutong/safety/web/controller/app/park/SafetyParkJobController.java` |
| 入厂备案 | `safety-web/src/main/java/com/yutong/safety/web/controller/app/entry/SafetyEntryJobController.java` |

#### 前端关键文件

| 文件 | 路径 |
|------|------|
| HTTP 客户端 | `src/utils/http/axios/index.ts` |
| 全局 WebSocket | `src/hooks/web/useWebSocket.ts` |
| SSO 登录 | `src/hooks/web/useSso.ts` |
| 第三方登录 | `src/hooks/system/useThirdLogin.ts` |
| BPM 通信 | `src/assets/bpm/domain.js` |
| 视频播放器 | `src/views/fire/video/VideoPlayer.vue` |
| 环境配置 | `.env.development` / `.env.production` / `.env.test1` |

### 6.2 Temporal 核心概念对照

| Temporal 概念 | 当前实现对应物 | 迁移后改进 |
|--------------|--------------|-----------|
| Workflow | Job Controller 端点 | 持久化、断点续传、可视化 |
| Activity | Handler/Service 方法 | 自动重试、限流、超时控制 |
| Worker | 无（主服务内执行） | 独立扩缩容、资源隔离 |
| Workflow.sleep() | XXL-JOB cron + 扫库 | 零资源占用、精确触发 |
| Signal | 无（HTTP 回调） | 持久化等待、人工审批 |
| RetryPolicy | 手写重试(1-3次) | 指数退避、自动执行 |
| Event Sourcing | 无 | 完整执行历史、故障恢复 |
| WorkflowId | 无 | 天然防重、幂等保证 |

### 6.3 版本信息

| 组件 | 当前版本 | Temporal 迁移推荐版本 |
|------|---------|---------------------|
| Temporal Server | - | `temporalio/auto-setup:1.24.2` |
| Temporal Web UI | - | `temporalio/ui:2.28.0` |
| Temporal Java SDK | - | `temporal-sdk:1.24.0` |
| Java | 8 (推断) | 17 |
| Spring Boot | 2.x (推断) | 3.x |
| PostgreSQL | - | 15-alpine |
| 海康 Artemis SDK | 1.1.3 | 保持不变 |

---

*报告结束*
