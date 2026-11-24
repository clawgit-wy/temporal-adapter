# 人员权限下发工作流 - 便携部署包

## 目录结构
```
permission-workflow-package/
├── demo-worker/                  # Worker 项目（含权限下发工作流）
│   ├── src/                      # Java 源码
│   ├── pom.xml                   # Maven 配置 (Java 8)
│   └── Dockerfile                # 可选: Docker 构建文件
├── mokapi/                       # Mock API 服务
│   ├── src/                      # Java 源码
│   ├── pom.xml                   # Maven 配置 (Java 17)
│   └── Dockerfile               # 可选: Docker 构建文件
├── docs/                         # 文档
│   ├── 人员权限下发工作流指南.md   # 完整使用文档
│   ├── Worker调用演示完整指南.md   # Worker 调用指南
│   └── api-specs/                # API 规范文档
├── logs/                         # 运行日志目录（运行后自动生成）
├── start.sh                      # 一键启动（本地模式）
├── stop.sh                       # 停止服务
└── README.md                     # 本文件
```

## 前置条件

| 依赖 | 版本 | 说明 |
|------|------|------|
| JDK 8 | 1.8+ | demo-worker 编译运行 |
| JDK 17 | 17+ | mokapi 编译运行 |
| Maven | 3.6+ | 项目构建 |
| Temporal CLI | 最新 | 本地 Temporal Server |

安装 Temporal CLI:
```bash
# macOS
brew install temporal

# Linux/WSL
curl -sSf https://temporal.download/cli.sh | sh
```

## 快速开始

```bash
# 1. 解压
unzip permission-workflow-package.zip
cd permission-workflow-package

# 2. 一键启动（自动启动 Temporal Server + mokapi + demo-worker）
./start.sh

# 3. 验证服务
curl http://localhost:18093/api/permission/health
curl http://localhost:19090/Api/FaceHandSet/health

# 4. 测试人脸发行
curl -X POST http://localhost:18093/api/permission/face-issue \
  -H "Content-Type: application/json" \
  -d '{"token":"CARD-001","staff":{"staffNo":"EMP-001","staffName":"测试用户"}}'

# 5. 查看 Temporal UI
open http://localhost:8280

# 6. 查看日志
tail -f mokapi/logs/mokapi-worker-calls.log       # mokapi 调用记录
tail -f demo-worker/logs/demo-worker-activity.log  # Activity 日志

# 7. 停止所有服务
./stop.sh
```

## 手动分步启动

如果需要分别启动各组件:

```bash
# 终端1: 启动 Temporal Server
temporal server start --port 7233 --ui-port 8280 --db-filename /tmp/temporal-dev.db

# 终端2: 启动 mokapi (JDK 17)
cd mokapi && mvn spring-boot:run

# 终端3: 启动 demo-worker (JDK 8)
cd demo-worker && mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## 详细文档

请参阅 `docs/人员权限下发工作流指南.md` 获取完整的使用说明、API 文档、Temporal CLI 命令和日志查看方法。

## 技术栈

- **Temporal Workflow SDK** - 工作流引擎
- **Spring Boot 2.7.18** (demo-worker, Java 8)
- **Spring Boot 3.2.5** (mokapi, Java 17)
- **Temporal CLI** - 本地 Temporal Server
