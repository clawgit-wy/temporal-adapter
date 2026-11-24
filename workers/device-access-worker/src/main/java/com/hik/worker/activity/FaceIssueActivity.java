package com.hik.worker.activity;

import com.hik.worker.model.FaceIssueRequest;
import com.hik.worker.model.FaceIssueResponse;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

/**
 * 人脸发行 Activity 接口
 */
@ActivityInterface
public interface FaceIssueActivity {
    @ActivityMethod
    FaceIssueResponse issueFace(FaceIssueRequest request);
}
