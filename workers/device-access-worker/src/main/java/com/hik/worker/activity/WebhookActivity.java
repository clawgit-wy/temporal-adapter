package com.hik.worker.activity;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

/**
 * Webhook 回调 Activity 接口
 */
@ActivityInterface
public interface WebhookActivity {
    @ActivityMethod
    boolean sendWebhook(String payload);
}
