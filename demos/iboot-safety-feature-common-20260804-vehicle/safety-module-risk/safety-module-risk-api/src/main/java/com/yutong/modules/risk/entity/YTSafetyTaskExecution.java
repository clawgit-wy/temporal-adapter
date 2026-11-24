package com.yutong.modules.risk.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.Date;

/**
 * @Description: 用户任务执行表
 * @Author: jeecg-boot
 * @Date:   2026-06-25
 * @Version: V1.0
 */
@Data
@TableName("ytsafety_task_execution")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ytsafety_task_execution对象", description="用户任务执行表")
public class YTSafetyTaskExecution {
    
	/**主键*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
	private Long id;
	/**任务台账id*/
	@Excel(name = "任务台账id", width = 15)
    @ApiModelProperty(value = "任务台账id")
	private Long taskInfoId;
	/**任务编号*/
	@Excel(name = "任务编号", width = 15)
    @ApiModelProperty(value = "任务编号")
	private String taskCode;
	/**任务名称*/
	@Excel(name = "任务名称", width = 15)
    @ApiModelProperty(value = "任务名称")
	private String taskName;
	/**所属厂区id(单选)*/
	@Excel(name = "所属厂区id", width = 15)
    @ApiModelProperty(value = "所属厂区id(单选)")
	private String factoryId;
	/**所属区域id(单选)*/
	@Excel(name = "所属区域id", width = 15)
    @ApiModelProperty(value = "所属区域id(单选)")
	private String areaId;
	/**所属组织id(单选)*/
	@Excel(name = "所属组织id", width = 15)
    @ApiModelProperty(value = "所属组织id(单选)")
	private String orgId;
	/**执行用户id(单选)*/
	@Excel(name = "执行用户id", width = 15)
    @ApiModelProperty(value = "执行用户id(单选)")
	private String execUserId;
	/**执行用户姓名(单选)*/
	@Excel(name = "执行用户姓名", width = 15)
    @ApiModelProperty(value = "执行用户姓名(单选)")
	private String execUserName;
	/**上报岗位id*/
	@Excel(name = "上报岗位id", width = 15)
    @ApiModelProperty(value = "上报岗位id")
	private Long reportPostId;
	/**风险等级*/
	@Excel(name = "风险等级", width = 15)
    @ApiModelProperty(value = "风险等级")
	private String riskLevel;
	/**风险类别(风险类型字典表id)*/
	@Excel(name = "风险类别", width = 15)
    @ApiModelProperty(value = "风险类别(风险类型字典表id)")
	private String riskCategoryId;
	/**任务开始时间*/
	@Excel(name = "任务开始时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "任务开始时间")
	private Date taskStartTime;
	/**任务完成时间*/
	@Excel(name = "任务完成时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "任务完成时间")
	private Date taskCompleteTime;
	/**完成间隔时间*/
	@Excel(name = "完成间隔时间", width = 15)
    @ApiModelProperty(value = "完成间隔时间")
	private String completeInterval;
	/**任务状态(已完成、执行中、超期完成、超期未完成)*/
	@Excel(name = "任务状态", width = 15)
    @ApiModelProperty(value = "任务状态(已完成、执行中、超期完成、超期未完成)")
	private String taskStatus;
	/**是否异常(1是0否)*/
	@Excel(name = "是否异常", width = 15)
    @ApiModelProperty(value = "是否异常(1是0否)")
	private Integer isAbnormal;
	/**附件图片id(文件服务回传id)*/
    @TableField(updateStrategy = FieldStrategy.IGNORED)
	@Excel(name = "附件图片id", width = 15)
    @ApiModelProperty(value = "附件图片id(文件服务回传id)")
	private String attachmentImgId;
	/**附件id(文件服务回传id)*/
	@Excel(name = "附件id", width = 15)
    @ApiModelProperty(value = "附件id(文件服务回传id)")
	private String attachmentId;
	/**创建人姓名*/
	@Excel(name = "创建人姓名", width = 15)
    @ApiModelProperty(value = "创建人姓名")
	private String createName;
	/**创建人工号*/
	@Excel(name = "创建人工号", width = 15)
    @ApiModelProperty(value = "创建人工号")
	private String createUser;
	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private Date createTime;
	/**修改人姓名*/
	@Excel(name = "修改人姓名", width = 15)
    @ApiModelProperty(value = "修改人姓名")
	private String updateName;
	/**修改人工号*/
	@Excel(name = "修改人工号", width = 15)
    @ApiModelProperty(value = "修改人工号")
	private String updateUser;
	/**修改时间*/
	@Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
	private Date updateTime;
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

    @Excel(name = "任务周期开始时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "任务开始时间")
    private Date startDate;
    /**任务完成时间*/
    @Excel(name = "任务周期结束时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "任务完成时间")
    private Date endDate;

    @ApiModelProperty(value = "审批人 多个逗号分割")
    private String approvalUser;

}
