package com.yutong.safety.core.db.entity.hik.param;

import com.yutong.safety.common.param.web.SafetyWebBaseParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SafetyHikAbnormalEnterQueryParam extends SafetyWebBaseParam {

    @ApiModelProperty("姓名")
    private String name;

    @ApiModelProperty("员工号/手机号")
    private String empNo;

    @ApiModelProperty("所属厂区")
    private String factoryCode;

    @ApiModelProperty("所属楼栋")
    private String buildCode;

    @ApiModelProperty("抓拍时间-开始")
    private String beginEventTime;

    @ApiModelProperty("抓拍时间-截止")
    private String endEventTime;

    @ApiModelProperty("设备编号")
    private String cameraIndexcode;

    @ApiModelProperty("组织编码")
    private String orgCode;
}
