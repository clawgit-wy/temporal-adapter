package com.yutong.modules.common.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="分页查询对象", description="流程汇总信息表")
public class YtsafetySummaryProcessQueryDTO {
    /**流程编码*/
    @ApiModelProperty(value = "流程编码")
    private String flowCode;
    /**标题*/
    @ApiModelProperty(value = "标题")
    private String flowSubject;
    /**流程名称*/
    @ApiModelProperty(value = "流程名称")
    private String flowName;
    /**流程状态*/
    @ApiModelProperty(value = "流程状态")
    private Integer flowStatus;
    /**发起人*/
    @ApiModelProperty(value = "发起人")
    private String userInfo;
    /**创建时间开始节点*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间开始节点")
    private Date createTimeStart;
    /**创建时间结束节点*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间结束节点")
    private Date createTimeEnd;
    /**处理时间开始节点*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "处理时间开始节点")
    private Date handleTimeStart;
    /**处理时间结束节点*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "处理时间结束节点")
    private Date handleTimeEnd;
    /**完成时间开始节点*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "完成时间开始节点")
    private Date applyEndTimeStart;
    /**完成时间结束节点*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "完成时间结束节点")
    private Date applyEndTimeEnd;
    /**流程节点名称*/
    @ApiModelProperty(value = "当前环节")
    private String nodeName;
    /**上一环节处理人*/
    @ApiModelProperty(value = "上一环节处理人")
    private String preNodeHandler;
}
