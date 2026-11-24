package com.yutong.safety.core.db.entity.enter.search;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class SafetyEnterStatisticSh implements Serializable {

    @ApiModelProperty("准入时间-始")
    private String fdFilingBegin;

    @ApiModelProperty("准入时间-终")
    private String fdFilingEnd;

    private String isAdmin;
}
