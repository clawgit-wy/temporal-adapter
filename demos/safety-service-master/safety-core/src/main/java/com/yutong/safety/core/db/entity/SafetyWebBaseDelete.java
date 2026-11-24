package com.yutong.safety.core.db.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SafetyWebBaseDelete {

    @ApiModelProperty("单个删除的标记")
    private Integer id;

    @ApiModelProperty("批量删除的标记： 多个用逗号分隔")
    private String ids;

    @ApiModelProperty("删除原因")
    private String reason;
}
