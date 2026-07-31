package com.hik.worker.activity;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface CrashRecoveryActivity {
    @ActivityMethod
    String executeStep(String stepName);
}
