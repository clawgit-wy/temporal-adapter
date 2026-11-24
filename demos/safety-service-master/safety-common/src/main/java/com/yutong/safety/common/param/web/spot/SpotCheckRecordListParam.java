package com.yutong.safety.common.param.web.spot;

import com.yutong.safety.common.param.web.WebPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class SpotCheckRecordListParam extends WebPageParam {
    @ApiModelProperty("周期任务ID")
    private Integer taskId;
    @ApiModelProperty("工厂ID")
    private Integer factoryId;
    @ApiModelProperty("类型")
    private String deviceType;
    @ApiModelProperty("项目ID")
    private Integer teamId;
    @ApiModelProperty("项目")
    private String teamName;
    @ApiModelProperty("位置")
    private String location;
    @ApiModelProperty("设备编码")
    private String deviceCode;
    @ApiModelProperty("责任部门")
    private String dutyName;
    @ApiModelProperty("点检日期-开始")
    private String spotBeginDate;
    @ApiModelProperty("点检日期-结束")
    private String spotEndDate;
    @ApiModelProperty("设备状态 0-正常 1-异常")
    private Integer deviceStatus;
    @ApiModelProperty("点检人员工号")
    private String spotEmpNo;
    @ApiModelProperty("点检人")
    private String spotEmp;
    @ApiModelProperty("计划整改日期-开始")
    private String planReformBegin;
    @ApiModelProperty("计划整改日期-结束")
    private String planReformEnd;
    @ApiModelProperty("点检人部门")
    private String checkOrgName;
    @ApiModelProperty("整改状态")
    private Integer isReform;
    @ApiModelProperty("整改责任人")
    private String reformEmp;
    @ApiModelProperty("整改验证结果 0-待验证 1-已验证")
    private Integer validateStatus;
    @ApiModelProperty("点检状态 0-待点检 1-已点检")
    private Integer status;
    @ApiModelProperty("验证时间-开始")
    private String validateBegin;
    @ApiModelProperty("验证时间-结束")
    private String validateEnd;
    @ApiModelProperty("验证人")
    private String validateEmp;
    @ApiModelProperty("分页标记 -后台逻辑使用")
    private Boolean pageFlag;
    @ApiModelProperty("计划点检人")
    private String planEmpNo;
    @ApiModelProperty("整改次数")
    private Integer reformNum;
    @ApiModelProperty("工厂ID拼接-后台使用")
    private String factoryIds;
    @ApiModelProperty("周期任务开始日期")
    private String taskBeginDate;
    @ApiModelProperty("周期任务结束日期")
    private String taskEndDate;
    @ApiModelProperty("周期任务-计划ID")
    private Integer planId;
    @ApiModelProperty("设备ID")
    private Integer deviceId;
    @ApiModelProperty("设备ID集合")
    private List<Integer> deviceIdList;
    @ApiModelProperty("bpm流程链接前缀-后台使用")
    private String bpmUrlPre;
    @ApiModelProperty("报修情况")
    private String deviceEipStatus;
}
