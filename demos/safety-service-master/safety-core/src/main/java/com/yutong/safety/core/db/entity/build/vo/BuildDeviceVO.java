package com.yutong.safety.core.db.entity.build.vo;

import com.yutong.safety.core.db.entity.build.SafetyBuildDeviceDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildPowerDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class BuildDeviceVO extends SafetyBuildDeviceDO {

    @ApiModelProperty("责任部门集合")
    private List<SafetyBuildPowerDO> dutyDeptList;
    @ApiModelProperty("保密等级名称")
    private String securityLevelName;

    private String createTimeStr;
    private String officeTimeStr;
    private String updateTimeStr;
}
