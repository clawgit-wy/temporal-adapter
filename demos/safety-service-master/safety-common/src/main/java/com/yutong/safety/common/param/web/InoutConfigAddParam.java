package com.yutong.safety.common.param.web;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import lombok.Data;

import java.util.Objects;

/**
 * @author gwcuiyx
 * @date 2024-4-8 16:56
 */
@Data
public class InoutConfigAddParam implements Check {
    /**
     * 员工号
     */
    private String empCode;
    /**
     * 姓名
     */
    private String empName;
    /**
     * 厂区
     */
    private String factoryName;
    /**
     * 厂区id
     */
    private Integer factoryId;
    /**
     * 权限类型
     */
    private String permissionType;
    /**
     * 查询时间
     */
    private Integer searchDay;
    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(empCode), ResponseEnum.PARAM_ERROR.getCode(), "员工号不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(empName), ResponseEnum.PARAM_ERROR.getCode(), "姓名不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(factoryId), ResponseEnum.PARAM_ERROR.getCode(), "厂区不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(permissionType), ResponseEnum.PARAM_ERROR.getCode(), "权限类型不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(searchDay), ResponseEnum.PARAM_ERROR.getCode(), "查询时间不能为空！");
    }
}
