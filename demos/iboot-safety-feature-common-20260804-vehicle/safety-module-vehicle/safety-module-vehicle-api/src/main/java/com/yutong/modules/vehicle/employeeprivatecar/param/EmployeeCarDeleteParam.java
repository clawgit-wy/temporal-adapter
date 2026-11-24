package com.yutong.modules.vehicle.employeeprivatecar.param;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.common.util.Check;
import org.jeecg.common.util.PreconditonsEx;
import org.jeecg.common.util.ResponseEnum;

import java.util.Objects;

@Data
public class EmployeeCarDeleteParam implements Check {

    @ApiModelProperty("员工号")
    private String empNo;

    @ApiModelProperty("车牌号")
    private String carNo;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(empNo), ResponseEnum.PARAM_ERROR.getCode(), "“员工号empNo”不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(carNo), ResponseEnum.PARAM_ERROR.getCode(), "”车牌号carNo”不能为空！");
    }
}
