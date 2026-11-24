package com.yutong.modules.risk.web;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class PatrolTaskListParam extends WebPageParam {
    private Integer planId;
    @ApiModelProperty("任务名称")
    private String planName;
    @ApiModelProperty("任务类型 1-治安巡查 2-业务验证")
    private Integer planType;
    private Integer taskId;
    @ApiModelProperty("点检日期-开始")
    private String taskBeginDate;
    @ApiModelProperty("点检日期-结束")
    private String taskEndDate;
    @ApiModelProperty("状态")
    private Integer status;
    @ApiModelProperty("工厂ID")
    private String factoryId;
    @ApiModelProperty("类型ID")
    private Integer typeId;
    @ApiModelProperty("类型")
    private String typeName;
    @ApiModelProperty("项目ID")
    private Integer teamId;
    @ApiModelProperty("项目")
    private String teamName;
    @ApiModelProperty("分页标记-后台使用 true-分页  false-不分页")
    private Boolean pageFlag;
    @ApiModelProperty("巡检人员工号")
    private String patrolEmpNo;
    @ApiModelProperty("工厂ID拼接串")
    private String factoryStr;
    private List<Integer> planIdList;
}
