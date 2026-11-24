package com.yutong.safety.core.db.entity.entry.param;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class SafetyEntryConfigBatchParams implements Check {

    private String idStr;
    @ApiModelProperty("准入厂区编码")
    private String enterFactoryCode;
    @ApiModelProperty("准入时间-开始")
    private String enterStartTime;
    @ApiModelProperty("准入时间-截止")
    private String enterEndTime;
    private String enterFactoryName;

    @ApiModelProperty("审批状态 10-废弃 30-发布")
    private String status;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(StringUtils.isNotBlank(getIdStr()), ResponseEnum.PARAM_ERROR.getCode(), "请选择批量处理的数据");
        PreconditonsEx.checkArgument(StringUtils.isNotBlank(getEnterFactoryCode()), ResponseEnum.PARAM_ERROR.getCode(), "“准入厂区”不能为空");
        PreconditonsEx.checkArgument(StringUtils.isNotBlank(getEnterStartTime()), ResponseEnum.PARAM_ERROR.getCode(), "“开始时间”不能为空");
        PreconditonsEx.checkArgument(StringUtils.isNotBlank(getEnterEndTime()), ResponseEnum.PARAM_ERROR.getCode(), "“截止时间”不能为空");
    }
}
