package com.yutong.safety.core.db.entity.build.vo;

import com.yutong.safety.core.db.entity.build.SafetyBuildProcessDOWithBLOBs;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 楼宇门禁-员工权限调整列表返回VO
 * @author gwxiexj
 */
@Data
public class BuildProcessListVO extends SafetyBuildProcessDOWithBLOBs {
    @ApiModelProperty("申请门禁-拼接")
    private String montageDeviceNameStr;
    @ApiModelProperty("楼栋-拼接")
    private String montageBuildNameStr;
    @ApiModelProperty("授权期限-拼接")
    private String montageTimeStr;
    @ApiModelProperty("厂区-拼接")
    private String montageFactoryNameStr;
    @ApiModelProperty("责任部门-拼接")
    private String montageDeptStr;
    @ApiModelProperty("当前责任人")
    private String curDutyPerson;
    @ApiModelProperty("流程详情")
    private String flowUrl;
    @ApiModelProperty("角色类型-描述")
    private String roleTypeStr;
    @ApiModelProperty("状态-描述")
    private String statusStr;
}
