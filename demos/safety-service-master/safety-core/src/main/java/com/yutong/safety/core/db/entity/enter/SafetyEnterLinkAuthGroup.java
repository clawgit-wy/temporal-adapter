package com.yutong.safety.core.db.entity.enter;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class SafetyEnterLinkAuthGroup implements Serializable {

    @ApiModelProperty("物流车备案权限ID")
    private Integer authId;
    @ApiModelProperty("组织结构编码")
    private String groupCode;
}
