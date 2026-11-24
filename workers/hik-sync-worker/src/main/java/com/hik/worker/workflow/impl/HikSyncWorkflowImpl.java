package com.hik.worker.workflow.impl;

import com.hik.worker.activity.HikSyncActivity;
import com.hik.worker.model.HkMiddleTableVO;
import com.hik.worker.model.HkSyncRequest;
import com.hik.worker.model.HkSyncResult;
import com.hik.worker.workflow.HikSyncWorkflow;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Async;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 海康数据同步工作流实现
 * 增量同步安保系统人员/人脸数据到海康中间库
 * 流程：计算时间窗口 → 并行查询4数据源 → 合并 → 处理人脸图片(过滤+URL替换+Base64) → 去重 → 批量写入
 */
@Slf4j
public class HikSyncWorkflowImpl implements HikSyncWorkflow {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** 查询类 Activity: 30s超时, 3次重试, 2s退避 */
    private final HikSyncActivity queryActivity = Workflow.newActivityStub(HikSyncActivity.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(30))
                    .setRetryOptions(RetryOptions.newBuilder()
                            .setInitialInterval(Duration.ofSeconds(2))
                            .setMaximumAttempts(3)
                            .setBackoffCoefficient(2.0)
                            .build())
                    .build());

    /** 图片处理 Activity: 120s超时, 2次重试 */
    private final HikSyncActivity imageActivity = Workflow.newActivityStub(HikSyncActivity.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(120))
                    .setRetryOptions(RetryOptions.newBuilder()
                            .setInitialInterval(Duration.ofSeconds(5))
                            .setMaximumAttempts(2)
                            .setBackoffCoefficient(2.0)
                            .build())
                    .build());

    /** 批量写入 Activity: 60s超时, 3次重试, 5s退避 */
    private final HikSyncActivity syncActivity = Workflow.newActivityStub(HikSyncActivity.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(60))
                    .setRetryOptions(RetryOptions.newBuilder()
                            .setInitialInterval(Duration.ofSeconds(5))
                            .setMaximumAttempts(3)
                            .setBackoffCoefficient(2.0)
                            .build())
                    .build());

    /** 同步进度（通过 QueryMethod 查询） */
    private String syncProgress = "INIT";

    @Override
    public HkSyncResult syncToHik(HkSyncRequest request) {
        Workflow.getLogger(HikSyncWorkflowImpl.class)
                .info("海康数据同步工作流启动, syncType: {}", request.getSyncType());

        String syncType = StringUtils.isNotBlank(request.getSyncType()) ? request.getSyncType() : "INCREMENTAL";
        int batchSize = request.getBatchSize() > 0 ? request.getBatchSize() : 900;

        // Step 1: 计算时间窗口
        syncProgress = "CALCULATING_TIME_WINDOW";
        String startTime;
        String endTime;
        if (StringUtils.isNotBlank(request.getCustomStartTime())
                && StringUtils.isNotBlank(request.getCustomEndTime())) {
            startTime = request.getCustomStartTime();
            endTime = request.getCustomEndTime();
        } else {
            int minutes = request.getTimeWindowMinutes() > 0 ? request.getTimeWindowMinutes() : 123;
            long nowMs = Workflow.currentTimeMillis();
            LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochMilli(nowMs), ZoneId.systemDefault());
            LocalDateTime start = end.minusMinutes(minutes);
            startTime = start.format(FORMATTER);
            endTime = end.format(FORMATTER);
        }
        Workflow.getLogger(HikSyncWorkflowImpl.class)
                .info(">>> Step 1: 时间窗口 {} ~ {}", startTime, endTime);

        // Step 2: 并行查询4个数据源
        syncProgress = "QUERYING_DATA_SOURCES";
        Workflow.getLogger(HikSyncWorkflowImpl.class).info(">>> Step 2: 并行查询4个数据源");
        Promise<List<HkMiddleTableVO>> blackPromise = Async.function(queryActivity::queryBlackMessageList, startTime, endTime);
        Promise<List<HkMiddleTableVO>> employeePromise = Async.function(queryActivity::queryEmployeeMessageList, startTime, endTime);
        Promise<List<HkMiddleTableVO>> registerPromise = Async.function(queryActivity::queryEnterRegisterList, startTime, endTime);
        Promise<List<HkMiddleTableVO>> visitorPromise = Async.function(queryActivity::queryVisitorHairpinList, startTime, endTime);

        List<HkMiddleTableVO> blackList = blackPromise.get();
        List<HkMiddleTableVO> employeeList = employeePromise.get();
        List<HkMiddleTableVO> registerList = registerPromise.get();
        List<HkMiddleTableVO> visitorList = visitorPromise.get();

        int blackCount = blackList != null ? blackList.size() : 0;
        int employeeCount = employeeList != null ? employeeList.size() : 0;
        int registerCount = registerList != null ? registerList.size() : 0;
        int visitorCount = visitorList != null ? visitorList.size() : 0;
        Workflow.getLogger(HikSyncWorkflowImpl.class)
                .info(">>> 查询结果: 黑名单={}, 员工={}, 备案={}, 访客={}", blackCount, employeeCount, registerCount, visitorCount);

        // Step 3: 合并所有结果
        syncProgress = "MERGING_DATA";
        List<HkMiddleTableVO> allList = new ArrayList<>();
        if (blackList != null) allList.addAll(blackList);
        if (employeeList != null) allList.addAll(employeeList);
        if (registerList != null) allList.addAll(registerList);
        if (visitorList != null) allList.addAll(visitorList);
        int totalAfterMerge = allList.size();
        Workflow.getLogger(HikSyncWorkflowImpl.class).info(">>> Step 3: 合并完成, 总数: {}", totalAfterMerge);

        if (totalAfterMerge == 0) {
            syncProgress = "COMPLETED_NO_DATA";
            Workflow.getLogger(HikSyncWorkflowImpl.class).info(">>> 无增量数据, 工作流结束");
            return HkSyncResult.builder()
                    .syncType(syncType)
                    .blackListCount(blackCount)
                    .employeeCount(employeeCount)
                    .registerCount(registerCount)
                    .visitorCount(visitorCount)
                    .totalAfterFilter(0)
                    .totalAfterDedup(0)
                    .successCount(0)
                    .failCount(0)
                    .status("COMPLETED")
                    .errorMessage("无增量数据")
                    .build();
        }

        // Step 4 & 5: 处理人脸图片（过滤 + URL替换 + Base64） + 去重
        syncProgress = "PROCESSING_FACE_IMAGES";
        Workflow.getLogger(HikSyncWorkflowImpl.class)
                .info(">>> Step 4-5: 处理人脸图片 + 去重");
        List<HkMiddleTableVO> filteredList;
        try {
            filteredList = imageActivity.processFaceImages(allList);
        } catch (Exception e) {
            Workflow.getLogger(HikSyncWorkflowImpl.class)
                    .error(">>> 处理人脸图片失败: {}", e.getMessage());
            syncProgress = "FAILED_PROCESS_IMAGES";
            return HkSyncResult.builder()
                    .syncType(syncType)
                    .blackListCount(blackCount)
                    .employeeCount(employeeCount)
                    .registerCount(registerCount)
                    .visitorCount(visitorCount)
                    .totalAfterFilter(0)
                    .totalAfterDedup(0)
                    .successCount(0)
                    .failCount(1)
                    .status("FAILED")
                    .errorMessage("处理人脸图片失败: " + e.getMessage())
                    .build();
        }
        int totalAfterFilter = totalAfterMerge - filteredList.size();
        int totalAfterDedup = filteredList.size();
        Workflow.getLogger(HikSyncWorkflowImpl.class)
                .info(">>> 过滤后数量: {}, 去重后数量: {}", totalAfterFilter, totalAfterDedup);

        if (totalAfterDedup == 0) {
            syncProgress = "COMPLETED_NO_VALID_DATA";
            Workflow.getLogger(HikSyncWorkflowImpl.class).info(">>> 过滤后无有效数据, 工作流结束");
            return HkSyncResult.builder()
                    .syncType(syncType)
                    .blackListCount(blackCount)
                    .employeeCount(employeeCount)
                    .registerCount(registerCount)
                    .visitorCount(visitorCount)
                    .totalAfterFilter(totalAfterFilter)
                    .totalAfterDedup(0)
                    .successCount(0)
                    .failCount(0)
                    .status("COMPLETED")
                    .errorMessage("过滤后无有效数据")
                    .build();
        }

        // Step 6: 批量写入海康中间库
        syncProgress = "SYNCING_TO_HIK";
        Workflow.getLogger(HikSyncWorkflowImpl.class)
                .info(">>> Step 6: 批量写入海康中间库, batchSize: {}", batchSize);
        int successCount = 0;
        String status = "COMPLETED";
        String errorMessage = null;
        try {
            successCount = syncActivity.batchSyncToHik(filteredList, batchSize);
            Workflow.getLogger(HikSyncWorkflowImpl.class)
                    .info(">>> 写入完成, 成功数: {}", successCount);
        } catch (Exception e) {
            Workflow.getLogger(HikSyncWorkflowImpl.class)
                    .error(">>> 批量写入海康中间库失败: {}", e.getMessage());
            status = "FAILED";
            errorMessage = "批量写入海康中间库失败: " + e.getMessage();
        }

        // Step 7: 返回结果
        syncProgress = status.equals("COMPLETED") ? "COMPLETED" : "FAILED";
        Workflow.getLogger(HikSyncWorkflowImpl.class)
                .info("海康数据同步工作流完成, status: {}, successCount: {}", status, successCount);

        return HkSyncResult.builder()
                .syncType(syncType)
                .blackListCount(blackCount)
                .employeeCount(employeeCount)
                .registerCount(registerCount)
                .visitorCount(visitorCount)
                .totalAfterFilter(totalAfterFilter)
                .totalAfterDedup(totalAfterDedup)
                .successCount(successCount)
                .failCount(status.equals("COMPLETED") ? 0 : 1)
                .status(status)
                .errorMessage(errorMessage)
                .build();
    }

    @Override
    public String getSyncProgress() {
        return syncProgress;
    }
}
