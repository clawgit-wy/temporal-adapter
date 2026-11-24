package com.yutong.safety.core.db.entity.login;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class UserRoleMenu implements Serializable {
    /*菜单id*/
    private Integer id;
    /*菜单名称*/
    private String title;
    /*菜单url*/
    private String url;
    /*父菜单id*/
    private Integer pid;
    /*类型（1目录2页面菜单3按钮）*/
    private Integer cate;
    /*code值*/
    private String code;
    /*子菜单集合*/
    private List<UserRoleMenu> children;
}
