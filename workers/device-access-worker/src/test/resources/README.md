# 楼宇门禁权限下发工作流测试说明

## 前置条件

### 1. 安装 temporal-cli

```bash
# 方式一: Go 安装（需要 Go 1.22+）
go install github.com/temporalio/cli/cmd/temporal@latest

# 方式二: Homebrew (macOS)
brew install temporal

# 方式三: 下载二进制
# https://github.com/temporalio/cli/releases
```

### 2. 启动 Temporal Server

```bash
# 使用 Docker Compose 快速启动
curl -sSfL https://raw.githubusercontent.com/temporalio/docker-compose/main/docker-compose.yml | docker compose -f - up -d
```

### 3. 启动 Worker

```bash
cd /Users/apple/Desktop/AdapterService/device-access-worker
mvn spring-boot:run
```

## 测试脚本

### 运行全部测试

```bash
chmod +x src/test/resources/run-workflow-tests.sh
./src/test/resources/run-workflow-tests.sh
```

### 测试场景一览

| 编号 | 测试场景 | 工作流 | TaskQueue | 说明 |
|------|---------|--------|-----------|------|
| 1 | 权限下发 | PermissionIssueWorkflow | PERMISSION_ISSUE_TASK_QUEUE | 向富士设备下发单个人员的门禁权限 |
| 2 | 权限改写 | PermissionModifyWorkflow | PERMISSION_MODIFY_TASK_QUEUE | 修改已发行的人员门禁权限 |
| 3 | 权限注销 | PermissionCancelWorkflow | PERMISSION_CANCEL_TASK_QUEUE | 注销人脸和卡权限（3个GID） |
| 4 | 增量权限-增加 | PermissionIncrementWorkflow | PERMISSION_INCREMENT_TASK_QUEUE | 为已发行人员增加新设备权限 |
| 5 | 增量权限-删除 | PermissionIncrementWorkflow | PERMISSION_INCREMENT_TASK_QUEUE | 删除已发行人员在指定设备的权限 |
| 6 | 批量下发 | BatchPermissionIssueWorkflow | BATCH_ISSUE_TASK_QUEUE | 批量并发下发（3人，20 QPS限流） |
| 7 | 访客下发-自动 | VisitorPermissionIssueWorkflow | VISITOR_ISSUE_TASK_QUEUE | 访客自动下发（排除涉密厂区） |
| 8 | 访客下发-手动 | VisitorPermissionIssueWorkflow | VISITOR_ISSUE_TASK_QUEUE | 门卫手动下发（包含涉密厂区） |

## 手动执行单个测试

### 权限下发

```bash
temporal workflow start \
    --address 127.0.0.1:7233 \
    --task-queue PERMISSION_ISSUE_TASK_QUEUE \
    --workflow-type PermissionIssueWorkflow \
    --workflow-id test-issue-001 \
    --input '{
        "batchId": "TEST-ISSUE-001",
        "deviceType": "FUJI",
        "operationType": "ISSUE",
        "staffList": [
            {
                "StaffNo": "TEST001",
                "StaffName": "测试人员",
                "Sex": 0,
                "OrganizationId": "C8BBDAA5-BA9E-41F2-BF0C-05522AA4DEC7",
                "Rid": "TEST001"
            }
        ],
        "authorizeDeviceNo": [3, 4],
        "authDevice": ["1", "2"],
        "beginTime": "2025-01-01",
        "endTime": "2030-12-31",
        "facePicture": "",
        "projectGids": ["FF7F139D-B80C-46D0-B330-1118F00255B7"]
    }'
```

### 权限注销

```bash
temporal workflow start \
    --address 127.0.0.1:7233 \
    --task-queue PERMISSION_CANCEL_TASK_QUEUE \
    --workflow-type PermissionCancelWorkflow \
    --workflow-id test-cancel-001 \
    --input '{
        "batchId": "TEST-CANCEL-001",
        "deviceType": "FUJI",
        "operationType": "CANCEL",
        "staffList": [
            {
                "StaffNo": "TEST001",
                "StaffName": "测试人员",
                "Sex": 0,
                "OrganizationId": "C8BBDAA5-BA9E-41F2-BF0C-05522AA4DEC7",
                "Rid": "TEST001"
            }
        ],
        "projectGids": [
            "8A0ADE28-0000-0000-0000-000000000001",
            "FF7F139D-0000-0000-0000-000000000002",
            "35EC9884-0000-0000-0000-000000000003"
        ]
    }'
```

## 查看测试结果

```bash
# 查看所有工作流
temporal workflow list --address 127.0.0.1:7233

# 查看指定工作流详情
temporal workflow describe --address 127.0.0.1:7233 --workflow-id test-issue-001

# 查看工作流执行历史（含每步Activity调用）
temporal workflow show --address 127.0.0.1:7233 --workflow-id test-issue-001

# 查看工作流最终结果
temporal workflow query --address 127.0.0.1:7233 --workflow-id test-issue-001 --query-type __result
```

## Temporal Web UI

打开浏览器访问 `http://localhost:8233`，可以可视化查看：

- 工作流执行状态
- Activity 调用链路
- 重试历史
- 输入/输出数据
- 错误详情
