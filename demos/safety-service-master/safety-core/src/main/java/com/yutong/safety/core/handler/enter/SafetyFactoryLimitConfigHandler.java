package com.yutong.safety.core.handler.enter;

import com.yutong.safety.core.db.entity.enter.SafetyFactoryLimitConfigEntity;

/**
 * @author caomg
 * @date 2024-9-10 10:59
 * @description SafetyFactoryLimitConfigHandler
 */
public interface SafetyFactoryLimitConfigHandler {
    //更新
    void saveOrUpdate(SafetyFactoryLimitConfigEntity en,String token);

    //定时任务
    void updateByTime();

    //查询
    SafetyFactoryLimitConfigEntity getData();
}
