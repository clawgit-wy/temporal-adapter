package com.hik.adapter.activity;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

/**
 * 场景2: 重试Activity接口
 * 调用mokapi的ERROR端点（前3次失败，第4次成功）
 */
@ActivityInterface
public interface RetryActivity {

    /**
     * 带重试的API调用
     * @param index 任务序号
     * @return 是否成功
     */
    @ActivityMethod
    boolean callWithRetry(int index);
}
