package com.yutong.safety.core.db.entity.hik.param;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import com.yutong.safety.core.db.entity.hik.SafetyHikGroupDO;
import org.apache.commons.lang3.StringUtils;

public class SafetyHikGroupAddParam extends SafetyHikGroupDO implements Check {
    @Override
    public void check() {
        PreconditonsEx.checkArgument(StringUtils.isNotBlank(getName()), ResponseEnum.FAIL.getCode(), "“人脸分组名称”不能为空");
        PreconditonsEx.checkArgument(StringUtils.isNotBlank(getBuildCode()), ResponseEnum.FAIL.getCode(), "“人脸分组描述”不能为空");
    }
}
