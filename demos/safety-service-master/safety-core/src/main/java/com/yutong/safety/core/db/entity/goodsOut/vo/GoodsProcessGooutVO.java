package com.yutong.safety.core.db.entity.goodsOut.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 物资流程中出门人信息
 * @author gwxiexj
 *
 */
@Data
public class GoodsProcessGooutVO implements Serializable {

    @ApiModelProperty("外部人员-出门人姓名")
    private String outsideName;
    @ApiModelProperty("外部人员-出门人电话")
    private String outsidePhone;
    @ApiModelProperty("内部人员-出门人电话")
    private String gooutPhone;
    @ApiModelProperty("内部人员-出门人员工号")
    private String gooutEmpNo;
    @ApiModelProperty("内部人员-出门人部门")
    private String gooutEmpDept;
    @ApiModelProperty("内部人员-出门人姓名")
    private String gooutEmpName;
    @ApiModelProperty("提货单位")
    private String deliveryUnitName;
    @ApiModelProperty("是否车辆运输 是/否")
    private String isVehicle;
    @ApiModelProperty("车牌号")
    private String vehicleNo;
    @ApiModelProperty("创建时间")
    private String createTime;
}
