package com.yutong.modules.vehicle.entity;

import java.io.Serializable;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 事项违规上报
 * @Author: jeecg-boot
 * @Date:   2026-08-04
 * @Version: V1.0
 */
@Data
@TableName("ytsafety_case_violation_report")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ytsafety_case_violation_report对象", description="事项违规上报")
public class YtsafetyCaseViolationReport {
    
	/**雪花主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "雪花主键")
	private Long id;
	/**编号*/
	@Excel(name = "编号", width = 15)
    @ApiModelProperty(value = "编号")
	private String violationNo;
	/**时间*/
	@Excel(name = "时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "时间")
	private Date violationTime;
	/**名称*/
	@Excel(name = "名称", width = 15)
    @ApiModelProperty(value = "名称")
	private String violationTitle;
	/**检查人工号*/
	@Excel(name = "检查人工号", width = 15)
    @ApiModelProperty(value = "检查人工号")
	private String inspectorNo;
	/**检查人名称*/
	@Excel(name = "检查人名称", width = 15)
    @ApiModelProperty(value = "检查人名称")
	private String inspectorName;
	/**当事人类型*/
	@Excel(name = "当事人类型", width = 15)
    @ApiModelProperty(value = "当事人类型")
	private Integer personConcernedType;
	/**当事人名称*/
	@Excel(name = "当事人名称", width = 15)
    @ApiModelProperty(value = "当事人名称")
	private String personConcernedName;
	/**员工号/外协号/手机号*/
	@Excel(name = "员工号/外协号/手机号", width = 15)
    @ApiModelProperty(value = "员工号/外协号/手机号")
	private String accountNo;
	/**所属单位/部门主键*/
	@Excel(name = "所属单位/部门主键", width = 15)
    @ApiModelProperty(value = "所属单位/部门主键")
	private Integer blongUnitId;
	/**所属单位/部门编码*/
	@Excel(name = "所属单位/部门编码", width = 15)
    @ApiModelProperty(value = "所属单位/部门编码")
	private String blongUnitCode;
	/**所属单位/部门名称*/
	@Excel(name = "所属单位/部门名称", width = 15)
    @ApiModelProperty(value = "所属单位/部门名称")
	private String blongUnitName;
	/**责任部门主键*/
	@Excel(name = "责任部门主键", width = 15)
    @ApiModelProperty(value = "责任部门主键")
	private Integer departId;
	/**责任部门编码*/
	@Excel(name = "责任部门编码", width = 15)
    @ApiModelProperty(value = "责任部门编码")
	private String departCode;
	/**责任部门名称*/
	@Excel(name = "责任部门名称", width = 15)
    @ApiModelProperty(value = "责任部门名称")
	private String departName;
	/**违规类别主键*/
	@Excel(name = "违规类别主键", width = 15)
    @ApiModelProperty(value = "违规类别主键")
	private Integer violationCategoryId;
	/**违规类别名称*/
	@Excel(name = "违规类别名称", width = 15)
    @ApiModelProperty(value = "违规类别名称")
	private String violationCategoryName;
	/**违规事项主键*/
	@Excel(name = "违规事项主键", width = 15)
    @ApiModelProperty(value = "违规事项主键")
	private Integer violationMatterId;
	/**违规事项名称*/
	@Excel(name = "违规事项名称", width = 15)
    @ApiModelProperty(value = "违规事项名称")
	private String violationMatterName;
	/**违规性质主键*/
	@Excel(name = "违规性质主键", width = 15)
    @ApiModelProperty(value = "违规性质主键")
	private Integer violationNatureId;
	/**违规性质名称*/
	@Excel(name = "违规性质名称", width = 15)
    @ApiModelProperty(value = "违规性质名称")
	private String violationNatureName;
	/**处理方式*/
	@Excel(name = "处理方式", width = 15)
    @ApiModelProperty(value = "处理方式")
	private Integer handlingMethod;
	/**依旧制度*/
	@Excel(name = "依旧制度", width = 15)
    @ApiModelProperty(value = "依旧制度")
	private String oldRule;
	/**处理人工号*/
	@Excel(name = "处理人工号", width = 15)
    @ApiModelProperty(value = "处理人工号")
	private String handlerNo;
	/**处理人名称*/
	@Excel(name = "处理人名称", width = 15)
    @ApiModelProperty(value = "处理人名称")
	private String handlerName;
	/**POS流水号*/
	@Excel(name = "POS流水号", width = 15)
    @ApiModelProperty(value = "POS流水号")
	private String posSerialNo;
	/**处罚金额*/
	@Excel(name = "处罚金额", width = 15)
    @ApiModelProperty(value = "处罚金额")
	private Float penaltyAmount;
	/**交款日期*/
	@Excel(name = "交款日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "交款日期")
	private Date paymentDate;
	/**事项经过*/
	@Excel(name = "事项经过", width = 15)
    @ApiModelProperty(value = "事项经过")
	private Object incidentDesc;
	/**处理结果*/
	@Excel(name = "处理结果", width = 15)
    @ApiModelProperty(value = "处理结果")
	private Object disposalResult;
	/**是否有纸质凭证*/
	@Excel(name = "是否有纸质凭证", width = 15)
    @ApiModelProperty(value = "是否有纸质凭证")
	private Integer hasPaperVoucher;
	/**是否有附件*/
	@Excel(name = "是否有附件", width = 15)
    @ApiModelProperty(value = "是否有附件")
	private Integer hasAttachment;
	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private Date createTime;
	/**创建人*/
	@Excel(name = "创建人", width = 15)
    @ApiModelProperty(value = "创建人")
	private String createUser;
	/**创建人名称*/
	@Excel(name = "创建人名称", width = 15)
    @ApiModelProperty(value = "创建人名称")
	private String createName;
	/**更新时间*/
	@Excel(name = "更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
	private Date updateTime;
	/**更新人*/
	@Excel(name = "更新人", width = 15)
    @ApiModelProperty(value = "更新人")
	private String updateUser;
	/**更新人工号*/
	@Excel(name = "更新人工号", width = 15)
    @ApiModelProperty(value = "更新人工号")
	private String updateName;
	/**删除标识 0 正常 1 删除*/
	@Excel(name = "删除标识 0 正常 1 删除", width = 15)
    @ApiModelProperty(value = "删除标识 0 正常 1 删除")
	private Integer delFlag;
	/**乐观锁*/
	@Excel(name = "乐观锁", width = 15)
    @ApiModelProperty(value = "乐观锁")
	private Integer version;
}
