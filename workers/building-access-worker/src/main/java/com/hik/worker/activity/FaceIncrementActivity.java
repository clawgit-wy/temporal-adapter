package com.hik.worker.activity;

import com.hik.worker.model.FaceIncrementRequest;
import com.hik.worker.model.FaceIssueResponse;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

/**
 * 人脸增量权限 Activity 接口
 * powerType: 0=增加, 1=删除
 */
@ActivityInterface
public interface FaceIncrementActivity {
    @ActivityMethod
    FaceIssueResponse modifyByPowerType(FaceIncrementRequest request);
}
