package com.yutong.modules.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description: 流程汇总信息表
 * @Author: jeecg-boot
 * @Date:   2026-06-18
 * @Version: V1.0
 */
@Data
@TableName("ytsafety_summary_process")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ytsafety_summary_process对象", description="流程汇总信息表")
public class YtsafetySummaryProcess {
    
	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
	private Long id;
	/**流程标识*/
    @ApiModelProperty(value = "流程标识")
	private String modelFlag;
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
	/**申请员工号*/
    @ApiModelProperty(value = "申请员工号")
	private String applyNo;
    /**申请人登录账号*/
    @ApiModelProperty(value = "申请人登录账号")
    private String applyLogin;
	/**申请员工名*/
    @ApiModelProperty(value = "申请员工名")
	private String applyName;
    /**所属部门编码*/
    @ApiModelProperty(value = "所属部门编码")
    private String applyDepartCode;
    /**所属部门名称*/
    @ApiModelProperty(value = "所属部门名称")
    private String applyDepartName;
    /**所属部门编码*/
    @ApiModelProperty(value = "所属厂区主键")
    private Long factoryId;
    /**所属厂区名称*/
    @ApiModelProperty(value = "所属厂区名称")
    private String factoryName;
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
    /**当前节点审批人 多个以“,”隔开*/
    @ApiModelProperty(value = "当前节点审批人")
    private String curNodeHandler;
    /**前一节点处理人 */
    @ApiModelProperty(value = "前一节点处理人")
    private String preNodeHandler;
    /**前一节点处理人名称*/
    @ApiModelProperty(value = "前一节点处理人名称")
    private String preNodeHandlerName;
    /**任务主键*/
    @ApiModelProperty(value = "任务主键")
    private String taskId;
	/**流程节点ID*/
    @ApiModelProperty(value = "流程节点ID")
	private String nodeId;
	/**流程节点名称*/
    @ApiModelProperty(value = "流程节点名称")
	private String nodeName;
    /**驳回原因*/
    @ApiModelProperty(value = "驳回原因")
    private String denyReason;
	/**表单访问地址*/
    @ApiModelProperty(value = "表单访问地址")
	private String formUrl;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private Date createTime;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
	private String createUser;
	/**更新时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
	private Date updateTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
	private String updateUser;
	/**删除标识 0 正常 1 删除*/
    @ApiModelProperty(value = "删除标识 0 正常 1 删除")
	private Integer delFlag;
	/**乐观锁*/
    @ApiModelProperty(value = "乐观锁")
	private Integer version;
    @ApiModelProperty(value = "是否昵称")
    private String isNickName;
    @ApiModelProperty(value = "昵称")
    private String nickName;
    @ApiModelProperty(value = "昵称账号")
    private String nickAccount;
    @ApiModelProperty(value = "是否同步")
    private String isSynchro;
}
