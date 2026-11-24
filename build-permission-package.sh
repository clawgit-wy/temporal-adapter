#!/bin/bash
##############################################################################
# 人员权限下发工作流 - 便携打包脚本
#
# 将以下组件打包为一个可移植的 ZIP 文件:
# 1. demo-worker 工作流代码 (含权限下发完整实现)
# 2. mokapi 接口模拟配置及数据
# 3. 文档说明 (配置指南和使用说明)
#
# 使用方法:
#   chmod +x build-permission-package.sh
#   ./build-permission-package.sh
#
# 产出: permission-workflow-package.zip
##############################################################################
set -e

PROJECT_ROOT="$(cd "$(dirname "$0")" && pwd)"
PKG_DIR="${PROJECT_ROOT}/permission-workflow-package"
ZIP_FILE="${PROJECT_ROOT}/permission-workflow-package.zip"

echo "=============================================="
echo "  人员权限下发工作流 - 便携打包"
echo "=============================================="
echo ""

# 1. 清理旧的打包目录
echo "[1/8] 清理旧的打包目录..."
rm -rf "${PKG_DIR}" "${ZIP_FILE}"
mkdir -p "${PKG_DIR}"

# 2. 复制 demo-worker 项目
echo "[2/8] 复制 demo-worker 项目..."
mkdir -p "${PKG_DIR}/demo-worker"
cp -r "${PROJECT_ROOT}/workers/demo-worker/src" "${PKG_DIR}/demo-worker/"
cp "${PROJECT_ROOT}/workers/demo-worker/pom.xml" "${PKG_DIR}/demo-worker/"
cp "${PROJECT_ROOT}/workers/demo-worker/Dockerfile" "${PKG_DIR}/demo-worker/"
cp "${PROJECT_ROOT}/workers/demo-worker/docker-compose.demo.yml" "${PKG_DIR}/demo-worker/docker-compose.demo.yml.optional"

# 3. 复制 mokapi 项目
echo "[3/8] 复制 mokapi 项目..."
mkdir -p "${PKG_DIR}/mokapi"
cp -r "${PROJECT_ROOT}/mokapi/src" "${PKG_DIR}/mokapi/"
cp "${PROJECT_ROOT}/mokapi/pom.xml" "${PKG_DIR}/mokapi/"
cp "${PROJECT_ROOT}/mokapi/Dockerfile" "${PKG_DIR}/mokapi/"

# 4. 复制 API 规范文档
echo "[4/8] 复制 API 规范文档..."
mkdir -p "${PKG_DIR}/docs/api-specs"
cp -r "${PROJECT_ROOT}/demos/请求示例/"* "${PKG_DIR}/docs/api-specs/" 2>/dev/null || true

# 5. 复制使用文档
echo "[5/8] 复制使用文档..."
cp "${PROJECT_ROOT}/docs/人员权限下发工作流指南.md" "${PKG_DIR}/docs/" 2>/dev/null || true
cp "${PROJECT_ROOT}/docs/Worker调用演示完整指南.md" "${PKG_DIR}/docs/" 2>/dev/null || true

# 6. 创建日志目录
echo "[6/8] 创建日志目录..."
mkdir -p "${PKG_DIR}/logs/mokapi"
mkdir -p "${PKG_DIR}/logs/demo-worker"

# 7. 创建启动脚本
echo "[7/8] 创建启动脚本..."

# 一键启动脚本（本地模式，不依赖 Docker）
cat > "${PKG_DIR}/start.sh" << 'SCRIPT'
#!/bin/bash
set -e
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

echo "=============================================="
echo "  人员权限下发工作流 - 本地启动"
echo "=============================================="
echo ""

# 检查依赖
echo "前置条件检查:"
echo "  - JDK 8 (demo-worker)"
echo "  - JDK 17 (mokapi)"
echo "  - Maven 3.6+"
echo "  - Temporal CLI (已安装)"
echo ""

# 检查 Temporal CLI
if ! command -v temporal &> /dev/null; then
    echo "[ERROR] 未检测到 temporal CLI，请先安装："
    echo "  macOS:  brew install temporal"
    echo "  Linux:  curl -sSf https://temporal.download/cli.sh | sh"
    exit 1
fi

# Step 1: 启动 Temporal Server
echo "[1/3] 启动 Temporal Server..."
temporal server start --port 7233 --ui-port 8280 --db-filename /tmp/temporal-dev.db --log-level error &
TEMPORAL_PID=$!
echo "Temporal Server PID: ${TEMPORAL_PID}"

echo "等待 Temporal Server 启动..."
sleep 5

# 验证 Temporal
if curl -s http://localhost:7233/health > /dev/null 2>&1 || curl -s http://localhost:7233 > /dev/null 2>&1; then
    echo "  Temporal Server 已启动 (PID: ${TEMPORAL_PID})"
else
    echo "  [WARNING] Temporal Server 可能仍在启动中，请稍等..."
fi

