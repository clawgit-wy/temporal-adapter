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
