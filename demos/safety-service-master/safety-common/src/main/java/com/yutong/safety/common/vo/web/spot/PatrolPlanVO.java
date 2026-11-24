package com.yutong.safety.common.vo.web.spot;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PatrolPlanVO {

    @ApiModelProperty("id")
    private Integer id;

    @ApiModelProperty("任务名称")
    private String planName;

    @ApiModelProperty("任务类型 1-治安巡查 2-业务验证")
    private Integer planType;

    @ApiModelProperty("工厂ID")
    private String factoryId;

    @ApiModelProperty("工厂名称")
    private String factoryName;

    @ApiModelProperty("任务周期")
    private Integer cycle;

    @ApiModelProperty("次数")
    private Integer frequency;

    @ApiModelProperty("事件类型ID")
    private String typeId;

    @ApiModelProperty("事件类型")
    private String typeName;

    @ApiModelProperty("事项id")
    private Integer teamId;

    @ApiModelProperty("事项")
    private String teamName;

    @ApiModelProperty("任务开始日期")
    private String beginDate;

    @ApiModelProperty("任务结束日期")
    private String endDate;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private String createTime;

    @ApiModelProperty("更新时间")
    private String updateTime;

    @ApiModelProperty("更新人")
    private String updatePerson;

    @ApiModelProperty("更新人员工号")
    private String updatePersonNo;

    @ApiModelProperty("休眠开始日期")
    private String dormancyBegin;

    @ApiModelProperty("休眠截止日期")
    private String dormancyEnd;

    @ApiModelProperty("应用角色ID")
    private Integer cycleRoleId;

    @ApiModelProperty("应用角色")
    private String cycleRoleName;

    @ApiModelProperty("事件确认角色ID")
    private Integer eventRoleId;

    @ApiModelProperty("事件确认角色")
    private String eventRoleName;

    @ApiModelProperty("任务类型名称")
    private String planTypeName;
}
