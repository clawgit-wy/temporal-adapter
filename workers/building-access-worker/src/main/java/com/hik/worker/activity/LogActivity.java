package com.hik.worker.activity;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import java.util.List;
import java.util.Map;

/**
 * 日志管理 Activity 接口
 * 创建/更新下发日志
 */
@ActivityInterface
public interface LogActivity {
    @ActivityMethod
    List<Integer> createIssueLogs(List<Map<String, Object>> logs);

    @ActivityMethod
    boolean updateLogStatus(int logId, String status, String remark);

    @ActivityMethod
    boolean batchUpdateLogStatus(List<Integer> logIds, String status);
}
