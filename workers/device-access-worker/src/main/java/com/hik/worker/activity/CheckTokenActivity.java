package com.hik.worker.activity;

import com.hik.worker.model.CheckTokenRequest;
import com.hik.worker.model.CheckTokenResponse;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

/**
 * 发行校验 Activity 接口
 * 校验卡号/人脸是否已有发行信息
 */
@ActivityInterface
public interface CheckTokenActivity {
    @ActivityMethod
    CheckTokenResponse checkToken(CheckTokenRequest request);
}
