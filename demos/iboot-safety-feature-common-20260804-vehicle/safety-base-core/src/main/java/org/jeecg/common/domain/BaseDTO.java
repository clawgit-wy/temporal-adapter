package org.jeecg.common.domain;

import cn.hutool.json.JSONUtil;
import io.swagger.annotations.ApiModelProperty;

public class BaseDTO {

    @ApiModelProperty("主键id，修改数据唯一标识，新增时候不用填")
    private Long id;

    @Override
    public String toString() {
        return JSONUtil.toJsonStr(this);
    }
}
