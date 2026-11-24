package com.yutong.safety.core.db.entity.device.param;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Objects;

@Data
public class SafetyRegionDeviceInfoUpdateParams implements Check {

    @ApiModelProperty("数据行ID")
    private Integer id;

    @ApiModelProperty("设备类型：1 大门设备； 2 楼宇设备")
    private String deviceCategory;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(id), ResponseEnum.PARAM_ERROR.getCode(), "数据行ID id不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(deviceCategory), ResponseEnum.PARAM_ERROR.getCode(), "设备类型 deviceCategory不能为空！");
        PreconditonsEx.checkArgumentNull(!deviceCategory.equals("1") && !deviceCategory.equals("2"), ResponseEnum.PARAM_ERROR.getCode(), "设备类型 deviceCategory格式不正确！");
    }
}
