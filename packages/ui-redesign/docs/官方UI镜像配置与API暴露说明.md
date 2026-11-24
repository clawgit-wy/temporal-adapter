# Temporal 官方 UI 镜像配置与 API 暴露说明

> **镜像版本**：`temporalio/ui:2.32.0`
> **更新日期**：2026-08-17

---

## 一、官方镜像架构

```
┌─────────────────────────────────────────────────────┐
│           temporalio/ui:2.32.0 容器                  │
│                                                     │
│  ┌─────────────┐    ┌──────────────────────────┐   │
│  │  静态前端   │    │   API 代理层 (Go Server)  │   │
│  │  (端口 8080) │    │   /api/v1/* → gRPC        │   │
│  │  Svelte UI  │    │   转发到 Temporal Server   │   │
│  └─────────────┘    └──────────────────────────┘   │
│                                                     │
│  环境变量: TEMPORAL_ADDRESS=temporal-server:7233    │
└─────────────────────────────────────────────────────┘
         │                              │
         ▼                              ▼
    浏览器访问                    Temporal Server
    http://localhost:8080         gRPC:7233
```

**关键结论**：官方镜像自带一个 Go 编写的 API 代理层，将 `/api/v1/*` HTTP 请求转换为 gRPC 调用转发到 Temporal Server。这个 API 代理层**对外暴露**，可以被任意前端项目使用。

---

## 二、API 端点清单

### 2.1 已验证可用的端点

| 端点 | 方法 | 说明 | 响应格式 |
|------|------|------|---------|
| `/api/v1/settings` | GET | UI 配置信息 | **PascalCase** |
| `/api/v1/cluster-info` | GET | 集群信息 | camelCase |
| `/api/v1/system-info` | GET | 系统版本信息 | camelCase |
| `/api/v1/namespaces` | GET | 命名空间列表 | camelCase |
| `/api/v1/namespaces/{ns}` | GET | 命名空间详情 | camelCase |
| `/api/v1/namespaces/{ns}/workflows` | GET | 工作流列表 | camelCase |
| `/api/v1/namespaces/{ns}/workflows/{id}` | GET | 工作流详情 | camelCase |
| `/api/v1/namespaces/{ns}/workflows/{id}` | POST | 启动工作流 | camelCase |
| `/api/v1/namespaces/{ns}/workflows/{id}/terminate` | POST | 终止工作流 | - |
| `/api/v1/namespaces/{ns}/workflows/{id}/cancel` | POST | 取消工作流 | - |
| `/api/v1/namespaces/{ns}/workflows/{id}/query/{type}` | POST | 查询工作流 | camelCase |
| `/api/v1/namespaces/{ns}/workflows/{id}/history` | GET | 历史事件 | camelCase |
| `/api/v1/namespaces/{ns}/workflow-count` | GET | 工作流计数 | camelCase |
| `/api/v1/namespaces/{ns}/task-queues/{name}` | GET | 任务队列信息 | camelCase |
| `/api/v1/namespaces/{ns}/schedules` | GET | 调度列表 | camelCase |

### 2.2 字段命名差异

```
/api/v1/settings → PascalCase
{
  "Auth": { "Enabled": false },
  "DefaultNamespace": "default",
  "Version": "2.32.0"
}

/api/v1/cluster-info → camelCase
{
  "clusterName": "active",
  "clusterId": "addbba57-...",
  "persistenceStore": "postgres12"
}
```

**原因**：`/api/v1/settings` 由 UI Server 自身生成（Go 结构体默认 PascalCase JSON tag），而其他端点是 gRPC 代理响应（protobuf JSON mapping 使用 camelCase）。

