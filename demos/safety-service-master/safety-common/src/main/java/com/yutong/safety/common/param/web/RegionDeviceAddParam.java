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
public class RegionDeviceAddParam implements Check {
    private Integer factoryId;
    private String factoryName;
    private String regionName;
    private Integer regionId;
    private Integer deviceType;
    private String deviceNo;
    private String deviceName;
    private String remark;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(factoryId), ResponseEnum.PARAM_ERROR.getCode(), "厂区id不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(factoryName), ResponseEnum.PARAM_ERROR.getCode(), "厂区不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(regionId), ResponseEnum.PARAM_ERROR.getCode(), "区域id不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(regionName), ResponseEnum.PARAM_ERROR.getCode(), "区域不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(deviceType), ResponseEnum.PARAM_ERROR.getCode(), "设备类型不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(deviceNo), ResponseEnum.PARAM_ERROR.getCode(), "设备号不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(deviceName), ResponseEnum.PARAM_ERROR.getCode(), "设备名称不能为空！");
    }
}
