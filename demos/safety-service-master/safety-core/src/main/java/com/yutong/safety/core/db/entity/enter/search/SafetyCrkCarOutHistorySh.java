package com.yutong.safety.core.db.entity.enter.search;

import com.yutong.safety.common.param.Page;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class SafetyCrkCarOutHistorySh extends Page implements Serializable {

    @ApiModelProperty("车牌号")
    private String tokenNo;
    @ApiModelProperty("入厂时间-始")
    private String inTimeBegin;
    @ApiModelProperty("入厂时间-终")
    private String inTimeEnd;
    @ApiModelProperty("车辆类型-运输车辆")
    private String tcmName;
    @ApiModelProperty("后台使用-是否管理员")
    private String isAdmin;
}
