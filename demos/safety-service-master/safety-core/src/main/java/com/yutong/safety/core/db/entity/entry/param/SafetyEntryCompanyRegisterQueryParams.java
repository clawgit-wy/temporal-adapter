package com.yutong.safety.core.db.entity.entry.param;

import com.yutong.safety.common.param.web.SafetyWebBaseParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SafetyEntryCompanyRegisterQueryParams extends SafetyWebBaseParam {

    @ApiModelProperty("姓名")
    private String fdName;

    @ApiModelProperty("手机号")
    private String fdPhone;

    @ApiModelProperty("公司名称")
    private String fdCompany;

    @ApiModelProperty("对接人: 对接人名称或对接人工号")
    private String buttJoint;

    @ApiModelProperty("状态")
    private String status;
}
