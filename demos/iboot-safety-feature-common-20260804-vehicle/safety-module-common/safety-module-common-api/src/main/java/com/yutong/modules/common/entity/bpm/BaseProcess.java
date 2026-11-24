package com.yutong.modules.common.entity.bpm;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BaseProcess {

    /**主键*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private Long id;
    /**流程模板主键*/
    @ApiModelProperty(value = "流程模板主键")
    private Long modelId;
    /**流程主键*/
    @ApiModelProperty(value = "流程主键")
    private String flowId;
    /**流程编码*/
    @ApiModelProperty(value = "流程编码")
    private String flowCode;
    /**流程名称*/
    @ApiModelProperty(value = "流程名称")
    private String flowName;
    /**流程主题*/
    @ApiModelProperty(value = "流程主题")
    private String flowSubject;
    /**流程状态*/
    @ApiModelProperty(value = "流程状态")
    private Integer flowStatus;
    /**前一节点处理人 多个以“,”隔开*/
    @ApiModelProperty(value = "前一节点处理人“,”隔开")
    private String preNodeHandler;
    /**任务主键*/
    @ApiModelProperty(value = "任务主键")
    private String taskId;
    /**流程节点ID*/
    @ApiModelProperty(value = "流程节点ID")
    private String nodeId;
    /**流程节点名称*/
    @ApiModelProperty(value = "流程节点名称")
    private String nodeName;
    /**申请开始时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "申请开始时间")
    private Date applyStartTime;
    /**处理时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "处理时间")
    private Date handlerDate;
    /**申请结束时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "申请结束时间")
    private Date applyEndTime;
}
