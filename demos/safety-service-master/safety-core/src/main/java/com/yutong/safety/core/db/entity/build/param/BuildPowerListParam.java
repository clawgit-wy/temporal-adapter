package com.yutong.safety.core.db.entity.build.param;

import com.yutong.safety.common.param.web.WebPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class BuildPowerListParam extends WebPageParam {

    @ApiModelProperty("员工号")
    private String empNo;

    @ApiModelProperty("登录名")
    private String empLogin;

    @ApiModelProperty("门禁设备编码集合")
    private List<String> deviceCodeList;

    @ApiModelProperty("分页标记-不需前端传值")
    private Integer pageFlag;

    private String dutyDeptCode;

    private List<String> dutyDeptCodeList;
}
