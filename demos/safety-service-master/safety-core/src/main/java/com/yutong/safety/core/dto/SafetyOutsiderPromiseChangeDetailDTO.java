package com.yutong.safety.core.dto;

import com.yutong.safety.common.param.web.ProcessParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class SafetyOutsiderPromiseChangeDetailDTO {

    @ApiModelProperty("流程表主键ID")
    private String id;
    @ApiModelProperty("流程ID")
    private String flowId;
    @ApiModelProperty("员工号")
    private String empNo;
    @ApiModelProperty("姓名")
    private String empName;
    @ApiModelProperty("外协组织")
    private String org;
    @ApiModelProperty("授权工厂")
    private String factory;
    @ApiModelProperty("服务截止日期")
    private String serviceEndDate;
}
