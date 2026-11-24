package com.yutong.safety.common.param.web.spot;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Objects;

@Data
public class SpotCheckPlanDormancyParam implements Check {
    @ApiModelProperty("批量操作-ID拼接串")
    private String ids;
    @ApiModelProperty("休眠开始日期")
    private String dormancyBegin;
    @ApiModelProperty("休眠截止日期")
    private String dormancyEnd;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(ids), ResponseEnum.PARAM_ERROR.getCode(), "选择有效的项目！");
        PreconditonsEx.checkArgument(Objects.nonNull(dormancyBegin), ResponseEnum.PARAM_ERROR.getCode(), "“休眠开始日期”不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(dormancyEnd), ResponseEnum.PARAM_ERROR.getCode(), "“休眠结束日期”不能为空！");
    }
}
