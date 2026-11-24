#!/bin/bash
##############################################################################
# 楼宇门禁权限下发工作流 - temporal-cli 测试脚本
#
# 前置条件：
#   1. Temporal Server 已启动（默认地址 127.0.0.1:7233）
#   2. temporal-cli 已安装（安装方式: go install github.com/temporalio/cli/cmd/temporal@latest）
#   3. device-access-worker 已启动（mvn spring-boot:run）
#
# 使用方式：
#   chmod +x src/test/resources/run-workflow-tests.sh
#   ./src/test/resources/run-workflow-tests.sh
##############################################################################

TEMPORAL_ADDRESS="${TEMPORAL_ADDRESS:-127.0.0.1:7233}"
NAMESPACE="default"

echo "=========================================="
echo "楼宇门禁权限下发工作流测试"
echo "Temporal Address: ${TEMPORAL_ADDRESS}"
echo "=========================================="

# 等待用户确认
read -p "确认 Temporal Server 和 Worker 已启动? (y/n): " confirm
if [ "$confirm" != "y" ]; then
    echo "请先启动 Temporal Server 和 Worker"
    exit 1
fi

##############################################################################
# 测试1: 权限下发工作流 (PermissionIssueWorkflow)
# 场景：向富士设备下发单个人员的门禁权限
# 流程：发行校验 -> 人脸发行 -> 卡发行 -> Webhook回调
##############################################################################
echo ""
echo ">>> 测试1: 权限下发工作流 (PermissionIssueWorkflow)"
echo "    TaskQueue: PERMISSION_ISSUE_TASK_QUEUE"

WORKFLOW_ID="test-permission-issue-$(date +%s)"
temporal workflow start \
    --address "${TEMPORAL_ADDRESS}" \
    --namespace "${NAMESPACE}" \
    --task-queue PERMISSION_ISSUE_TASK_QUEUE \
    --workflow-type PermissionIssueWorkflow \
    --workflow-id "${WORKFLOW_ID}" \
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

echo ""
echo "    工作流ID: ${WORKFLOW_ID}"
echo "    查看结果: temporal workflow describe --address ${TEMPORAL_ADDRESS} --workflow-id ${WORKFLOW_ID}"
read -p "    按回车继续..." pause

##############################################################################
# 测试2: 权限改写工作流 (PermissionModifyWorkflow)
# 场景：修改已发行的人员门禁权限
# 流程：发行校验 -> 人脸改写 -> Webhook回调
##############################################################################
echo ""
echo ">>> 测试2: 权限改写工作流 (PermissionModifyWorkflow)"
echo "    TaskQueue: PERMISSION_MODIFY_TASK_QUEUE"

WORKFLOW_ID="test-permission-modify-$(date +%s)"
temporal workflow start \
    --address "${TEMPORAL_ADDRESS}" \
    --namespace "${NAMESPACE}" \
    --task-queue PERMISSION_MODIFY_TASK_QUEUE \
    --workflow-type PermissionModifyWorkflow \
    --workflow-id "${WORKFLOW_ID}" \
    --input '{
        "batchId": "TEST-MODIFY-001",
        "deviceType": "FUJI",
        "operationType": "MODIFY",
        "staffList": [
            {
                "StaffNo": "TEST001",
                "StaffName": "测试人员",
                "Sex": 0,
                "OrganizationId": "C8BBDAA5-BA9E-41F2-BF0C-05522AA4DEC7",
                "Rid": "TEST001"
            }
        ],
        "authorizeDeviceNo": [3, 7, 627],
        "authDevice": ["1", "2"],
        "beginTime": "2025-01-01",
        "endTime": "2030-12-31",
        "facePicture": "",
        "projectGids": ["FF7F139D-B80C-46D0-B330-1118F00255B7"]
    }'

echo ""
echo "    工作流ID: ${WORKFLOW_ID}"
read -p "    按回车继续..." pause

