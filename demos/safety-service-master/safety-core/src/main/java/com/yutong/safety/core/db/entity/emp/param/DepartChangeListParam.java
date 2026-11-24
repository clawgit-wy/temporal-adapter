package com.yutong.safety.core.db.entity.emp.param;

import com.yutong.safety.common.param.web.WebPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DepartChangeListParam extends WebPageParam {

    @ApiModelProperty("分页标识-后端")
    private Boolean pageFlag;

    @ApiModelProperty("更新时间-起始")
    private String updateTimeBegin;

    @ApiModelProperty("更新时间-截止")
    private String updateTimeEnd;

    @ApiModelProperty("是否离职 是/否")
    private String changeType;

    @ApiModelProperty("员工号排查开头")
    private String employeeNoStart;

    @ApiModelProperty("员工号")
    private String employeeCode;
}
