package com.yutong.safety.core.db.entity.entry.param;

import com.yutong.safety.common.param.web.SafetyWebBaseParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class SafetyEntryBlankProcessDetailQueryParams extends SafetyWebBaseParam {

    @ApiModelProperty("对接人: 对接人名称或对接人工号")
    private String buttJoint;

    @ApiModelProperty("公司名称")
    private String companyName;

    @ApiModelProperty("身份证号")
    private String cardNo;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("提交日期")
    public String createTime;

    @ApiModelProperty("审核人")
    private String applyEmpInfo;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("分页标记-不需前端传值")
    private boolean pageFlag;

    private Integer processId;

    private String applyStatus;

    private String notApplyStatus;

    @ApiModelProperty("开始时间（包含）")
    private String containBeginTime;

    @ApiModelProperty("结束时间（包含）")
    private String containEndTime;

    @ApiModelProperty("身份证集合")
    private List<String> cardNoList;

    @ApiModelProperty("校验当前未失效")
    private Boolean checkNow;

    @ApiModelProperty("是否过滤备案类型不为空")
    private Boolean nullRecordType;

    @ApiModelProperty("非友商负责人")
    private String competitorDirector;
}
