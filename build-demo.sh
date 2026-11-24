#!/bin/bash
##############################################################################
# Demo Worker 演示项目一键打包脚本
#
# 用途：将 demo-worker、mokapi 项目打包为可部署的完整包
#
# 使用方法：
#   chmod +x build-demo.sh
#   ./build-demo.sh
#
# 产出目录：demo-deploy-package/
##############################################################################
set -e

PROJECT_ROOT="$(cd "$(dirname "$0")" && pwd)"
DEPLOY_DIR="${PROJECT_ROOT}/demo-deploy-package"

echo "=============================================="
echo "  Demo Worker 演示项目打包"
echo "=============================================="
echo ""

# 1. 清理旧的打包目录
echo "[1/7] 清理旧的打包目录..."
rm -rf "${DEPLOY_DIR}"
mkdir -p "${DEPLOY_DIR}"

# 2. 复制 demo-worker 项目
echo "[2/7] 复制 demo-worker 项目..."
mkdir -p "${DEPLOY_DIR}/demo-worker"
cp -r "${PROJECT_ROOT}/workers/demo-worker/src" "${DEPLOY_DIR}/demo-worker/"
cp "${PROJECT_ROOT}/workers/demo-worker/pom.xml" "${DEPLOY_DIR}/demo-worker/"
cp "${PROJECT_ROOT}/workers/demo-worker/Dockerfile" "${DEPLOY_DIR}/demo-worker/"
cp "${PROJECT_ROOT}/workers/demo-worker/docker-compose.demo.yml" "${DEPLOY_DIR}/demo-worker/"
cp "${PROJECT_ROOT}/workers/demo-worker/src/main/resources/application.yml" "${DEPLOY_DIR}/demo-worker/application.yml.example"

# 3. 复制 mokapi 项目
echo "[3/7] 复制 mokapi 项目..."
mkdir -p "${DEPLOY_DIR}/mokapi"
cp -r "${PROJECT_ROOT}/mokapi/src" "${DEPLOY_DIR}/mokapi/"
cp "${PROJECT_ROOT}/mokapi/pom.xml" "${DEPLOY_DIR}/mokapi/"
cp "${PROJECT_ROOT}/mokapi/Dockerfile" "${DEPLOY_DIR}/mokapi/"

# 4. 复制 Docker 配置
echo "[4/7] 复制 Docker 配置..."
mkdir -p "${DEPLOY_DIR}/docker-config"
cp "${PROJECT_ROOT}/docker-config/docker-compose.temporal-stack.yml" "${DEPLOY_DIR}/docker-config/"

# 如果存在离线镜像包，复制
if [ -d "${PROJECT_ROOT}/docker-config/mirrors" ]; then
    echo "      复制离线镜像包..."
    cp -r "${PROJECT_ROOT}/docker-config/mirrors" "${DEPLOY_DIR}/docker-config/"
fi

# 5. 创建日志目录
echo "[5/7] 创建日志目录..."
mkdir -p "${DEPLOY_DIR}/logs/mokapi"
mkdir -p "${DEPLOY_DIR}/logs/demo-worker"

# 6. 复制文档
echo "[6/7] 复制文档..."
cp "${PROJECT_ROOT}/docs/验证场景.md" "${DEPLOY_DIR}/" 2>/dev/null || true

# 7. 生成包信息
echo "[7/7] 生成包信息..."
cat > "${DEPLOY_DIR}/README.md" << 'EOF'
# Demo Worker 演示部署包

## 目录结构
```
demo-deploy-package/
├── demo-worker/              # Worker 项目
│   ├── src/                  # Java 源码
│   ├── pom.xml              # Maven 配置
│   ├── Dockerfile           # Docker 构建文件
│   ├── docker-compose.demo.yml  # 一体化编排
│   └── application.yml.example  # 配置模板
├── mokapi/                   # Mock API 服务
│   ├── src/                  # Java 源码
│   ├── pom.xml              # Maven 配置
│   └── Dockerfile           # Docker 构建文件
├── docker-config/            # Docker 编排配置
│   ├── docker-compose.temporal-stack.yml  # Temporal 全栈
│   └── mirrors/             # 离线镜像包（如有）
├── logs/                     # 运行日志目录
└── README.md                 # 本文件
```

## 快速开始
1. 安装 Docker 和 Docker Compose
2. cd demo-worker && docker compose -f docker-compose.demo.yml up -d
3. 访问 http://localhost:8280 查看 Temporal UI
4. 访问 http://localhost:18093/api/demo/health 检查 Worker 状态
5. 参阅《Worker调用演示完整指南.md》执行演示场景
EOF

# 打包
echo ""
echo "=============================================="
echo "  打包完成！"
echo "  输出目录: ${DEPLOY_DIR}"
echo "=============================================="
echo ""
echo "下一步："
echo "  1. 将 demo-deploy-package 目录拷贝到目标机器"
echo "  2. cd demo-deploy-package/demo-worker"
echo "  3. docker compose -f docker-compose.demo.yml up -d"
echo ""
