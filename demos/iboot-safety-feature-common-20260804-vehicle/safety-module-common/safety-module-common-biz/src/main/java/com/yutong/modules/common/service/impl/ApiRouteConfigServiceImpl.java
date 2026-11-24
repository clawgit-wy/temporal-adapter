package com.yutong.modules.common.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yutong.modules.common.entity.YtsafetyApiRouteConfig;
import com.yutong.modules.common.mapper.YtsafetyApiRouteConfigMapper;
import com.yutong.modules.common.api.IApiRouteConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApiRouteConfigServiceImpl extends ServiceImpl<YtsafetyApiRouteConfigMapper, YtsafetyApiRouteConfig> implements IApiRouteConfigService {

    @Autowired
    private YtsafetyApiRouteConfigMapper  apiRouteConfigMapper;

    @Override
    public YtsafetyApiRouteConfig queryApiRouteConfigByCode(String routeCode) {
        return apiRouteConfigMapper.queryApiRouteConfig(routeCode);
    }
}
