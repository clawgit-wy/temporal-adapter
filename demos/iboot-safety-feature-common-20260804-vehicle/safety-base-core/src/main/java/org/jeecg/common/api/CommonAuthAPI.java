package org.jeecg.common.api;

import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysPermissionDataRuleModel;
import org.jeecg.common.system.vo.SysUserCacheInfo;
import org.apache.shiro.authc.AuthenticationException;

import java.util.List;
import java.util.Set;

/**
 * @description:
 * @author: liuky
 * @createDate: 2023/4/19
 * @version: 1.0
 */
public interface CommonAuthAPI {

    /**
     * 1查询用户角色信息
     * @param username
     * @return
     */
    Set<String> queryUserRoles(String username);

    /**
     * 2查询用户权限信息
     * @param username
     * @return
     */
    Set<String> queryUserAuths(String username);

    /**
     * 5根据用户账号查询用户信息
     * @param username
     * @return
     */
    LoginUser getUserByName(String username);

    /**
     * 8查询数据权限
     * @param component 组件
     * @param username 用户名
     * @param requestPath 前段请求地址
     * @return
     */
    List<SysPermissionDataRuleModel> queryPermissionDataRule(String component, String requestPath, String username);

    /**
     * 9查询用户信息
     * @param username
     * @return
     */
    SysUserCacheInfo getCacheUser(String username);

    LoginUser getLoginUser(String username);

    /**
     * 校验token的有效性
     *
     * @param token
     */
    LoginUser checkUserTokenIsEffect(String token) throws AuthenticationException;

}
