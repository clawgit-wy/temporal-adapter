package com.yutong.modules.auth.api;


import org.jeecg.common.system.vo.LoginUser;

public interface UserAuthDataApi {
    String queryUserDataRule(String permCode);

    /**
     * 获取登录人信息
     * @param empNo
     * @return
     */
    public LoginUser queryLoginUser(String empNo);
}
