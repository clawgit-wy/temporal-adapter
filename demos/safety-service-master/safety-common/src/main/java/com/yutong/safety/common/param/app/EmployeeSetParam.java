package com.yutong.safety.common.param.app;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author gwcuiyx
 * @date 2024-4-7 14:00
 */
@Data
public class EmployeeSetParam implements Check {
    /**
     * 被访人手机号
     */
    private String phoneNo;
    /**
     * 被访人姓名
     */
    private String empName;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(StringUtils.isNotBlank(phoneNo), ResponseEnum.FAIL.getCode(), "被访人手机号不能为空");
        PreconditonsEx.checkArgument(StringUtils.isNotBlank(empName), ResponseEnum.FAIL.getCode(), "被访人姓名不能为空");
    }
}
