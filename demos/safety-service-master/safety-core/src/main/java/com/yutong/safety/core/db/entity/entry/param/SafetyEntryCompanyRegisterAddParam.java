package com.yutong.safety.core.db.entity.entry.param;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.Objects;

@Data
public class SafetyEntryCompanyRegisterAddParam implements Check {

    @ApiModelProperty("Id")
    private Integer id;

    @ApiModelProperty("姓名")
    private String fdName;

    @ApiModelProperty("手机号")
    private String fdPhone;

    @ApiModelProperty("公司代码")
    private String fdCompanyCode;

    @ApiModelProperty("对接人姓名")
    private String buttJointName;

    @ApiModelProperty("对接人手机号")
    private String buttJointPhone;

    @ApiModelProperty("事由")
    private String reason;

    @ApiModelProperty("验证码")
    private String code;

    @ApiModelProperty("状态： 10-驳回, 20-待审批, 30-通过")
    private String status;

    @Override
    public void check() {
        if (id == null) {
            PreconditonsEx.checkArgument(Objects.nonNull(fdName), ResponseEnum.PARAM_ERROR.getCode(), "姓名 fdName 不能为空！");
            PreconditonsEx.checkArgument(Objects.nonNull(fdPhone), ResponseEnum.PARAM_ERROR.getCode(), "手机号 fdPhone 不能为空！");
            PreconditonsEx.checkArgument(Objects.nonNull(fdCompanyCode), ResponseEnum.PARAM_ERROR.getCode(), "公司代码 fdCompanyCode 不能为空！");
            PreconditonsEx.checkArgument(Objects.nonNull(buttJointName), ResponseEnum.PARAM_ERROR.getCode(), "对接人姓名 buttJointName 不能为空！");
            PreconditonsEx.checkArgument(Objects.nonNull(buttJointPhone), ResponseEnum.PARAM_ERROR.getCode(), "对接人手机号 buttJointPhone 不能为空！");
            PreconditonsEx.checkArgument(Objects.nonNull(reason), ResponseEnum.PARAM_ERROR.getCode(), "事由 reason 不能为空！");
            PreconditonsEx.checkArgument(Objects.nonNull(code), ResponseEnum.PARAM_ERROR.getCode(), "短信验证码 code 不能为空！");
        } else {
            PreconditonsEx.checkArgument(Objects.nonNull(status), ResponseEnum.PARAM_ERROR.getCode(), "状态 status 不能为空！");
            PreconditonsEx.checkArgument(Objects.nonNull(fdPhone), ResponseEnum.PARAM_ERROR.getCode(), "手机号 fdPhone 不能为空！");
        }
    }
}
