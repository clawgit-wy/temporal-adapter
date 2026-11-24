package com.yutong.safety.core.db.entity.goodsOut.param;

import com.yutong.safety.common.param.web.WebPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class GoodsOutDetailListParam extends WebPageParam {

    @ApiModelProperty("流程编号")
    private String flowNo;
    @ApiModelProperty("主题")
    private String subject;
    @ApiModelProperty("出门类型")
    private String outTypeName;
    @ApiModelProperty("车工号")
    private String vehicleNo;
    @ApiModelProperty("VIN号")
    private String vinNo;
    @ApiModelProperty("提交时间-起")
    private String createTimeBegin;
    @ApiModelProperty("提交时间-止")
    private String createTimeEnd;
    @ApiModelProperty("出厂人")
    private String gooutEmpName;
    @ApiModelProperty("出厂厂区")
    private String outFactory;
    @ApiModelProperty("出厂厂区ID")
    private String outFactoryId;
    @ApiModelProperty("审核人")
    private String examineEmpName;
    @ApiModelProperty("流程状态")
    private String status;
    @ApiModelProperty("物资编码")
    private String goodsCode;

    private Integer begin;
    @ApiModelProperty("出厂ID集合")
    private String outFactoryIds;

    private String isHistoryExce;
    @ApiModelProperty("当前用户登录名")
    private String curPersonLogin;

    private List<String> processIdList;
}
