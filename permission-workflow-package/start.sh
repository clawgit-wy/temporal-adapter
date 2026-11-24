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
