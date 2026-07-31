package com.hik.adapter.activity;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

/**
 * 场景1: 限流Activity接口
 * 每次调用模拟一次门禁API请求（50ms）
 */
@ActivityInterface
public interface RateLimitActivity {

    /**
     * 调用模拟API
     * @param index 任务序号
     * @return 是否成功
     */
    @ActivityMethod
    boolean callApi(int index);
}
