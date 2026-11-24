package com.hik.worker.activity;

import com.hik.worker.model.PermissionIssueRequest;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

/**
 * 人员权限下发 Activity 接口
 *
 * 对应 mokapi 模拟的 5 个海康 FaceHandSet 接口:
 * 1. checkTokenState       - 发行校验
 * 2. faceIssue             - 人脸发行
 * 3. faceModify            - 人脸改写
 * 4. doorIssue             - 卡发行
 * 5. faceModifyBypowerType - 增量权限/删除
 */
@ActivityInterface
public interface PermissionActivity {

    /**
     * 发行校验 - 检查卡号/人脸是否已存在
     * POST {mokapi.url}/Api/FaceHandSet/CheckTokenState
     *
     * @return mokapi 返回的 JSON 字符串
     */
    @ActivityMethod
    String checkTokenState(PermissionIssueRequest request);

    /**
     * 人脸发行
     * POST {mokapi.url}/Api/FaceHandSet/FaceIssue
     *
     * @return mokapi 返回的 JSON 字符串
     */
    @ActivityMethod
    String faceIssue(PermissionIssueRequest request);

    /**
     * 人脸改写
     * POST {mokapi.url}/Api/FaceHandSet/FaceModify
     *
     * @return mokapi 返回的 JSON 字符串
     */
    @ActivityMethod
    String faceModify(PermissionIssueRequest request);

    /**
     * 卡发行
     * POST {mokapi.url}/Api/FaceHandSet/DoorIssue
     *
     * @return mokapi 返回的 JSON 字符串
     */
    @ActivityMethod
    String doorIssue(PermissionIssueRequest request);

    /**
     * 增量权限/删除
     * POST {mokapi.url}/Api/FaceHandSet/FaceModifyBypowerType
     *
     * @param powerType 0=增加权限, 1=删除权限
     * @return mokapi 返回的 JSON 字符串
     */
    @ActivityMethod
    String faceModifyBypowerType(PermissionIssueRequest request, int powerType);
}
