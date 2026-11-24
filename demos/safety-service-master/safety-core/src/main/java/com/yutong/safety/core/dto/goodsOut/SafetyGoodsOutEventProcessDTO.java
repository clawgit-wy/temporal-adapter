package com.yutong.safety.core.dto.goodsOut;

import com.yutong.safety.common.param.web.ProcessParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.regex.Pattern;

@Data
public class SafetyGoodsOutEventProcessDTO {
        @ApiModelProperty(value = "主键ID")
        private String id;

        @ApiModelProperty(value = "违规事件上报的流程ID")
        private String processId;

        @ApiModelProperty(value = "违规事件上报的流程主题")
        private String processSubject;

        @ApiModelProperty(value = "流程状态")
        private String processStatus;

        @ApiModelProperty(value = "当前流程节点ID")
        private String currentNodeId;

        @ApiModelProperty(value = "上报人姓名")
        private String reporterName;

        @ApiModelProperty(value = "上报人EIP")
        private String reporterEip;

        @ApiModelProperty(value = "上报人员工号")
        private String reporterEmployeeId;

        @ApiModelProperty(value = "上报人部门")
        private String reporterDepartment;

        @ApiModelProperty(value = "上报人手机号")
        private String reporterPhoneNumber;

        @ApiModelProperty(value = "创建时间")
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private Date creationTime;

        @ApiModelProperty(value = "关联出门证流程ID")
        private String relatedProcessId;

        @ApiModelProperty(value = "关联出门证流程编号")
        private String flowNo;

        @ApiModelProperty(value = "关联出门证流程主题")
        private String subject;

        @ApiModelProperty(value = "事件发生时间")
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private Date eventOccurrenceTime;

        @ApiModelProperty(value = "厂区名称")
        private String siteName;

        @ApiModelProperty(value = "厂区ID")
        private String siteId;

        @ApiModelProperty(value = "具体位置")
        private String specificLocation;

        @ApiModelProperty(value = "事件类型")
        private String eventType;

        @ApiModelProperty(value = "问题类型")
        private String issueType;

        @ApiModelProperty(value = "事件等级")
        private String eventLevel;

        @ApiModelProperty(value = "问题描述")
        private String issueDescription;

        @ApiModelProperty(value = "责任人姓名")
        private String responsiblePersonName;

        @ApiModelProperty(value = "责任人EIP")
        private String responsiblePersonEip;

        @ApiModelProperty(value = "责任人员工号")
        private String responsiblePersonEmployeeId;

        @ApiModelProperty(value = "责任人部门")
        private String responsiblePersonDepartment;

        @ApiModelProperty(value = "整改完成时间")
        private String remediationCompletionTime;

        @ApiModelProperty(value = "整改负责人员工号")
        private String remediationEmployeeId;

        @ApiModelProperty(value = "整改负责人姓名")
        private String remediationName;

        @ApiModelProperty(value = "整改负责人EIP")
        private String remediationEip;

        @ApiModelProperty(value = "整改负责人手机号")
        private String remediationPhoneNumber;

        @ApiModelProperty(value = "整改措施")
        private String remediationMeasure;

        @ApiModelProperty(value = "更新时间")
        private Date lastUpdateTime;

        @ApiModelProperty(value = "异常记录ID")
        private Integer exceptionRecordId;

        @ApiModelProperty("流程节点审批意见")
        private String auditNote;

        @ApiModelProperty("流程参数")
        private ProcessParam processParam;

        @ApiModelProperty("异常明细id")
        private Integer exceptionId;
}
