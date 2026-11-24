package com.yutong.safety.core.db.entity.hik.param;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import com.yutong.safety.core.db.entity.hik.SafetyHikThresholdDO;
import org.apache.commons.lang3.StringUtils;

public class SafetyHikThresholdAddParam extends SafetyHikThresholdDO implements Check {

    @Override
    public void check() {
        PreconditonsEx.checkArgument(StringUtils.isNotBlank(getSceneName()), ResponseEnum.FAIL.getCode(), "“场景名称”不能为空");
        PreconditonsEx.checkArgument(StringUtils.isNotBlank(getSceneValue()), ResponseEnum.FAIL.getCode(), "“阈值”不能为空");
    }
}
