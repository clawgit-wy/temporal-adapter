package org.jeecg.common.domain;

import cn.hutool.json.JSONUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseQuery {

    @ApiModelProperty("查询页码， 默认1")
    private Integer pageNo = 1;

    @ApiModelProperty("每页大小， 默认10")
    private Integer pageSize = 10;

    @Override
    public String toString() {
        return JSONUtil.toJsonStr(this);
    }

}
