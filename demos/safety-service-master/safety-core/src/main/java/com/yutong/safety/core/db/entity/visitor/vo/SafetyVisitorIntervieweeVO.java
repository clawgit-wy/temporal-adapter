package com.yutong.safety.core.db.entity.visitor.vo;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class SafetyVisitorIntervieweeVO implements Check {

    private String workNo;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(StringUtils.isNotBlank(workNo), ResponseEnum.FAIL.getCode(), "员工号为空");
    }
}
