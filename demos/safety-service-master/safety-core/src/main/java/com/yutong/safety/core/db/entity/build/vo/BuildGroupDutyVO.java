package com.yutong.safety.core.db.entity.build.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 部门对应责任部门临时对象
 */
@Data
public class BuildGroupDutyVO implements Serializable {

    @ApiModelProperty("组织编码")
    private String code;

    @ApiModelProperty("组织名称")
    private String name;

    @ApiModelProperty("组织编码全路径")
    private String orgCodePath;

    @ApiModelProperty("责任部门编码全路径")
    private String dutyDeptCode;


    @ApiModelProperty("责任部门名称全路径")
    private String dutyDeptName;


}
