package com.yutong.modules.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="分页查询映射类", description="流程汇总信息表")
public class YtsafetySummaryProcessVO {
    /**主键*/
    @ApiModelProperty(value = "主键")
    private Long id;
    /**系统标识*/
    @ApiModelProperty(value = "系统标识")
    private String sysId;
    /**流程模板标识*/
    @ApiModelProperty(value = "流程模板标识")
    private String modelFlag;
    /**流程表单标识*/
    @ApiModelProperty(value = "流程表单标识")
    private String templateFormId;
    /**流程主键*/
    @ApiModelProperty(value = "流程主键")
    private String flowId;
    /**流程编号*/
    @ApiModelProperty(value = "流程编号")
    private String flowCode;
    /**流程主题*/
    @ApiModelProperty(value = "流程主题")
    private String flowSubject;
    /**流程名称*/
    @ApiModelProperty(value = "流程名称")
    private String flowName;
    /**流程状态*/
    @ApiModelProperty(value = "流程状态")
    private Integer flowStatus;
    /**申请员工号*/
    @ApiModelProperty(value = "申请员工号")
    private String applyNo;
    /**发起人*/
    @ApiModelProperty(value = "发起人")
    private String applyName;
    /**所属部门编码*/
    @ApiModelProperty(value = "所属部门编码")
    private String applyDepartCode;
    /**所属部门名称*/
    @ApiModelProperty(value = "所属部门名称")
    private String applyDepartName;
    /**处理时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "处理时间")
    private Date handlerDate;
    /**完成时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "完成时间")
    private Date applyEndTime;
    /**流程节点ID*/
    @ApiModelProperty(value = "流程节点ID")
    private String nodeId;
    /**当前环节*/
    @ApiModelProperty(value = "当前环节")
    private String nodeName;
    /**驳回原因*/
    @ApiModelProperty(value = "驳回原因")
    private String denyReason;
    /**前一节点处理人 多个以“,”隔开*/
    @ApiModelProperty(value = "上一环节处理人")
    private String preNodeHandlerName;
    /**表单访问地址*/
    @ApiModelProperty(value = "表单访问地址")
    private String formUrl;
    /**创建时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "是否昵称")
    private String isNickName;
    @ApiModelProperty(value = "昵称")
    private String nickName;
}
