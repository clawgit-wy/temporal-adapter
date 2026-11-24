package com.yutong.safety.common.vo.web;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author gwcuiyx
 * @date 2024-4-28 18:44
 */
@Data
public class SpotCheckDeviceInfoVO {
    @ApiModelProperty("id")
    private Integer id;
    @ApiModelProperty("设备编号")
    private String deviceCode;
    @ApiModelProperty("设备名称")
    private String deviceName;
    @ApiModelProperty("工厂ID")
    private Integer factoryId;
    @ApiModelProperty("工厂")
    private String factoryName;
    @ApiModelProperty("类型")
    private String deviceType;
    @ApiModelProperty("项目ID")
    private Integer teamId;
    @ApiModelProperty("项目")
    private String teamName;
    @ApiModelProperty("位置")
    private String location;
    @ApiModelProperty("责任部门")
    private String dutyName;
    @ApiModelProperty("点检人员工号")
    private String checkEmpNo;
    @ApiModelProperty("点检人")
    private String checkEmp;
    @ApiModelProperty("临时点检人员工号")
    private String proTemCheckEmpNo;
    @ApiModelProperty("临时点检人")
    private String proTemCheckEmp;
    @ApiModelProperty("临时点检人应用开始时间")
    private String proTemBeginDate;
    @ApiModelProperty("临时点检人应用结束时间")
    private String proTemEndDate;
    @ApiModelProperty("备注")
    private String remark;
    @ApiModelProperty("创建时间")
    private Date createTime;
    @ApiModelProperty("更新人")
    private String updateEmp;
    @ApiModelProperty("更新人员工号")
    private String updateEmpNo;
    @ApiModelProperty("更新时间")
    private Date updateTime;
    @ApiModelProperty("累计故障次数")
    private Integer faultTotal;
    @ApiModelProperty("应用巡检标准ID拼接")
    private String ruleIds;
    @ApiModelProperty("应用巡检标准拼接")
    private String ruleNames;
    @ApiModelProperty("设备类型编号")
    private String deviceFieldCode;
    @ApiModelProperty("设备当前状态")
    private Integer deviceStatus;
}
