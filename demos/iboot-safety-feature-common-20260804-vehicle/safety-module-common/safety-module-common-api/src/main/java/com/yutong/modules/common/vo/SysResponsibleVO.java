package com.yutong.modules.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="安全责任人人员对象", description="安全责任人人员对象")
public class SysResponsibleVO {

    @ApiModelProperty(value = "姓名")
    private String empName;

    @ApiModelProperty(value = "员工号")
    private String empNo;

    @ApiModelProperty(value = "登录账号")
    private String userName;
}
