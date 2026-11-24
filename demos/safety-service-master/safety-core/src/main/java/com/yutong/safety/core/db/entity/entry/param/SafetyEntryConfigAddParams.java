package com.yutong.safety.core.db.entity.entry.param;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import com.yutong.safety.core.db.entity.entry.SafetyEntryConfig;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class SafetyEntryConfigAddParams extends SafetyEntryConfig implements Check {

    private String openId;

    private Boolean modifyFlag;
    @Override
    public void check() {
        PreconditonsEx.checkArgument(StringUtils.isNotBlank(getName()), ResponseEnum.FAIL.getCode(), "“姓名”不能为空");
        PreconditonsEx.checkArgument(StringUtils.isNotBlank(getPhone()), ResponseEnum.FAIL.getCode(), "“手机号”不能为空");
        PreconditonsEx.checkArgument(StringUtils.isNotBlank(getCardNo()), ResponseEnum.FAIL.getCode(), "“身份证号”不能为空");
        PreconditonsEx.checkArgument(StringUtils.isNotBlank(getButtJointCode()), ResponseEnum.FAIL.getCode(), "“对接人员工号”不能为空");
        PreconditonsEx.checkArgument(StringUtils.isNotBlank(getRecordTypeCode()), ResponseEnum.FAIL.getCode(), "“人员类型”不能为空");
        PreconditonsEx.checkArgument(StringUtils.isNotBlank(getEnterStartTime()), ResponseEnum.FAIL.getCode(), "“备案开始时间”不能为空");
        PreconditonsEx.checkArgument(StringUtils.isNotBlank(getEnterEndTime()), ResponseEnum.FAIL.getCode(), "“备案截止时间”不能为空");
    }
}
