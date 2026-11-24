package com.yutong.modules.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="任务对象", description="任务对象")
public class TaskUserQueryDTO {

    @ApiModelProperty(value = "厂区")
    private String factoryId;

    @ApiModelProperty(value = "组织code")
    private String orgCode;

    @ApiModelProperty(value = "name or empno")
    private String empNameORempNo;

    @ApiModelProperty(value = "岗位编码")
    private String roleCode;


}
