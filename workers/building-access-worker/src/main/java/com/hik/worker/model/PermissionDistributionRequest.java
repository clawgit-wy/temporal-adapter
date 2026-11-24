package com.hik.worker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.List;

/**
 * 权限下发请求模型（大规模权限下发工作流）
 * 对应 BPM 审批通过后的批量权限下发场景
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionDistributionRequest implements Serializable {
    /** 流程ID */
    private String processId;
    /** BPM流程实例ID */
    private String bpmFlowId;
    /** 批次ID */
    private String batchId;
    /** 设备类型: FUJI=富士, XINKAIPU=新开普 */
    private String deviceType;
    /** 操作类型: ISSUE=开通权限, CANCEL=关闭权限 */
    private String operationType;
    /** 人员列表 */
    private List<StaffInfo> staffList;
    /** 授权设备编号列表（人脸设备） */
    private List<Integer> authorizeDeviceNo;
    /** 授权设备列表（卡设备） */
    private List<String> authDevice;
    /** 生效时间 */
    private String beginTime;
    /** 失效时间 */
    private String endTime;
    /** 人脸图片（Base64） */
    private String facePicture;
    /** 项目GID列表 */
    private List<String> projectGids;
    /** 最大并发数 */
    private int maxConcurrency;
    /** 是否白名单人员 */
    private boolean whitelist;
    /** 责任部门编码 */
    private String dutyDeptCode;
    /** 保密负责人员工号 */
    private String dutyEmpCode;
}
