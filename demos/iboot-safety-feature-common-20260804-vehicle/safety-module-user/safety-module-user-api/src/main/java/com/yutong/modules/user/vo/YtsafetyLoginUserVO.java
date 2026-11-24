package com.yutong.modules.user.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="登录人员信息", description="登录人员信息")
public class YtsafetyLoginUserVO {
    /**用户主键*/
    @ApiModelProperty(value = "用户主键")
    private Long userId;
    /**员工号*/
    @ApiModelProperty(value = "员工号")
    private String empNo;
    /**员工名*/
    @ApiModelProperty(value = "员工名")
    private String realName;
    /**EIP用户名*/
    @ApiModelProperty(value = "EIP用户名")
    private String eipName;
    /**手机号*/
    @ApiModelProperty(value = "手机号")
    private String phone;
    /**组织机构*/
    @ApiModelProperty(value = "组织机构")
    private String orgName;
    /**岗位*/
    @ApiModelProperty(value = "岗位")
    private String positionName;
    /**邮箱*/
    @ApiModelProperty(value = "邮箱")
    private String email;
}
