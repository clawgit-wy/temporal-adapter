package com.hik.worker.activity;

import com.hik.worker.model.DemoRequest;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

/**
 * Demo Activity 接口
 */
@ActivityInterface
public interface DemoActivity {
    /**
     * 调用 mokapi 模拟海康校验接口
     * POST {mokapi.url}/api/hikvision/verify
     */
    @ActivityMethod
    String callMockApi(DemoRequest request);

    /**
     * 模拟数据处理 (带 sleep)
     */
    @ActivityMethod
    int processData(DemoRequest request);

    /**
     * 生成汇总报告
     */
    @ActivityMethod
    String generateReport(DemoRequest request);
}
