package com.yutong.safety.core.db.entity.visitor.param;

import com.yutong.safety.common.param.web.SafetyWebBaseParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class SafetyVisitorAuthorizationQueryParam extends SafetyWebBaseParam {

    @ApiModelProperty("状态：0-审批中，1-正常，2-注销")
    private Integer status;

    @ApiModelProperty("授权人姓名/员工号")
    private String grantorInfo;

    @ApiModelProperty("被授权人姓名/员工号")
    private String granteeInfo;


    @ApiModelProperty("分页标记-不需前端传值")
    private boolean pageFlag;

    @ApiModelProperty("授权日期-截止日期-大于等于")
    private String authorizationEndTime;

    @ApiModelProperty("授权日期-截止日期-小于")
    private String ltAuthorizationEndTime;

    @ApiModelProperty("授权日期-截止日期-大于")
    private String gtAuthorizationEndTime;

    @ApiModelProperty("授权日期-截止日期-等于")
    private String eqAuthorizationEndTime;

    @ApiModelProperty("流程ID，此字段不等于null时，获取不为空的数据")
    private String bpmFlowIdNotNull;

    @ApiModelProperty("流程ID，此字段不等于null时，获取为空的数据")
    private String bpmFlowIdNull;

    @ApiModelProperty("流程ID")
    private String bpmFlowId;

    @ApiModelProperty("授权人工号")
    private List<String> grantorNoList;

    @ApiModelProperty("被授权人工号")
    private List<String> granteeNoList;
}
