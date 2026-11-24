# Temporal Web UI 二次开发与落地实施路径

> 本文档从 [Temporal业务应用方案介绍.md](file:///Users/apple/Desktop/AdapterService/docs/Temporal业务应用方案介绍.md) 中拆分而出，聚焦于 Temporal 的 Web UI 自研方案与分阶段落地实施路径。

---

## 一、Web UI 二次开发

### 1.1 为什么要自研 UI

Temporal 官方 Web UI 面向技术人员，展示的是底层 gRPC Event History 细节。对于业务团队而言，需要的是：

- 将 Workflow 状态**翻译成业务语言**（如"下发中/已完成/失败"而非"Running/Completed/Failed"）
- 整合进现有管理后台，统一权限管控
- 屏蔽底层技术细节，只展示业务关心的字段

### 1.2 BFF 架构模式

采用 **BFF（Backend For Frontend）** 模式，前端不直连 Temporal gRPC，由自研后端层统一接入：

| 优势 | 说明 |
| --- | --- |
| 鉴权统一 | 复用业务系统 JWT + RBAC，无需独立账号体系 |
| 数据脱敏 | BFF 层做字段过滤与二次加工 |
| 业务整合 | 将 Workflow 状态翻译为业务语言 |
| 加密解密 | 集成 Codec Server 解密加密 Payload |

### 1.3 核心 API 设计

| 接口分类 | 功能 | 说明 |
| --- | --- | --- |
| **查询类** | 工作流列表 / 执行详情 / 事件历史 / 状态查询 | 分页查询 + 实时状态 |
| **控制类** | 发送 Signal / 取消 / 终止 / 重置 | 按 RBAC 权限控制 |
| **业务类** | 启动工作流 / 查询工作流类型 | 业务前置校验 + 参数封装 |

### 1.4 权限设计

| 角色 | 权限 |
| --- | --- |
| `viewer` | 只读：查看工作流列表、状态、历史 |
| `operator` | 可操作：发送 Signal、取消工作流 |
| `admin` | 全权限：终止、重置工作流 |

---

## 二、落地实施路径

### 2.1 分阶段实施计划

```
阶段一：PoC 验证（1~2周）
├── Docker Compose 搭建本地 Temporal 环境
├── 编写 5 个验证场景的测试代码（详见 [验证场景.md](file:///Users/apple/Desktop/AdapterService/docs/验证场景.md)）
├── 执行破坏性测试（Kill 进程、模拟接口故障等）
└── 输出验证报告，确认 Temporal 满足业务需求

阶段二：核心业务接入（2~3周）
├── 搭建测试环境部署（四层分离架构）
├── 将海康批量校验场景迁移到 Temporal Workflow
├── 实现限流、重试、Webhook 回调
└── 功能测试 + 压力测试

阶段三：UI 二次开发（2~3周）
├── BFF 后端开发（鉴权 + API 封装）
├── Vue3 前端页面开发（工作流列表/详情/控制）
├── 整合进现有管理后台
└── 联调测试

阶段四：生产部署（1周）
├── 生产环境四层分离部署
├── 监控告警配置
├── 灰度切流
└── 全量上线
```

### 2.2 技术选型

| 层级 | 选型 | 理由 |
| --- | --- | --- |
| Temporal Server | temporalio/auto-setup:1.24.2 | 官方镜像，自动初始化 |
| 数据库 | PostgreSQL 15 | 官方推荐，稳定可靠 |
| Worker | Java 17 + Spring Boot 3 + Temporal Java SDK | 与业务主应用同构 |
| BFF 后端 | Java 17 + Spring Boot 3 | 复用鉴权/审计体系 |
| UI 前端 | Vue 3 + TypeScript + Ant Design Vue | 与现有前端栈一致 |
| 部署 | Docker / Docker Compose / K8s | 容器化部署，便于扩缩容 |

---

> **相关文档**：
> - 业务方案与痛点分析：参见 [Temporal业务应用方案介绍.md](file:///Users/apple/Desktop/AdapterService/docs/Temporal业务应用方案介绍.md)
> - PoC 验证场景与操作指南：参见 [验证场景.md](file:///Users/apple/Desktop/AdapterService/docs/验证场景.md)
> - 部署架构详细设计：参见 [Temporal-WebUI二次开发与部署设计文档.md](file:///Users/apple/Desktop/AdapterService/docs/Temporal-WebUI二次开发与部署设计文档.md)
