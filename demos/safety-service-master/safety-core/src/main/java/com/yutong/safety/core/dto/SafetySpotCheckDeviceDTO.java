package com.yutong.safety.core.dto;

import com.yutong.safety.core.db.entity.spot.SafetySpotCheckDeviceInfoDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SafetySpotCheckDeviceDTO extends SafetySpotCheckDeviceInfoDO {

    @ApiModelProperty("任务ID")
    private Integer taskId;
    @ApiModelProperty("计划点检人")
    private String planCheckEmp;
    @ApiModelProperty("计划点检人员工号")
    private String planCheckEmpNo;
}
