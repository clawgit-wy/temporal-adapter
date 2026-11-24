package com.yutong.safety.core.db.entity.enter.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class SatefyEnterStatisticRt implements Serializable {

    @ApiModelProperty("备案流程数量")
    private int processNum;

    @ApiModelProperty("已入厂车辆数量")
    private int enterCarNum;

    @ApiModelProperty("车辆进厂次数")
    private int enterTotalNum;
}