##############################################################################
# 测试3: 权限注销工作流 (PermissionCancelWorkflow)
# 场景：注销人员的人脸和卡权限（对3个GID逐一注销）
# 流程：查询卡号 -> 注销人脸(3个GID) -> 注销卡(3个GID) -> Webhook回调
##############################################################################
echo ""
echo ">>> 测试3: 权限注销工作流 (PermissionCancelWorkflow)"
echo "    TaskQueue: PERMISSION_CANCEL_TASK_QUEUE"

WORKFLOW_ID="test-permission-cancel-$(date +%s)"
temporal workflow start \
    --address "${TEMPORAL_ADDRESS}" \
    --namespace "${NAMESPACE}" \
    --task-queue PERMISSION_CANCEL_TASK_QUEUE \
    --workflow-type PermissionCancelWorkflow \
    --workflow-id "${WORKFLOW_ID}" \
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

echo ""
echo "    工作流ID: ${WORKFLOW_ID}"
read -p "    按回车继续..." pause

##############################################################################
# 测试4: 增量权限工作流 (PermissionIncrementWorkflow) - 增加设备权限
# 场景：为已发行人员增加新设备的权限
# 流程：人脸增量权限(powerType=0) -> Webhook回调
##############################################################################
echo ""
echo ">>> 测试4: 增量权限工作流 - 增加设备权限 (PermissionIncrementWorkflow)"
echo "    TaskQueue: PERMISSION_INCREMENT_TASK_QUEUE"

WORKFLOW_ID="test-permission-increment-add-$(date +%s)"
temporal workflow start \
    --address "${TEMPORAL_ADDRESS}" \
    --namespace "${NAMESPACE}" \
    --task-queue PERMISSION_INCREMENT_TASK_QUEUE \
    --workflow-type PermissionIncrementWorkflow \
    --workflow-id "${WORKFLOW_ID}" \
    --input '{
        "batchId": "TEST-INCREMENT-ADD-001",
        "deviceType": "FUJI",
        "operationType": "INCREMENT_ADD",
        "staffList": [
            {
                "StaffNo": "TEST001",
                "StaffName": "测试人员",
                "Sex": 0,
                "OrganizationId": "C8BBDAA5-BA9E-41F2-BF0C-05522AA4DEC7",
                "Rid": "TEST001"
            }
        ],
        "authorizeDeviceNo": [5, 6],
        "beginTime": "2025-01-01",
        "endTime": "2030-12-31",
        "facePicture": ""
    }'

echo ""
echo "    工作流ID: ${WORKFLOW_ID}"
read -p "    按回车继续..." pause

##############################################################################
# 测试5: 增量权限工作流 (PermissionIncrementWorkflow) - 删除设备权限
# 场景：删除已发行人员在指定设备上的权限
# 流程：人脸增量权限(powerType=1) -> Webhook回调
##############################################################################
echo ""
echo ">>> 测试5: 增量权限工作流 - 删除设备权限 (PermissionIncrementWorkflow)"
echo "    TaskQueue: PERMISSION_INCREMENT_TASK_QUEUE"

WORKFLOW_ID="test-permission-increment-delete-$(date +%s)"
temporal workflow start \
    --address "${TEMPORAL_ADDRESS}" \
    --namespace "${NAMESPACE}" \
    --task-queue PERMISSION_INCREMENT_TASK_QUEUE \
    --workflow-type PermissionIncrementWorkflow \
    --workflow-id "${WORKFLOW_ID}" \
    --input '{
        "batchId": "TEST-INCREMENT-DELETE-001",
        "deviceType": "FUJI",
        "operationType": "INCREMENT_DELETE",
        "staffList": [
            {
                "StaffNo": "TEST001",
                "StaffName": "测试人员",
                "Sex": 0,
                "OrganizationId": "C8BBDAA5-BA9E-41F2-BF0C-05522AA4DEC7",
                "Rid": "TEST001"
            }
        ],
        "authorizeDeviceNo": [3],
        "beginTime": "2025-01-01",
        "endTime": "2030-12-31",
        "facePicture": ""
    }'

