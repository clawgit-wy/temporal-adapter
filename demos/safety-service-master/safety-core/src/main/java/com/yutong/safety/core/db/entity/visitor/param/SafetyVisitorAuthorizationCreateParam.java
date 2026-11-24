package com.yutong.safety.core.db.entity.visitor.param;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * 访客审批授权流程
 */
@Data
public class SafetyVisitorAuthorizationCreateParam implements Check {

    @ApiModelProperty("数据ID")
    private Integer id;

    @ApiModelProperty("bpm 流程参数， 审核时必填")
    private String fdParameterJson;

    @ApiModelProperty("流程ID")
    private String bpmFlowId;

    @ApiModelProperty("创建人的EIP账号")
    private String submitEmpLogin;

    @ApiModelProperty("授权人工号")
    private String grantorNo;

    @ApiModelProperty("授权时间-始")
    private String authorizationBeginTime;

    @ApiModelProperty("授权时间-终")
    private String authorizationEndTime;

    @ApiModelProperty("授权原因")
    private String authorizationReason;

    @ApiModelProperty("是否科级管理及以上，0：否 1：是")
    private Integer sectionChiefType;

    @ApiModelProperty("审批人员工号")
    private String applyEmpNo;

    @ApiModelProperty("审批人EIP账号")
    private String applyEmpLogin;

    @ApiModelProperty("审批人名称")
    private String applyEmpName;

    private List<String> detailList;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(grantorNo), ResponseEnum.PARAM_ERROR.getCode(), "授权人员工号 grantorNo 不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(authorizationReason), ResponseEnum.PARAM_ERROR.getCode(), "授权原因 authorizationReason 不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(authorizationBeginTime), ResponseEnum.PARAM_ERROR.getCode(), "授权时间-始 authorizationBeginTime 不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(authorizationEndTime), ResponseEnum.PARAM_ERROR.getCode(), "授权时间-终 authorizationEndTime 不能为空！");
        PreconditonsEx.checkArgument(sectionChiefType != null, ResponseEnum.PARAM_ERROR.getCode(), "是否科级管理及以上 sectionChiefType 不能为空！");
        if (sectionChiefType == 0) {
            PreconditonsEx.checkArgument(StringUtils.isNotBlank(applyEmpNo), ResponseEnum.PARAM_ERROR.getCode(), "请选择授权人科长");
            PreconditonsEx.checkArgument(StringUtils.isNotBlank(applyEmpLogin), ResponseEnum.PARAM_ERROR.getCode(), "请选择授权人科长");
            PreconditonsEx.checkArgument(StringUtils.isNotBlank(applyEmpName), ResponseEnum.PARAM_ERROR.getCode(), "请选择授权人科长");
        }
    }
}
