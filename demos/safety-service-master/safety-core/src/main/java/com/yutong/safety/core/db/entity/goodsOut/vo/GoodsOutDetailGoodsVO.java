package com.yutong.safety.core.db.entity.goodsOut.vo;

import com.yutong.safety.core.db.entity.goodsOut.SafetyGoodsOutDetailGoodsDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GoodsOutDetailGoodsVO extends SafetyGoodsOutDetailGoodsDO {

    private String flowId;
    @ApiModelProperty("流程编号")
    private String flowNo;
    @ApiModelProperty("主题")
    private String subject;
    @ApiModelProperty("出门类型")
    private String outTypeName;
    @ApiModelProperty("提交时间")
    private String createTimeStr;
    @ApiModelProperty("出厂人")
    private String gooutEmpName;
    @ApiModelProperty("出厂厂区")
    private String outFactoryName;
    @ApiModelProperty("审核人")
    private String examineEmpName;
    @ApiModelProperty("流程状态")
    private String status;
    @ApiModelProperty("车工号")
    private String vehicleNo;
    @ApiModelProperty("VIN号")
    private String vinNo;
    @ApiModelProperty("是否异常")
    private String isHistoryExce;
    @ApiModelProperty("车牌号")
    private String processVehicelNo;
}
