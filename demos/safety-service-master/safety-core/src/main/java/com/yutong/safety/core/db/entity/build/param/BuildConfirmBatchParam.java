package com.yutong.safety.core.db.entity.build.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class BuildConfirmBatchParam implements Serializable {

    @ApiModelProperty("处理类型 1-保留 2-关闭")
    private String dealType;

    @ApiModelProperty("门禁清单")
    private List<BuildConfirmAddParam> confirmList;
}
