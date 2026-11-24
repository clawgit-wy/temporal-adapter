package com.yutong.safety.core.db.entity.enter;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author caomg
 * @date 2024-9-10 11:25
 * @description SafetyFactoryLimitConfigEntity
 */
@ApiModel("未入厂时间限制")
@Data
public class SafetyFactoryLimitConfigEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    private Integer id;

    @ApiModelProperty("时间限制")
    private Integer limitNumber;
}
