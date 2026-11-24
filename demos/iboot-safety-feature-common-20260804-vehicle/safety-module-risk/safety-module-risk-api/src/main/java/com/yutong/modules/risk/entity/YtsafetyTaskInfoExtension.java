package com.yutong.modules.risk.entity;
import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 任务台账扩展表
 * @Author: jeecg-boot
 * @Date:   2026-06-30
 * @Version: V1.0
 */
@Data
@TableName("ytsafety_task_info_extension")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ytsafety_task_info_extension对象", description="任务台账扩展表")
public class YtsafetyTaskInfoExtension {

    /**主键*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private Long id;

    /**任务id*/
    @ApiModelProperty(value = "任务id")
    private String taskInfoId;

    /**所属厂区id(单选)*/
    @ApiModelProperty(value = "所属厂区id(单选)")
    private String factoryId;

    /**所属区域id(单选)*/
    @ApiModelProperty(value = "所属区域id(单选)")
    private String areaId;

    /**所属组织id(单选)*/
    @ApiModelProperty(value = "所属组织id(单选)")
    private String orgId;

    /**下发类型(默认下发:1、自定义下发:2)*/
    @ApiModelProperty(value = "下发类型(默认下发:1、自定义下发:2)")
    private String issueType;

    /**任务层级*/
    @ApiModelProperty(value = "任务层级")
    private String taskLevel;

    /**风险等级(风险等级字典表id)*/
    @ApiModelProperty(value = "风险等级(风险等级字典表id)")
    private String riskLevel;

    /**执行类型(单次、按天、按周、按月、自定义)*/
    @ApiModelProperty(value = "执行类型(单次、按天、按周、按月、自定义)")
    private String execType;

    /**周期类型(自定义周期)，非自定义默认0*/
    @ApiModelProperty(value = "周期类型(自定义周期)，非自定义默认0")
    private Integer cycleType;

    /**任务频率(执行几次)*/
    @ApiModelProperty(value = "任务频率(执行几次)")
    private Integer taskFrequency;

    /**执行日期(周一,周二... 1,2,3)*/
    @ApiModelProperty(value = "执行日期(周一,周二... 1,2,3)")
    private String executionDate;

    /**是否跳过周末(1是0否)*/
    @ApiModelProperty(value = "是否跳过周末(1是0否)")
    private Integer skipWeekend;

    /**是否跳过法定节假日(1是0否)*/
    @ApiModelProperty(value = "是否跳过法定节假日(1是0否)")
    private Integer skipHoliday;

    /**人员姓名(多选，逗号分割)*/
    @ApiModelProperty(value = "人员姓名(多选，逗号分割)")
    private String personnelName;

    /**人员岗位名称(多选，逗号分割)*/
    @ApiModelProperty(value = "人员岗位名称(多选，逗号分割)")
    private String personnelPostName;

    /**人员id(多选，逗号分割)*/
    @ApiModelProperty(value = "人员id(多选，逗号分割)")
    private String personnelId;

    /**人员岗位id(多选，逗号分割)*/
    @ApiModelProperty(value = "人员岗位id(多选，逗号分割)")
    private String personnelPostId;

    /**人员厂区id*/
    @ApiModelProperty(value = "人员厂区id")
    private String personnelFactoryId;

    /**人员组织id*/
    @ApiModelProperty(value = "人员组织id")
    private String personnelOrgId;

    /**当前执行周期*/
    @ApiModelProperty(value = "当前执行周期")
    private String currentExecutionCycle;

    /**当前执行状态*/
    @ApiModelProperty(value = "当前执行状态")
    private String taskStatus;

    /**附件必传*/
    @ApiModelProperty(value = "附件必传: 0 否  1是")
    private String fileMust;

    /**照片必传*/
    @ApiModelProperty(value = "照片必传:0 否  1是")
    private String photoMust;


}
