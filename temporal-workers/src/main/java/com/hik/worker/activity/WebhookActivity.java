package com.hik.worker.activity;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface WebhookActivity {
    @ActivityMethod
    boolean executeTask(int index);
    @ActivityMethod
    boolean sendWebhook(String payload);
}
