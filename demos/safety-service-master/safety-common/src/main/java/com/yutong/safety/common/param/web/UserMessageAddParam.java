package com.yutong.safety.common.param.web;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import lombok.Data;

import java.util.Objects;

/**
 * create by gwcuiyx on 2023.07.14
 */
@Data
public class UserMessageAddParam implements Check {
    private String userName;
    private String empName;
    private String empNo;
    private String role;
    private String roleId;
    private String modifyEmp;
    private String modifyEmpNo;
    private String factoryId;
    private String factoryName;
    private String powerFactoryId;
    private String powerFactoryName;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(empName), ResponseEnum.PARAM_ERROR.getCode(), "userName不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(empName), ResponseEnum.PARAM_ERROR.getCode(), "empName不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(empNo), ResponseEnum.PARAM_ERROR.getCode(), "empNo不能为空！");
//        PreconditonsEx.checkArgument(Objects.nonNull(role), ResponseEnum.PARAM_ERROR.getCode(), "role不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(roleId), ResponseEnum.PARAM_ERROR.getCode(), "roleId不能为空！");
//        PreconditonsEx.checkArgument(Objects.nonNull(modifyEmp), ResponseEnum.PARAM_ERROR.getCode(), "modifyEmp不能为空！");
//        PreconditonsEx.checkArgument(Objects.nonNull(modifyEmpNo), ResponseEnum.PARAM_ERROR.getCode(), "modifyEmpNo不能为空！");
    }
}
