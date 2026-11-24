package com.hik.worker.workflow.impl;

import com.hik.worker.activity.DeviceSyncActivity;
import com.hik.worker.activity.NotificationActivity;
import com.hik.worker.activity.WebhookActivity;
import com.hik.worker.model.DeviceSyncActivityRequest;
import com.hik.worker.model.DeviceSyncRequest;
import com.hik.worker.model.DeviceSyncResult;
import com.hik.worker.workflow.DeviceSyncWorkflow;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Async;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 设备数据同步工作流实现
 * 从第三方门禁系统全量/增量同步设备数据
 * 流程：拉取首页 → 计算总页数 → 并行拉取 → 合并 → 保存 → 标记移除 → 通知 → Webhook
 */
@Slf4j
public class DeviceSyncWorkflowImpl implements DeviceSyncWorkflow {

    private final DeviceSyncActivity deviceSyncActivity = Workflow.newActivityStub(DeviceSyncActivity.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(60))
                    .setRetryOptions(RetryOptions.newBuilder()
                            .setInitialInterval(Duration.ofSeconds(5))
                            .setMaximumAttempts(3)
                            .setBackoffCoefficient(2.0)
                            .build())
                    .build());

    private final NotificationActivity notificationActivity = Workflow.newActivityStub(NotificationActivity.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(15))
                    .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(5).build())
                    .build());

    private final WebhookActivity webhookActivity = Workflow.newActivityStub(WebhookActivity.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(15))
                    .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(5).build())
                    .build());

    @Override
    public DeviceSyncResult syncDevices(DeviceSyncRequest request) {
        Workflow.getLogger(DeviceSyncWorkflowImpl.class)
                .info("设备同步工作流启动, syncType: {}, deviceType: {}", request.getSyncType(), request.getDeviceType());

        int pageSize = request.getPageSize() > 0 ? request.getPageSize() : 100;
        int parallelPages = request.getParallelPages() > 0 ? request.getParallelPages() : 5;

        // Step 1: 拉取第一页，判断总数
        Workflow.getLogger(DeviceSyncWorkflowImpl.class)
                .info(">>> Step 1: 拉取第一页");
        DeviceSyncActivityRequest firstReq = DeviceSyncActivityRequest.builder()
                .deviceType(request.getDeviceType())
                .pageNo(1)
                .pageSize(pageSize)
                .areaId(request.getAreaId())
                .build();
        List<Map<String, Object>> firstPage = deviceSyncActivity.fetchDevicePage(firstReq);

        List<Map<String, Object>> allDevices = new ArrayList<>(firstPage);
        Workflow.getLogger(DeviceSyncWorkflowImpl.class)
                .info(">>> 第一页设备数: {}", firstPage.size());

        // Step 2 & 3: 计算总页数并并行拉取剩余页
        if (firstPage.size() >= pageSize) {
            Workflow.getLogger(DeviceSyncWorkflowImpl.class)
                    .info(">>> Step 2-3: 并行拉取剩余页, parallelPages: {}", parallelPages);
            int currentPage = 2;
            boolean hasMore = true;
            while (hasMore) {
                List<Promise<List<Map<String, Object>>>> pagePromises = new ArrayList<>();
                for (int i = 0; i < parallelPages; i++) {
                    int pageNo = currentPage + i;
                    DeviceSyncActivityRequest pageReq = DeviceSyncActivityRequest.builder()
                            .deviceType(request.getDeviceType())
                            .pageNo(pageNo)
                            .pageSize(pageSize)
                            .areaId(request.getAreaId())
                            .build();
                    pagePromises.add(Async.function(deviceSyncActivity::fetchDevicePage, pageReq));
                }

                for (Promise<List<Map<String, Object>>> p : pagePromises) {
                    try {
                        List<Map<String, Object>> page = p.get();
                        if (page == null || page.isEmpty()) {
                            hasMore = false;
                            continue;
                        }
                        allDevices.addAll(page);
                        if (page.size() < pageSize) {
                            hasMore = false;
                        }
                    } catch (Exception e) {
                        Workflow.getLogger(DeviceSyncWorkflowImpl.class)
                                .error(">>> 拉取页面失败: {}", e.getMessage());
                        hasMore = false;
                    }
                }
                currentPage += parallelPages;
                // 安全上限
                if (currentPage > 1000) {
                    hasMore = false;
                }
            }
        }

        int totalDevices = allDevices.size();
        Workflow.getLogger(DeviceSyncWorkflowImpl.class)
                .info(">>> 合并完成, 总设备数: {}", totalDevices);

        // 提取设备编码列表
        List<String> deviceCodes = new ArrayList<>();
        for (Map<String, Object> device : allDevices) {
            Object code = device.get("deviceCode");
            if (code != null) {
                deviceCodes.add(code.toString());
            }
        }

        // Step 5: 保存设备数据
        Workflow.getLogger(DeviceSyncWorkflowImpl.class)
                .info(">>> Step 5: 保存设备数据");
        try {
            deviceSyncActivity.saveDevices(allDevices, request.getDeviceType());
        } catch (Exception e) {
            Workflow.getLogger(DeviceSyncWorkflowImpl.class)
                    .error(">>> 保存设备失败: {}", e.getMessage());
            return DeviceSyncResult.builder()
                    .syncType(request.getSyncType())
                    .deviceType(request.getDeviceType())
                    .totalDevices(totalDevices)
                    .failCount(1)
                    .status("FAILED")
                    .errorMessage("保存设备失败: " + e.getMessage())
                    .build();
        }

        // Step 6: 标记已移除设备（仅全量同步）
        int removedDevices = 0;
        if ("FULL".equals(request.getSyncType())) {
            Workflow.getLogger(DeviceSyncWorkflowImpl.class)
                    .info(">>> Step 6: 标记已移除设备");
            try {
                deviceSyncActivity.markRemovedDevices(deviceCodes);
            } catch (Exception e) {
                Workflow.getLogger(DeviceSyncWorkflowImpl.class)
                        .error(">>> 标记移除设备失败: {}", e.getMessage());
            }
        }

        // Step 7: 通知新设备
        Workflow.getLogger(DeviceSyncWorkflowImpl.class)
                .info(">>> Step 7: 通知新设备");
        List<String> newDeviceCodes = new ArrayList<>();
        try {
            deviceSyncActivity.notifyNewDevices(deviceCodes);
            newDeviceCodes = deviceCodes;
        } catch (Exception e) {
            Workflow.getLogger(DeviceSyncWorkflowImpl.class)
                    .error(">>> 通知新设备失败: {}", e.getMessage());
        }
        try {
            String content = String.format("设备同步完成[%s], 总数: %d, 新增: %d",
                    request.getDeviceType(), totalDevices, newDeviceCodes.size());
            notificationActivity.sendNotification("DEVICE_SYNC", "system", content);
        } catch (Exception e) {
            Workflow.getLogger(DeviceSyncWorkflowImpl.class)
                    .error(">>> 发送通知失败: {}", e.getMessage());
        }

        // Step 8: Webhook 回调
        Workflow.getLogger(DeviceSyncWorkflowImpl.class)
                .info(">>> Step 8: Webhook 回调");
        String status = "COMPLETED";
        String payload = String.format(
                "{\"syncType\":\"%s\",\"deviceType\":\"%s\",\"totalDevices\":%d,\"newDevices\":%d,\"status\":\"%s\"}",
                request.getSyncType(), request.getDeviceType(), totalDevices, newDeviceCodes.size(), status);
        try {
            webhookActivity.sendWebhook(payload);
        } catch (Exception e) {
            Workflow.getLogger(DeviceSyncWorkflowImpl.class)
                    .error("Webhook回调发送失败: {}", e.getMessage());
        }

        Workflow.getLogger(DeviceSyncWorkflowImpl.class)
                .info("设备同步工作流完成, 总数: {}", totalDevices);

        return DeviceSyncResult.builder()
                .syncType(request.getSyncType())
                .deviceType(request.getDeviceType())
                .totalDevices(totalDevices)
                .newDevices(newDeviceCodes.size())
                .updatedDevices(0)
                .removedDevices(removedDevices)
                .failCount(0)
                .status(status)
                .newDeviceCodes(newDeviceCodes)
                .build();
    }
}
