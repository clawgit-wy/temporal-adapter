package com.yutong.modules.common.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
@ApiModel(value="新增修改对象", description="流程汇总信息表")
public class YtsafetySummaryProcessDTO {
    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    private Long id;
    /**
     * 流程模板主键
     */
    @ApiModelProperty(value = "流程模板主键")
    private Long modelId;
    /**
     * 流程主键
     */
    @ApiModelProperty(value = "流程主键")
    private String flowId;
    /**
     * 流程编码
     */
    @ApiModelProperty(value = "流程编码")
    private String flowCode;
    /**
     * 流程主题
     */
    @ApiModelProperty(value = "流程主题")
    private String flowSubject;
    /**
     * 申请员工号
     */
    @ApiModelProperty(value = "申请员工号")
    private String applyNo;
    /**
     * 申请员工名
     */
    @ApiModelProperty(value = "申请员工名")
    private String applyName;
    /**
     * 所属部门编码
     */
    @ApiModelProperty(value = "所属部门编码")
    private String applyDepartCode;
    /**
     * 所属部门名称
     */
    @ApiModelProperty(value = "所属部门名称")
    private String applyDepartName;
    /**
     * 申请开始时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "申请开始时间")
    private Date applyStartTime;
    /**
     * 申请结束时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "申请结束时间")
    private Date applyEndTime;
    /**
     * 节点操作员工
     */
    @ApiModelProperty(value = "节点操作员工")
    private String nodeHandlerNo;
    /**
     * 节点操作员工名
     */
    @ApiModelProperty(value = "节点操作员工名")
    private String nodeHandlerName;
    /**
     * 流程节点ID
     */
    @ApiModelProperty(value = "流程节点ID")
    private String nodeId;
    /**
     * 流程节点名称
     */
    @ApiModelProperty(value = "流程节点名称")
    private String nodeName;
    /**
     * 表单访问地址
     */
    @ApiModelProperty(value = "表单访问地址")
    private String formUrl;
}
