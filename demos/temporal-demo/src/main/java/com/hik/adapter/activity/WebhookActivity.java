package com.hik.adapter.activity;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

/**
 * 场景5: Webhook回调Activity接口
 * 包含批量任务执行和Webhook发送两个方法
 */
@ActivityInterface
public interface WebhookActivity {

    /**
     * 执行单个任务（调用mokapi）
     * @param index 任务序号
     * @return 是否成功
     */
    @ActivityMethod
    boolean executeTask(int index);

    /**
     * 发送Webhook回调通知
     * 如果目标服务不可用会抛出异常，触发Temporal自动重试
     * @param payload JSON格式的回调数据
     * @return 是否发送成功
     */
    @ActivityMethod
    boolean sendWebhook(String payload);
}
