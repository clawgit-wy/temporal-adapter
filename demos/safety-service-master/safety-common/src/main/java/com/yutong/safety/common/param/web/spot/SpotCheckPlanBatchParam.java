package com.yutong.safety.common.param.web.spot;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Objects;

@Data
public class SpotCheckPlanBatchParam  implements Check {
    @ApiModelProperty("批量操作-ID拼接串")
    private String ids;
    @ApiModelProperty("状态 0-正常 1-暂停")
    private Integer status;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(ids), ResponseEnum.PARAM_ERROR.getCode(), "选择有效的项目！");
        PreconditonsEx.checkArgument(Objects.nonNull(status), ResponseEnum.PARAM_ERROR.getCode(), "设置状态不能为空！");
    }
}
