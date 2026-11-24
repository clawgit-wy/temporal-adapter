package com.hik.worker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.List;

/**
 * 访客权限下发请求模型
 * 包含访客特有的业务字段：涉密厂区、黑名单、合并标识等
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitorPermissionRequest implements Serializable {
    /**
     * 批次ID
     */
    private String batchId;
    /**
     * 操作类型: ISSUE=下发, CANCEL=注销
     */
    private String operationType;
    /**
     * 访客列表（同一访客多条记录需合并下发）
     */
    private List<StaffInfo> visitorList;
    /**
     * 授权设备编号列表（人脸设备）
     */
    private List<Integer> authorizeDeviceNo;
    /**
     * 授权设备列表（卡设备）
     */
    private List<String> authDevice;
    /**
     * 生效时间
     */
    private String beginTime;
    /**
     * 失效时间
     */
    private String endTime;
    /**
     * 人脸图片（Base64）
     */
    private String facePicture;
    /**
     * 项目GID列表
     */
    private List<String> projectGids;
    /**
     * 涉密厂区列表（需排除的厂区）
     */
    private List<String> secretFactoryIds;
    /**
     * 黑名单厂区列表（需过滤的厂区）
     */
    private List<String> blacklistFactoryIds;
    /**
     * 门卫权限厂区列表（门卫手动下发时限制范围）
     */
    private List<String> guardFactoryIds;
    /**
     * 是否门卫手动下发
     */
    private boolean manualIssue;
    /**
     * 访客卡类型ID A（无身份证照片时使用）
     */
    private String visitorCardTcmIdA;
    /**
     * 访客卡类型ID B（有身份证照片时使用）
     */
    private String visitorCardTcmIdB;
    /**
     * 是否有身份证照片
     */
    private boolean hasIdCardPhoto;
    /**
     * 注销用的3个固定GID
     */
    private List<String> unregisterGids;
    /**
     * 访客手机号（作为Token使用）
     */
    private String visitorPhone;
}