# Step 2: 编译并启动 mokapi（需要 JDK 17）
echo ""
echo "[2/3] 编译并启动 mokapi (需要 JDK 17)..."
cd "${SCRIPT_DIR}/mokapi"
if [ ! -f target/mokapi-1.0.0.jar ]; then
    echo "  编译 mokapi..."
    mvn clean package -DskipTests -q
fi
java -jar target/mokapi-1.0.0.jar &
MOKAPI_PID=$!
echo "mokapi PID: ${MOKAPI_PID}"

echo "等待 mokapi 启动..."
sleep 8

# 验证 mokapi
if curl -s http://localhost:19090/Api/FaceHandSet/health > /dev/null 2>&1; then
    echo "  mokapi 已启动 (PID: ${MOKAPI_PID})"
else
    echo "  [WARNING] mokapi 可能仍在启动中，请稍等..."
fi

# Step 3: 编译并启动 demo-worker（需要 JDK 8）
echo ""
echo "[3/3] 编译并启动 demo-worker (需要 JDK 8)..."
cd "${SCRIPT_DIR}/demo-worker"
if [ ! -f target/demo-worker-1.0.0.jar ]; then
    echo "  编译 demo-worker..."
    mvn clean package -DskipTests -q -Pdev
fi
java -jar target/demo-worker-1.0.0.jar &
WORKER_PID=$!
echo "demo-worker PID: ${WORKER_PID}"

echo "等待 demo-worker 启动..."
sleep 8

# 验证 demo-worker
if curl -s http://localhost:18093/api/permission/health > /dev/null 2>&1; then
    echo "  demo-worker 已启动 (PID: ${WORKER_PID})"
else
    echo "  [WARNING] demo-worker 可能仍在启动中，请稍等..."
fi

# 保存 PID 文件
echo "${TEMPORAL_PID}" > "${SCRIPT_DIR}/.temporal.pid"
echo "${MOKAPI_PID}" > "${SCRIPT_DIR}/.mokapi.pid"
echo "${WORKER_PID}" > "${SCRIPT_DIR}/.worker.pid"

echo ""
echo "=============================================="
echo "  启动完成！"
echo "=============================================="
echo ""
echo "服务地址:"
echo "  - Temporal UI:  http://localhost:8280"
echo "  - demo-worker:  http://localhost:18093"
echo "  - mokapi:       http://localhost:19090"
echo ""
echo "进程 PID:"
echo "  - Temporal Server: ${TEMPORAL_PID}"
echo "  - mokapi:          ${MOKAPI_PID}"
echo "  - demo-worker:     ${WORKER_PID}"
echo ""
echo "快速验证:"
echo "  curl http://localhost:18093/api/permission/health"
echo "  curl http://localhost:19090/Api/FaceHandSet/health"
echo ""
echo "测试人脸发行:"
echo '  curl -X POST http://localhost:18093/api/permission/face-issue \'
echo '    -H "Content-Type: application/json" \'
echo '    -d '"'"'{"token":"CARD-001","staff":{"staffNo":"EMP-001","staffName":"测试用户"}}'"'"
echo ""
echo "停止服务:"
echo "  ./stop.sh"
echo ""
echo "查看日志:"
echo "  tail -f mokapi/logs/mokapi-worker-calls.log      # mokapi 调用记录"
echo "  tail -f demo-worker/logs/demo-worker.log          # worker 主日志"
echo "  tail -f demo-worker/logs/demo-worker-activity.log  # Activity 日志"
echo ""
SCRIPT

# 停止脚本
cat > "${PKG_DIR}/stop.sh" << 'SCRIPT'
#!/bin/bash
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

echo "停止所有服务..."

# 读取 PID 并停止
for pidfile in .temporal.pid .mokapi.pid .worker.pid; do
    pidfile_path="${SCRIPT_DIR}/${pidfile}"
    if [ -f "$pidfile_path" ]; then
        PID=$(cat "$pidfile_path")
        if kill -0 "$PID" 2>/dev/null; then
            kill "$PID"
            echo "  已停止 ${pidfile#.} (PID: ${PID})"
        else
            echo "  ${pidfile#.} (PID: ${PID}) 已不在运行"
        fi
        rm -f "$pidfile_path"
    fi
done

echo "服务已停止。"
SCRIPT

# 添加执行权限
chmod +x "${PKG_DIR}/start.sh" "${PKG_DIR}/stop.sh"

# 8. 生成 README
echo "[8/8] 生成 README..."
cat > "${PKG_DIR}/README.md" << 'EOF'
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
EOF

# 打包为 ZIP
echo ""
echo "正在打包为 ZIP..."
cd "${PROJECT_ROOT}"
zip -r "${ZIP_FILE}" "permission-workflow-package/" -q

echo ""
echo "=============================================="
echo "  打包完成！"
echo "=============================================="
echo ""
echo "ZIP 文件: ${ZIP_FILE}"
echo "目录:    ${PKG_DIR}"
echo ""
echo "部署步骤:"
echo "  1. 将 permission-workflow-package.zip 拷贝到目标机器"
echo "  2. unzip permission-workflow-package.zip"
echo "  3. cd permission-workflow-package"
echo "  4. ./start.sh"
echo ""
