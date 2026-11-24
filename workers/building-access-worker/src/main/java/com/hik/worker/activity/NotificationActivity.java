package com.hik.worker.activity;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import java.util.List;

/**
 * 通知 Activity 接口
 * 发送 YxtRebot 消息通知
 */
@ActivityInterface
public interface NotificationActivity {
    @ActivityMethod
    boolean sendNotification(String messageType, String recipient, String content);

    @ActivityMethod
    boolean sendBatchNotification(String messageType, List<String> recipients, String content);
}
