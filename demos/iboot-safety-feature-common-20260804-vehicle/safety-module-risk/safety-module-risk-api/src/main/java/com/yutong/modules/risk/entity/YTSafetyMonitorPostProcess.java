package com.yutong.modules.risk.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 监控岗位后处理表
 * @Author: jeecg-boot
 * @Date:   2026-07-06
 * @Version: V1.0
 */
@Data
@TableName("ytsafety_monitor_post_process")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ytsafety_monitor_post_process对象", description="监控岗位后处理表")
public class YTSafetyMonitorPostProcess implements Serializable {

    private static final long serialVersionUID = 1L;

    /**主键*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private Long id;

    /**模型主键*/
    @Excel(name = "模型主键", width = 15)
    @ApiModelProperty(value = "模型主键")
    private Long modelId;

    /**流程ID*/
    @Excel(name = "流程ID", width = 15)
    @ApiModelProperty(value = "流程ID")
    private String flowId;

    /**流程主题*/
    @Excel(name = "流程主题", width = 15)
    @ApiModelProperty(value = "流程主题")
    private String title;

    /**流程提交人员工号*/
    @Excel(name = "流程提交人员工号", width = 15)
    @ApiModelProperty(value = "流程提交人员工号")
    private String creator;

    /**流程创建时间*/
    @Excel(name = "流程创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "流程创建时间")
    private Date createTime;

    /**流程发布时间*/
    @Excel(name = "流程发布时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "流程发布时间")
    private Date publishTime;

    /**流程状态*/
    @Excel(name = "流程状态", width = 15)
    @ApiModelProperty(value = "流程状态")
    private Integer status;

    /**事件编号*/
    @Excel(name = "事件编号", width = 15)
    @ApiModelProperty(value = "事件编号")
    private String flowNo;

    /**上报人姓名*/
    @Excel(name = "上报人姓名", width = 15)
    @ApiModelProperty(value = "上报人姓名")
    private String submitName;

    /**上报人员工号*/
    @Excel(name = "上报人员工号", width = 15)
    @ApiModelProperty(value = "上报人员工号")
    private String submitNo;

    /**上报人部门班组*/
    @Excel(name = "上报人部门班组", width = 15)
    @ApiModelProperty(value = "上报人部门班组")
    private String submitDept;

    /**厂区*/
    @Excel(name = "厂区", width = 15)
    @ApiModelProperty(value = "厂区")
    private String factory;

    /**位置/岗位*/
    @Excel(name = "位置/岗位", width = 15)
    @ApiModelProperty(value = "位置/岗位")
    private String positionPost;

    /**镜头名称*/
    @Excel(name = "镜头名称", width = 15)
    @ApiModelProperty(value = "镜头名称")
    private String lensName;

    /**方式途径*/
    @Excel(name = "方式途径", width = 15)
    @ApiModelProperty(value = "方式途径")
    private String waysMeans;

    /**验证时间-开始*/
    @Excel(name = "验证时间-开始", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "验证时间-开始")
    private String checkTimeStart;

    /**验证时间-结束*/
    @Excel(name = "验证时间-结束", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "验证时间-结束")
    private String checkTimeEnd;

    /**异常事件类型*/
    @Excel(name = "异常事件类型", width = 15)
    @ApiModelProperty(value = "异常事件类型")
    private String abnormalType;

    /**异常事件描述*/
    @Excel(name = "异常事件描述", width = 15)
    @ApiModelProperty(value = "异常事件描述")
    private String abnormalDesc;

    /**异常事件接收人*/
    @Excel(name = "异常事件接收人", width = 15)
    @ApiModelProperty(value = "异常事件接收人")
    private String abnormalCreator;

    /**异常事件接收人员工号*/
    @Excel(name = "异常事件接收人员工号", width = 15)
    @ApiModelProperty(value = "异常事件接收人员工号")
    private String abnormalCreatorNo;

    /**异常事件接收人登录名*/
    @Excel(name = "异常事件接收人登录名", width = 15)
    @ApiModelProperty(value = "异常事件接收人登录名")
    private String abnormalCreatorEip;

    /**异常事件接收人部门班组编号*/
    @Excel(name = "异常事件接收人部门班组编号", width = 15)
    @ApiModelProperty(value = "异常事件接收人部门班组编号")
    private String abnormalDept;

    /**验证对象姓名*/
    @Excel(name = "验证对象姓名", width = 15)
    @ApiModelProperty(value = "验证对象姓名")
    private String checkName;

    /**验证对象员工号*/
    @Excel(name = "验证对象员工号", width = 15)
    @ApiModelProperty(value = "验证对象员工号")
    private String checkUserno;

    /**验证对象部门班组*/
    @Excel(name = "验证对象部门班组", width = 15)
    @ApiModelProperty(value = "验证对象部门班组")
    private String checkDept;

    /**是否异常*/
    @Excel(name = "是否异常", width = 15)
    @ApiModelProperty(value = "是否异常")
    private String checkException;

    /**是否考核兑现*/
    @Excel(name = "是否考核兑现", width = 15)
    @ApiModelProperty(value = "是否考核兑现")
    private String examFulfill;

    /**整改处理结果*/
    @Excel(name = "整改处理结果", width = 15)
    @ApiModelProperty(value = "整改处理结果")
    private String improveResult;

    /**备注*/
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private String remark;

    /**处理时间*/
    @Excel(name = "处理时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "处理时间")
    private Date auditTime;

    /**删除标记*/
    @Excel(name = "删除标记", width = 15)
    @ApiModelProperty(value = "删除标记")
    @TableLogic(value = "0", delval = "1")
    private Integer delFlag;

    /**修改版本*/
    @Excel(name = "修改版本", width = 15)
    @ApiModelProperty(value = "修改版本")
    @Version
    private Integer version;
}
