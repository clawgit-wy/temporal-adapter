package com.yutong.safety.common.param.web.sys;

import com.yutong.safety.common.param.Check;
import lombok.Data;

import java.util.Date;

/**
 * create by gwcuiyx on 2023.07.27
 */
@Data
public class SysRoleParam implements Check {

    /**
     * 数据id
     */
    private Integer id;

    /**
     * 角色名
     */
    private String roleName;

    /**
     * 菜单ids
     */
    private String permIds;

//    /**
//     * 角色描述
//     */
//    private String roleDesc;
//
//    /**
//     * 操作人
//     */
//    private String lastUser;
//
//    /**
//     * 操作时间
//     */
//    private Date lastTime;
//
//    /**
//     * 有效状态（0正常1删除）
//     */
//    private String deleteFalg;

    @Override
    public void check() {

    }
}
