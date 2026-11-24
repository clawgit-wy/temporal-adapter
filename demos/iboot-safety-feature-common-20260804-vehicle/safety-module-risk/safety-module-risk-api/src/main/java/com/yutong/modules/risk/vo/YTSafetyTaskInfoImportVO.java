package com.yutong.modules.risk.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.common.excel.core.BaseImportData;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description: 任务台账表VO
 * @Author: jeecg-boot
 * @Date:   2026-06-25
 * @Version: V1.0
 */
@Data
@ApiModel(value="任务台账表VO", description="任务台账表VO")
public class YTSafetyTaskInfoImportVO extends BaseImportData {
    
	/**主键*/
    @ApiModelProperty(value = "主键")
	private Long id;
	/**任务编号*/
    @ApiModelProperty(value = "任务编号")
	private String taskCode;
	/**任务名称*/
    @ApiModelProperty(value = "任务名称")
	private String taskName;
	/**任务类型(巡查、专项、定点)*/
    @ApiModelProperty(value = "任务类型(巡查、专项、定点)")
	private String taskType;
	/**任务类别(风险类型字典表id)*/
    @ApiModelProperty(value = "任务类别(风险类型字典表id)")
	private Long riskCategoryId;
	/**风险等级(风险等级字典表id)*/
    @ApiModelProperty(value = "风险等级(风险等级字典表id)")
	private String riskLevel;
	/**任务描述(任务要求)*/
    @ApiModelProperty(value = "任务描述(任务要求)")
	private String taskDescription;
	/**开始日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "开始日期")
	private Date startDate;
	/**结束日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "结束日期")
	private Date endDate;
	/**任务执行范围(电脑:1、手机:2)*/
    @ApiModelProperty(value = "任务执行范围(电脑:1、手机:2)")
	private String execScope;
	/**下发类型(默认下发:1、自定义下发:2)*/
    @ApiModelProperty(value = "下发类型(默认下发:1、自定义下发:2)")
	private String issueType;
	/**任务层级*/
    @ApiModelProperty(value = "任务层级")
	private String taskLevel;
	/**任务状态(待执行、执行中、已过期、已休眠)*/
    @ApiModelProperty(value = "任务状态(待执行、执行中、已过期、已休眠)")
	private String taskStatus;
	/**附件id(文件服务回传id)*/
    @ApiModelProperty(value = "附件id(文件服务回传id)")
	private String attachmentId;
	/**附件图片id(文件服务回传id)*/
    @ApiModelProperty(value = "附件图片id(文件服务回传id)")
	private String attachmentImgId;
	/**所属厂区id(多选，逗号分割)*/
    @ApiModelProperty(value = "所属厂区id(多选，逗号分割)")
	private String factoryId;
	/**所属区域id(多选，逗号分割)*/
    @ApiModelProperty(value = "所属区域id(多选，逗号分割)")
	private String areaId;
	/**所属组织id(多选，逗号分割)*/
    @ApiModelProperty(value = "所属组织id(多选，逗号分割)")
	private String orgId;
	/**风险数据id(多选，逗号分割)*/
    @ApiModelProperty(value = "风险数据id(多选，逗号分割)")
	private String riskDataId;
	/**人员id(多选，逗号分割)*/
    @ApiModelProperty(value = "人员id(多选，逗号分割)")
	private String personnelId;
	/**岗位id(多选，逗号分割)*/
    @ApiModelProperty(value = "岗位id(多选，逗号分割)")
	private String postId;
	/**执行类型(单次、按天、按周、按月、自定义)*/
    @ApiModelProperty(value = "执行类型(单次、按天、按周、按月、自定义)")
	private String execType;
	/**周期类型(自定义周期)，非自定义默认0*/
    @ApiModelProperty(value = "周期类型(自定义周期)，非自定义默认0")
	private Integer cycleType;
	/**任务频率(执行几次)*/
    @ApiModelProperty(value = "任务频率(执行几次)")
	private Integer taskFrequency;
	/**是否跳过周末(1是0否)*/
    @ApiModelProperty(value = "是否跳过周末(1是0否)")
	private Integer skipWeekend;
	/**是否跳过法定节假日(1是0否)*/
    @ApiModelProperty(value = "是否跳过法定节假日(1是0否)")
	private Integer skipHoliday;
	/**岗位类型(1:监控岗位 2:现场岗位 3:模拟岗位)*/
    @ApiModelProperty(value = "岗位类型(1:监控岗位 2:现场岗位 3:模拟岗位)")
	private String postType;
	/**休眠日期(休眠状态下展示)*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "休眠日期(休眠状态下展示)")
	private Date dormantDate;
	/**创建人姓名*/
    @ApiModelProperty(value = "创建人姓名")
	private String createName;
	/**创建人工号*/
    @ApiModelProperty(value = "创建人工号")
	private String createUser;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private Date createTime;
	/**修改人姓名*/
    @ApiModelProperty(value = "修改人姓名")
	private String updateName;
	/**修改人工号*/
    @ApiModelProperty(value = "修改人工号")
	private String updateUser;
	/**修改时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
	private Date updateTime;
	/**删除标记*/
    @ApiModelProperty(value = "删除标记")
	private Integer delFlag;
	/**修改版本*/
    @ApiModelProperty(value = "修改版本")
	private Integer version;

    private YtsafetyTaskInfoExtensionVO ytsafetyTaskInfoExtension;

}
