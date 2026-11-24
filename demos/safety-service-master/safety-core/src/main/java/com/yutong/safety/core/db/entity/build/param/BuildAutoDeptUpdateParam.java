package com.yutong.safety.core.db.entity.build.param;

import com.yutong.safety.common.param.Check;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class BuildAutoDeptUpdateParam  implements Check {

    private String id;

    @ApiModelProperty("禁用标记 0-正常 1-禁用")
    private String autoFlag;

    @Override
    public void check() {
    }
}
