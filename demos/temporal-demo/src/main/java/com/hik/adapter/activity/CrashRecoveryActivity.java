package com.hik.adapter.activity;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

/**
 * 场景3: 断点续传Activity接口
 * 每个步骤模拟一个业务操作
 */
@ActivityInterface
public interface CrashRecoveryActivity {

    /**
     * 执行一个步骤
     * @param stepName 步骤名称
     * @return 执行结果描述
     */
    @ActivityMethod
    String executeStep(String stepName);
}
