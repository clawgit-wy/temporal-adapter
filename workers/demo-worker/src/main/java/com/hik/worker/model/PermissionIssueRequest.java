package com.hik.worker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.List;

/**
 * 权限下发请求模型
 *
 * 支持以下操作类型:
 * - FACE_ISSUE: 人脸发行 (OperType=0)
 * - FACE_MODIFY: 人脸改写 (OperType=1)
 * - DOOR_ISSUE: 卡发行
 * - POWER_ADD: 增加设备权限 (powerType=0)
 * - POWER_DELETE: 删除设备权限 (powerType=1)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionIssueRequest implements Serializable {
    /** 工作流ID */
    private String workflowId;

    /** 操作类型: FACE_ISSUE / FACE_MODIFY / DOOR_ISSUE / POWER_ADD / POWER_DELETE */
    private String operationType;

    /** 人员信息 */
    private StaffInfo staff;

    /** Token (卡号或人脸标识) */
    private String token;

    /** Token类型: 0=卡, 1=人脸 */
    private int tokenType;

    /** 授权设备编号列表 */
    private List<Integer> authorizeDeviceNo;

    /** 项目GID列表 */
    private List<String> projectGids;

    /** 开始时间 (yyyy-MM-dd) */
    private String beginTime;

    /** 结束时间 (yyyy-MM-dd) */
    private String endTime;

    /** StaffState: 0=不变, 1=新增, 2=修改 */
    private int staffState;

    /** 人脸图片 (base64) */
    private String facePicture;

    /** 卡发行: 使用模式 */
    private int useModel;

    /** 卡发行: 卡类型ID */
    private String tcm;

    /** 卡发行: 子卡类型ID */
    private String stcm;

    /** 卡发行: 车辆通行权限 */
    private String carPass;
}
