package com.yutong.safety.core.db.entity.entry.param;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.web.SafetyWebBaseParam;
import com.yutong.safety.common.utils.PreconditonsEx;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class SafetyEntryImportFailQueryParams extends SafetyWebBaseParam {

    @ApiModelProperty("版本号")
    private String versionNo;

    private String failRemark;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(StringUtils.isNotBlank(versionNo), ResponseEnum.FAIL.getCode(), "版本号不能为空");
    }
}
