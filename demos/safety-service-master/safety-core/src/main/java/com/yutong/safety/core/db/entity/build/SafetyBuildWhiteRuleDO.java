package com.yutong.safety.core.db.entity.build;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 楼宇门禁-白名单规则
 */
@Data
public class SafetyBuildWhiteRuleDO implements Serializable {

    private Integer id;

    @ApiModelProperty("岗位")
    private String postName;

    @ApiModelProperty("组织编码")
    private String orgCode;

    @ApiModelProperty("组织路径名称")
    private String orgNamePath;

    private String createUser;

    private String createTime;

    @ApiModelProperty("原因")
    private String remark;

    private String deleteFlag;

    private String updateTime;

    private String updateUser;

    private static final long serialVersionUID = 1L;

}