echo ""
echo "    工作流ID: ${WORKFLOW_ID}"
read -p "    按回车继续..." pause

##############################################################################
# 测试6: 批量权限下发工作流 (BatchPermissionIssueWorkflow)
# 场景：批量并发下发多个人员的门禁权限（20 QPS限流）
# 流程：并发下发(校验->人脸->卡) -> Webhook回调
##############################################################################
echo ""
echo ">>> 测试6: 批量权限下发工作流 (BatchPermissionIssueWorkflow)"
echo "    TaskQueue: BATCH_ISSUE_TASK_QUEUE"

WORKFLOW_ID="test-batch-issue-$(date +%s)"
temporal workflow start \
    --address "${TEMPORAL_ADDRESS}" \
    --namespace "${NAMESPACE}" \
    --task-queue BATCH_ISSUE_TASK_QUEUE \
    --workflow-type BatchPermissionIssueWorkflow \
    --workflow-id "${WORKFLOW_ID}" \
    --input '{
        "batchId": "TEST-BATCH-001",
        "deviceType": "FUJI",
        "operationType": "ISSUE",
        "staffList": [
            {
                "StaffNo": "BATCH001",
                "StaffName": "批量人员1",
                "Sex": 0,
                "OrganizationId": "C8BBDAA5-BA9E-41F2-BF0C-05522AA4DEC7",
                "Rid": "BATCH001"
            },
            {
                "StaffNo": "BATCH002",
                "StaffName": "批量人员2",
                "Sex": 1,
                "OrganizationId": "C8BBDAA5-BA9E-41F2-BF0C-05522AA4DEC7",
                "Rid": "BATCH002"
            },
            {
                "StaffNo": "BATCH003",
                "StaffName": "批量人员3",
                "Sex": 0,
                "OrganizationId": "C8BBDAA5-BA9E-41F2-BF0C-05522AA4DEC7",
                "Rid": "BATCH003"
            }
        ],
        "authorizeDeviceNo": [3, 4, 311],
        "authDevice": ["1", "2"],
        "beginTime": "2025-01-01",
        "endTime": "2030-12-31",
        "facePicture": "",
        "projectGids": ["FF7F139D-B80C-46D0-B330-1118F00255B7"]
    }'

echo ""
echo "    工作流ID: ${WORKFLOW_ID}"
read -p "    按回车继续..." pause

##############################################################################
# 测试7: 访客权限下发工作流 (VisitorPermissionIssueWorkflow)
# 场景：访客门禁权限下发，含涉密过滤和合并下发逻辑
# 流程：校验 -> 涉密过滤 -> 人脸发行 -> 卡发行 -> Webhook回调
##############################################################################
echo ""
echo ">>> 测试7: 访客权限下发工作流 (VisitorPermissionIssueWorkflow)"
echo "    TaskQueue: VISITOR_ISSUE_TASK_QUEUE"

WORKFLOW_ID="test-visitor-issue-$(date +%s)"
temporal workflow start \
    --address "${TEMPORAL_ADDRESS}" \
    --namespace "${NAMESPACE}" \
    --task-queue VISITOR_ISSUE_TASK_QUEUE \
    --workflow-type VisitorPermissionIssueWorkflow \
    --workflow-id "${WORKFLOW_ID}" \
    --input '{
        "batchId": "TEST-VISITOR-001",
        "operationType": "ISSUE",
        "visitorList": [
            {
                "StaffNo": "18928969687",
                "StaffName": "访客张三",
                "Sex": 0,
                "OrganizationId": "C8BBDAA5-BA9E-41F2-BF0C-05522AA4DEC7",
                "Rid": "18928969687"
            }
        ],
        "authorizeDeviceNo": [3, 4, 311],
        "authDevice": ["1", "2"],
        "beginTime": "2025-01-01",
        "endTime": "2025-12-31",
        "facePicture": "",
        "projectGids": ["FF7F139D-B80C-46D0-B330-1118F00255B7"],
        "secretFactoryIds": ["151"],
        "blacklistFactoryIds": [],
        "guardFactoryIds": ["147", "148"],
        "manualIssue": false,
        "visitorCardTcmIdA": "BBB817CE-E6A9-4F9C-8C2D-BDFD2431C053",
        "visitorCardTcmIdB": "AAA717CE-E6A9-4F9C-8C2D-BDFD2431C054",
        "hasIdCardPhoto": false,
        "unregisterGids": [
            "8A0ADE28-0000-0000-0000-000000000001",
            "FF7F139D-0000-0000-0000-000000000002",
            "35EC9884-0000-0000-0000-000000000003"
        ],
        "visitorPhone": "18928969687"
    }'

