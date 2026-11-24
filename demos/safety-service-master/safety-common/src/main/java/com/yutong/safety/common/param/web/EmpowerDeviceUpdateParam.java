package com.yutong.safety.common.param.web;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import lombok.Data;

import java.util.Objects;

/**
 * create by gwcuiyx on 2023.07.17
 */
@Data
public class EmpowerDeviceUpdateParam implements Check {
    private Integer id;
    private Integer factoryId;
    private String factoryName;
    private String regionName;
    private String empowerType;
    private String empowerNo;
    private Integer regionId;
    private String remark;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(id), ResponseEnum.PARAM_ERROR.getCode(), "访客id不能为空");
    }
}
