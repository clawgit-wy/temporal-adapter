package com.yutong.safety.core.db.entity.build.param;

import com.yutong.safety.common.param.Check;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BuildProcessUpdateParam implements Check {

    @ApiModelProperty("主键ID")
    private String id;

    @ApiModelProperty("BPM流程ID")
    private String flowId;

    @ApiModelProperty("当前处理人-用户名")
    private String curPersonLogin;

    @ApiModelProperty("当前处理人姓名")
    private String curPersonName;

    @ApiModelProperty("当前处理人员工号")
    private String curPersonNo;

    @ApiModelProperty("流程节点审批意见")
    private String auditNote;

    @ApiModelProperty("申请状态 1-通过 2-不通过")
    private String applyStatus;

    @ApiModelProperty("开通门禁明细ID拼接")
    private String openIdStr;

    @ApiModelProperty("关闭门禁明细ID拼接")
    private String closeIdStr;

    @ApiModelProperty("当前节点")
    private String curNodeId;

    @ApiModelProperty("流程参数")
    private String fdParameterJson;

    @ApiModelProperty("通过门禁明细ID拼接")
    private String passIdStr;

    @ApiModelProperty("不通过门禁明细ID拼接")
    private String failIdStr;

    @Override
    public void check() {
    }
}
