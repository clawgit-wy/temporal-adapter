package com.yutong.modules.risk.web;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author gwxiexj
 * @since 2024-06-11 11:20
 */
@Data
public class ValidateEnvironmentListParam extends WebPageParam {

    @ApiModelProperty("事件编号")
    private String flowNo;
    @ApiModelProperty("工厂ID")
    private Long factoryId;
    @ApiModelProperty("位置")
    private String location;
    @ApiModelProperty("车牌号")
    private String carNumber;
    @ApiModelProperty("驾驶员姓名")
    private String driverName;
    @ApiModelProperty("排放标准")
    private String emissionStandard;
    @ApiModelProperty("是否加入黑名单")
    private Integer isBlacklist;
    @ApiModelProperty("抽查人")
    private String checkPerson;
    @ApiModelProperty("抽查时间-开始")
    private String checkTimeBegin;
    @ApiModelProperty("抽查时间-截止")
    private String checkTimeEnd;

    @ApiModelProperty("工厂ID拼接-后台参数")
    private String factoryIdStr;
    private String flag;
}
