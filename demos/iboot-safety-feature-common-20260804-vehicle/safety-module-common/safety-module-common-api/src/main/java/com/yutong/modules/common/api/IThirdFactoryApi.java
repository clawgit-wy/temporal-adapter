package com.yutong.modules.common.api;

/**
 * 统一接口信息
 */
public interface IThirdFactoryApi {

    /**
     * 三方路由接口
     * @param routeCode
     * @param input
     * @return
     */
    public String executeMethod(String routeCode,Object input);

    /**
     * 推送宇信通
     * @return
     */
    public String pushYxtRebot(Object input);

    /**
     * 获取用户同步信息
     * @param input
     * @return
     */
    public String pushUserSyncInfo(Object input);
}
