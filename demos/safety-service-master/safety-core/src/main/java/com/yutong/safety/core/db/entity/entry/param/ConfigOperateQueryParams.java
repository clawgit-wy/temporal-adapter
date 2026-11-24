package com.yutong.safety.core.db.entity.entry.param;

import com.yutong.safety.common.param.web.SafetyWebBaseParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ConfigOperateQueryParams extends SafetyWebBaseParam {
    @ApiModelProperty("对接人")
    private String buttJoint;

    @ApiModelProperty("备案类型：接收编码值")
    private String recordTypeCode;

    @ApiModelProperty("准入厂区：接收准入厂区编码")
    private String enterFactoryCode;

    @ApiModelProperty("准入时间-开始")
    private String enterStartTime;

    @ApiModelProperty("准入时间-截止")
    private String enterEndTime;

    @ApiModelProperty("姓名")
    private String name;

    @ApiModelProperty("身份证号")
    private String cardNo;

    @ApiModelProperty("联系电话")
    private String phone;

    @ApiModelProperty("所属公司")
    private String companyName;

    @ApiModelProperty("状态 10-驳回 20-审批中 25-超时未审批 30-审批通过")
    private String status;

    @ApiModelProperty("备注")
    private String remark;

    private String createTime;

    private String createTimeBegin;
    private String createTimeEnd;

    private List<String> recordTypeCodeList;
    private List<String> buttJointCodeList;
    private List<String> cardNoList;
}
