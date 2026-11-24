package com.yutong.modules.vehicle.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
public class CarInfoVO {

    @ApiModelProperty(value = "车牌号")
    private String carNo;

    @ApiModelProperty(value = "车辆品牌")
    private String carModel;

    @ApiModelProperty(value = "车辆颜色")
    private String carColor;

    @ApiModelProperty(value = "车辆类型")
    private String vehicleType;

    @ExcelProperty(value = "车辆状态 是否锁定 0-未锁定:代表正常 1-锁定")
    private String lockFlag;

}
