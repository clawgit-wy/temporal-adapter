package com.yutong.modules.user.vo;

import com.yutong.modules.user.model.AuthItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("用户权限响应")
public class UserPermissionVO {

    @ApiModelProperty("所有权限(全部)")
    private List<AuthItem> allAuth;

    @ApiModelProperty("按钮/表单访问权限")
    private List<AuthItem> auth;

    @ApiModelProperty("菜单树")
    private List<MenuNodeVO> menu;

    @ApiModelProperty("系统安全模式")
    private Boolean sysSafeMode;

    @ApiModelProperty("集合列表")
    private List<String> codeList;

}