**解决方案**：在 API 客户端中使用 `convertKeys()` 递归转换所有响应为 camelCase（已在 [client.ts](file:///Users/apple/Desktop/AdapterService/ui-redesign/src/lib/api/client.ts) 中实现）。

---

## 三、环境变量配置

### 3.1 核心配置

| 环境变量 | 默认值 | 说明 |
|---------|--------|------|
| `TEMPORAL_ADDRESS` | - | **必填**，Temporal Server 的 gRPC 地址 |
| `TEMPORAL_UI_PORT` | `8080` | UI Server 监听端口 |
| `TEMPORAL_UI_ENABLED` | `true` | 是否启用 UI 界面 |
| `TEMPORAL_DEFAULT_NAMESPACE` | `default` | 默认命名空间 |
| `TEMPORAL_AUTH_ENABLED` | `false` | 是否启用认证 |

### 3.2 CORS 配置

| 环境变量 | 默认值 | 说明 |
|---------|--------|------|
| `TEMPORAL_CORS_UNSAFE_ALLOW_ALL_ORIGINS` | `false` | **关键**：设为 `true` 允许所有来源跨域访问 |

**重要说明**：

即使设置了 `TEMPORAL_CORS_UNSAFE_ALLOW_ALL_ORIGINS=true`，官方镜像的 CORS 实现存在以下限制：

1. **不返回 `Access-Control-Allow-Origin` 响应头**：实际测试中，GET 请求不返回 CORS 头
2. **OPTIONS 预检返回 204 但无 CORS 头**：浏览器仍会拦截跨域请求

**结论**：CORS 环境变量不可靠。推荐使用 **反向代理** 方式解决跨域问题。

### 3.3 CSRF 保护

官方镜像默认启用 CSRF 保护：

```
Set-Cookie: _csrf=xxx; Path=/; Secure; SameSite=Strict
```

**问题**：
- Cookie 设置了 `Secure` 标志，仅 HTTPS 下浏览器才会存储
- `SameSite=Strict` 阻止跨站 Cookie 发送
- POST/PUT/DELETE 请求需要 `X-CSRF-Token` 请求头

**解决方案**（开发环境）：

在 Vite 代理中剥离 `Secure` 和 `SameSite=Strict` 标志（已在 [vite.config.ts](file:///Users/apple/Desktop/AdapterService/ui-redesign/vite.config.ts) 中实现）：

```typescript
proxy: {
  '/api/v1': {
    target: 'http://localhost:8080',
    changeOrigin: true,
    onProxyRes: (proxyRes) => {
      const setCookie = proxyRes.headers['set-cookie']
      if (setCookie) {
        proxyRes.headers['set-cookie'] = setCookie.map(cookie =>
          cookie.replace(/;\s*Secure/gi, '')
                .replace(/;\s*SameSite=Strict/gi, '; SameSite=Lax')
        )
      }
    },
  },
},
```

---

## 四、Docker Compose 部署配置

### 4.1 推荐配置

```yaml
# docker-compose.ui-server.yml
services:
  ui-server:
    image: temporalio/ui:2.32.0
    container_name: temporal-ui-server
    environment:
      # 连接 Temporal Server
      - TEMPORAL_ADDRESS=temporal-server:7233
      - TEMPORAL_UI_PORT=8080
      # 禁用认证（开发环境）
      - TEMPORAL_AUTH_ENABLED=false
      # 默认命名空间
      - TEMPORAL_DEFAULT_NAMESPACE=default
      # CORS（不可靠，建议使用反向代理）
      - TEMPORAL_CORS_UNSAFE_ALLOW_ALL_ORIGINS=true
    ports:
      - "8080:8080"
    networks:
      - temporal_default
    restart: unless-stopped

networks:
  temporal_default:
    external: true
```

### 4.2 启动命令

```bash
# 启动 UI Server
docker compose -f docker-compose.ui-server.yml up -d

# 验证启动
curl http://localhost:8080/api/v1/settings
curl http://localhost:8080/api/v1/cluster-info
```

---

## 五、前端对接方案

### 5.1 方案对比

| 方案 | CORS | CSRF | 复杂度 | 推荐场景 |
|------|------|------|--------|---------|
| **Vite 代理（推荐）** | 无需配置 | 代理层处理 | 低 | 开发环境 |
| Nginx 反向代理 | 无需配置 | 需额外处理 | 中 | 生产环境 |
| 直接跨域调用 | 不可靠 | 无法解决 | - | 不推荐 |

### 5.2 Vite 代理方案（当前使用）

```
浏览器 (localhost:5175)
    │
    ├── 静态资源 → Vite Dev Server
    │
    └── /api/v1/* → Vite Proxy → Temporal UI Server (localhost:8080)
                                         │
                                         └── gRPC → Temporal Server (localhost:7233)
```

**优点**：
- 浏览器看到的所有请求都是同源（`localhost:5175`），无 CORS 问题
- Vite 代理可修改响应头，解决 CSRF Cookie 的 `Secure` 标志问题
- 无需额外服务

### 5.3 生产环境 Nginx 方案

```nginx
server {
    listen 80;
    server_name your-domain.com;

    # 静态前端文件
    location / {
        root /usr/share/nginx/html;
        try_files $uri $uri/ /index.html;
    }

    # API 代理到 Temporal UI Server
    location /api/v1/ {
        proxy_pass http://temporal-ui-server:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;

        # 修复 CSRF Cookie
        proxy_cookie_flags _csrf nosecure samesite=lax;
    }
}
```

---

## 六、API 调用示例

### 6.1 获取工作流列表

```bash
curl "http://localhost:8080/api/v1/namespaces/default/workflows?pageSize=10"
```

响应：
```json
{
  "executions": [
    {
      "execution": { "workflowId": "demo-ui-test-001", "runId": "9c76220d-..." },
      "type": { "name": "DemoWorkflow" },
      "status": "WORKFLOW_EXECUTION_STATUS_RUNNING",
      "startTime": "2026-08-17T02:47:02.379206Z",
      "taskQueue": "DEMO_TASK_QUEUE"
    }
  ]
}
```

### 6.2 启动工作流（需 CSRF）

```bash
# 1. 获取 CSRF token
CSRF=$(curl -s -c - http://localhost:8080/api/v1/settings | grep _csrf | awk '{print $NF}')

# 2. 启动工作流
curl -X POST "http://localhost:8080/api/v1/namespaces/default/workflows/my-workflow-id" \
  -H "Content-Type: application/json" \
  -H "X-CSRF-Token: $CSRF" \
  -b "_csrf=$CSRF" \
  -d '{
    "workflowId": "my-workflow-id",
    "taskQueue": {"name": "DEMO_TASK_QUEUE"},
    "workflowType": {"name": "DemoWorkflow"},
    "input": {
      "payloads": [{
        "metadata": {"encoding": "json/plain"},
        "data": "BASE64_ENCODED_JSON"
      }]
    }
  }'
```

### 6.3 Input 编码说明

Temporal API 的 `input` 字段使用 Payload 格式：

```json
{
  "payloads": [{
    "metadata": { "encoding": "json/plain" },
    "data": "BASE64_ENCODED_JSON_STRING"
  }]
}
```

**Base64 编码**：将 JSON 字符串进行 Base64 编码后填入 `data` 字段。

```javascript
// JavaScript 编码示例
const input = { workflowId: "demo-001", personId: "P001", name: "张三" };
const base64Data = btoa(JSON.stringify(input));
```

---

## 七、验证清单

- [x] **Docker 镜像启动**：`temporalio/ui:2.32.0` 正常运行
- [x] **API 端点暴露**：`/api/v1/*` 对外可访问
- [x] **Temporal 连接**：UI Server 成功连接 Temporal Server
- [x] **工作流列表**：API 返回真实工作流数据
- [x] **工作流启动**：POST 请求可成功启动工作流（需 CSRF）
- [x] **集群信息**：返回集群名称、存储类型等信息
- [x] **Vite 代理**：前端通过代理正常访问 API
- [x] **CSRF 修复**：代理层剥离 `Secure` 标志，POST 请求正常工作
- [x] **字段名转换**：`convertKeys()` 统一 PascalCase → camelCase
- [x] **Demo Worker**：`DEMO_TASK_QUEUE` 已注册并有工作流执行

---

*文档结束*
