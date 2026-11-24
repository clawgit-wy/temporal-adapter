package com.yutong.safety.core.dto;

import com.yutong.safety.core.db.entity.spot.SafetySpotCheckRecordDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SpotCheckRecordDTO extends SafetySpotCheckRecordDO {

    @ApiModelProperty("类型")
    private String deviceType;
    @ApiModelProperty("项目ID")
    private Integer teamId;
    @ApiModelProperty("项目名称")
    private String teamName;
    @ApiModelProperty("工厂ID")
    private Integer factoryId;
    @ApiModelProperty("工厂编码")
    private String factoryCode;
    @ApiModelProperty("工厂")
    private String factoryName;
    @ApiModelProperty("位置")
    private String location;
    @ApiModelProperty("设备编码")
    private String deviceCode;
    @ApiModelProperty("整改次数")
    private Integer reformNum;
    @ApiModelProperty("点检人部门")
    private String checkOrgName;
    @ApiModelProperty("责任部门")
    private String dutyName;
    @ApiModelProperty("点检时间")
    private String checkTimeStr;
    @ApiModelProperty("整改时间")
    private String reformTimeStr;
    @ApiModelProperty("验证时间")
    private String validateTimeStr;
    @ApiModelProperty("设备点检状态")
    private String recordStatus;
    @ApiModelProperty("任务结束日期")
    private String taskEndDate;
    @ApiModelProperty("BPM流程ID")
    private String flowId;
    @ApiModelProperty("BPM流程URL")
    private String flowUrl;
    @ApiModelProperty("整改照片")
    private String reformImg;

    @ApiModelProperty("保修情况")
    private String deviceEipStatus;
}
