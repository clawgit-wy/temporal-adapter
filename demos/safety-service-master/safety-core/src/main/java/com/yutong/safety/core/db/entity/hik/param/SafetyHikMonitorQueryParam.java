package com.yutong.safety.core.db.entity.hik.param;

import com.yutong.safety.common.param.web.SafetyWebBaseParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SafetyHikMonitorQueryParam extends SafetyWebBaseParam {

    @ApiModelProperty("海浪平台唯一标识")
    private String indexCode;

    @ApiModelProperty("设备名称")
    private String name;

    @ApiModelProperty("所属厂区")
    private String factoryCode;

    @ApiModelProperty("所属楼栋")
    private String buildCode;

    @ApiModelProperty("最近提交人")
    private String updateBy;

}
