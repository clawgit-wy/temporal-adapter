package com.yutong.safety.common.param.web;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import lombok.Data;

import java.util.Objects;

/**
 * create by gwcuiyx on 2023.07.10
 */
@Data
public class BlackMessageAddParam implements Check {
    private String companyName;
    private String empName;
    private String cardNo;
    private String phoneNo;
    private String idCardNo;
    private String empType;
    private String carNo;
    private String operator;
    private String positionType;
    private String remark;
    private Integer state;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(companyName), ResponseEnum.PARAM_ERROR.getCode(), "公司companyName不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(empName), ResponseEnum.PARAM_ERROR.getCode(), "姓名empName不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(phoneNo), ResponseEnum.PARAM_ERROR.getCode(), "手机号phoneNo不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(idCardNo), ResponseEnum.PARAM_ERROR.getCode(), "身份证号idCardNo不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(empType), ResponseEnum.PARAM_ERROR.getCode(), "人员类型empType不能为空！");
    }
}
