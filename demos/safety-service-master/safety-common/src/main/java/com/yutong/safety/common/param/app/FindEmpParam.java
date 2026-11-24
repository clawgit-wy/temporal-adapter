package com.yutong.safety.common.param.app;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author gwcuiyx
 * @date 2024-2-28 17:17
 */
@Data
public class FindEmpParam implements Check {
    /**
     * 姓名
     */
    private String name ;
    /**
     * 员工号
     */
    private String mobile ;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(StringUtils.isNotBlank(name), ResponseEnum.FAIL.getCode(), "被访人姓名不能为空");
        PreconditonsEx.checkArgument(StringUtils.isNotBlank(mobile), ResponseEnum.FAIL.getCode(), "被访人手机号不能为空");
    }
}
