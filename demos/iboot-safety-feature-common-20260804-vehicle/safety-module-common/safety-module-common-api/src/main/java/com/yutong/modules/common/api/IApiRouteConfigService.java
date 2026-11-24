package com.yutong.modules.common.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yutong.modules.common.entity.YtsafetyApiRouteConfig;

public interface IApiRouteConfigService extends IService<YtsafetyApiRouteConfig> {

    /**
     * 依据路由编码查询路由实体
     * @param routeCode
     * @return
     */
    public YtsafetyApiRouteConfig queryApiRouteConfigByCode(String routeCode);
}
