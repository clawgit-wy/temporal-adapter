package com.yutong.modules.risk.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class YTSafetyGoodsDetails {
    @ApiModelProperty("出门证id")
    private String goodsOutProcessId;
    @ApiModelProperty("物资id")
    private Integer goodsId;
    @ApiModelProperty("物资编号")
    private String goodsCode;
    @ApiModelProperty("物资名称")
    private String goodsName;
    @ApiModelProperty("物资单位")
    private String goodsUnit;
    @ApiModelProperty("物资数量")
    private Integer goodsNum;
    @ApiModelProperty("物资备注")
    private String goodsRemark;


    @ApiModelProperty("车辆详情id")
    private Integer vehicleId;
    @ApiModelProperty("车工号")
    private String vehicleNo;
    @ApiModelProperty("车辆VIN")
    private String vinNo;
    @ApiModelProperty("车辆备注")
    private String vehicleRemark;

    @ApiModelProperty("是否验证通过")
    private String validAccess;
}
