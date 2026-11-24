package com.yutong.safety.core.db.entity.employeeprivatecar.param;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import com.yutong.safety.core.db.entity.employeeprivatecar.SafetyEmployeePrivateCarDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Objects;

@Data
public class EmployeeCarAddParam extends SafetyEmployeePrivateCarDO implements Check {

    @ApiModelProperty("是否更换 是/否")
    private String changeFlag;

    @ApiModelProperty("是否注销 是/否")
    private String cancelFlag;

    @ApiModelProperty("历史车牌号")
    private String oldCarNo;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(super.getCarNo()), ResponseEnum.PARAM_ERROR.getCode(), "“车工号carNo”不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(super.getEmployeeNo()), ResponseEnum.PARAM_ERROR.getCode(), "“员工号employeeNo”不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(super.getMyselfCar()), ResponseEnum.PARAM_ERROR.getCode(), "“是否本人车辆myselfCar”不能为空！");
    }
}
