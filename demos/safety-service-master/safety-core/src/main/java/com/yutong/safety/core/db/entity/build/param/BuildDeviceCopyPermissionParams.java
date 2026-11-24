package com.yutong.safety.core.db.entity.build.param;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Objects;

@Data
public class BuildDeviceCopyPermissionParams implements Check {

    @ApiModelProperty("数据行ID")
    private String id;

    @ApiModelProperty("克隆的设备编码")
    private String deviceCode;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(id), ResponseEnum.PARAM_ERROR.getCode(), "数据行ID id不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(deviceCode), ResponseEnum.PARAM_ERROR.getCode(), "克隆的设备编码 deviceCode不能为空！");
    }
}
