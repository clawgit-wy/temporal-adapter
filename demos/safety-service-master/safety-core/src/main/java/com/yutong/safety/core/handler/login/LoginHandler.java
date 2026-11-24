package com.yutong.safety.core.handler.login;

import com.yutong.safety.core.db.entity.SafetySysRoleDO;
import com.yutong.safety.core.service.login.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * create gwcuiyx 2023.08.07
 * 登录用户--handler
 */
@Component
public class LoginHandler {
    @Autowired
    private LoginService loginService;

    /**
     * 查询用户拥有的权限资源
     * @return
     */
    public Object selectMainMenuList(String tocken) {
        return loginService.selectMainMenuList(tocken);
    }

    /**
     * 根据员工号查询角色清单
     * @param empNo 员工号
     * @return 角色清单
     */
    public List<SafetySysRoleDO> getRoleList(String empNo){
        return loginService.getRoleList(empNo);
    }
}
