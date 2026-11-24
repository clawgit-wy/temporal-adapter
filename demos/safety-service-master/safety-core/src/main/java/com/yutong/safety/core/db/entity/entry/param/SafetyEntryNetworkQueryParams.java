package com.yutong.safety.core.db.entity.entry.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SafetyEntryNetworkQueryParams {


    @ApiModelProperty("姓名")
    private String name;

    @ApiModelProperty("身份证号")
    private String cardNo;

    @ApiModelProperty("联系电话")
    private String phone;

    @ApiModelProperty("状态 10-驳回 20-审批中 25-超时未审批 30-审批通过")
    private String status;

    @ApiModelProperty("所属公司")
    private String companyName;

    @ApiModelProperty("准入厂区：接收准入厂区编码")
    private String enterFactoryCode;

    @ApiModelProperty("准入时间-开始")
    private String enterStartTime;

    @ApiModelProperty("准入时间-截止")
    private String enterEndTime;

    @ApiModelProperty("备注")
    private String remark;

}
