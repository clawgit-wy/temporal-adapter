package com.yutong.safety.common.vo.login;

import lombok.Data;

@Data
public class UserRoleMenuVO {
    /*菜单id*/
    private Integer id;
    /*父菜单id*/
    private Integer pid;
    /*菜单名称*/
    private String title;
    /*菜单url*/
    private String url;
    /*目录层级*/
    private Integer level;
    /*类型（1目录2页面菜单3按钮）*/
    private Integer cate;
    /*code值*/
    private String code;
}
