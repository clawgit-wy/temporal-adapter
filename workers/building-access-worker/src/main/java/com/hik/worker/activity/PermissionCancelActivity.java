package com.hik.worker.activity;

import com.hik.worker.model.UnregisterRequest;
import com.hik.worker.model.UnregisterResponse;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import java.util.List;

/**
 * 权限注销 Activity 接口
 * 对3个固定GID分别注销人脸和卡权限
 */
@ActivityInterface
public interface PermissionCancelActivity {
    /**
     * 注销人脸权限（对多个GID逐一注销）
     */
    @ActivityMethod
    boolean unregisterFace(List<UnregisterRequest> requests);

    /**
     * 注销卡权限（对多个GID逐一注销）
     */
    @ActivityMethod
    boolean unregisterCard(List<UnregisterRequest> requests);

    /**
     * 根据手机号查询卡号
     */
    @ActivityMethod
    String queryCardNo(String phone);
}
