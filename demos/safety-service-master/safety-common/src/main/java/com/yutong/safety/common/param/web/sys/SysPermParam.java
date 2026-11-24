package com.yutong.safety.common.param.web.sys;

import com.yutong.safety.common.param.Check;
import lombok.Data;

/**
 * create by gwcuiyx on 2023.07.26
 */
@Data
public class SysPermParam implements Check {

    private Integer id;

    /**
     * 权限名称
     */
    private String permName;

    /**
     * 权限访问路径
     */
    private String permUrl;

    /**
     * 父ID
     */
    private Integer parentId;

    /**
     * 目录层级
     */
    private Integer level;

    /**
     * 类型（1目录2页面菜单3按钮）
     */
    private Integer cate;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 是否系统菜单（1普通菜单，2开发使用）
     */
    private String sys;

    /**
     * 权限表达式
     */
    private String permission;

    /**
     * 权限类型（按角色，按权限表达式）
     */
    private String permCate;

    /**
     * 有效状态（0正常1删除）
     */
    private String deleteFalg;

    /**
     * 操作人
     */
    private String lastUser;

    @Override
    public void check() {

    }
}
