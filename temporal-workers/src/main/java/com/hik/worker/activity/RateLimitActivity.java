package com.hik.worker.activity;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface RateLimitActivity {
    @ActivityMethod
    boolean callApi(int index);
}