echo ""
echo "    工作流ID: ${WORKFLOW_ID}"
read -p "    按回车继续..." pause

##############################################################################
# 测试8: 访客权限下发 - 门卫手动下发（包含涉密厂区）
##############################################################################
echo ""
echo ">>> 测试8: 访客权限下发 - 门卫手动下发 (VisitorPermissionIssueWorkflow)"
echo "    TaskQueue: VISITOR_ISSUE_TASK_QUEUE"
echo "    与测试7区别: manualIssue=true, 包含涉密厂区"

WORKFLOW_ID="test-visitor-manual-$(date +%s)"
temporal workflow start \
    --address "${TEMPORAL_ADDRESS}" \
    --namespace "${NAMESPACE}" \
    --task-queue VISITOR_ISSUE_TASK_QUEUE \
    --workflow-type VisitorPermissionIssueWorkflow \
    --workflow-id "${WORKFLOW_ID}" \
    --input '{
        "batchId": "TEST-VISITOR-MANUAL-001",
        "operationType": "ISSUE",
        "visitorList": [
            {
                "StaffNo": "13900000000",
                "StaffName": "访客李四",
                "Sex": 1,
                "OrganizationId": "C8BBDAA5-BA9E-41F2-BF0C-05522AA4DEC7",
                "Rid": "13900000000"
            }
        ],
        "authorizeDeviceNo": [3, 4, 311],
        "authDevice": ["1", "2"],
        "beginTime": "2025-01-01",
        "endTime": "2025-12-31",
        "facePicture": "",
        "projectGids": ["FF7F139D-B80C-46D0-B330-1118F00255B7"],
        "secretFactoryIds": ["151"],
        "blacklistFactoryIds": [],
        "guardFactoryIds": ["147", "151"],
        "manualIssue": true,
        "visitorCardTcmIdA": "BBB817CE-E6A9-4F9C-8C2D-BDFD2431C053",
        "visitorCardTcmIdB": "AAA717CE-E6A9-4F9C-8C2D-BDFD2431C054",
        "hasIdCardPhoto": true,
        "unregisterGids": [
            "8A0ADE28-0000-0000-0000-000000000001",
            "FF7F139D-0000-0000-0000-000000000002",
            "35EC9884-0000-0000-0000-000000000003"
        ],
        "visitorPhone": "13900000000"
    }'

echo ""
echo "    工作流ID: ${WORKFLOW_ID}"

##############################################################################
echo ""
echo "=========================================="
echo "所有测试已启动!"
echo "=========================================="
echo ""
echo "常用查看命令:"
echo "  查看所有工作流:"
echo "    temporal workflow list --address ${TEMPORAL_ADDRESS}"
echo ""
echo "  查看指定工作流详情:"
echo "    temporal workflow describe --address ${TEMPORAL_ADDRESS} --workflow-id <WORKFLOW_ID>"
echo ""
echo "  查看工作流执行历史:"
echo "    temporal workflow show --address ${TEMPORAL_ADDRESS} --workflow-id <WORKFLOW_ID>"
echo ""
echo "  查询工作流结果:"
echo "    temporal workflow query --address ${TEMPORAL_ADDRESS} --workflow-id <WORKFLOW_ID> --query-type __result"
echo ""
echo "  终止工作流:"
echo "    temporal workflow terminate --address ${TEMPORAL_ADDRESS} --workflow-id <WORKFLOW_ID>"
