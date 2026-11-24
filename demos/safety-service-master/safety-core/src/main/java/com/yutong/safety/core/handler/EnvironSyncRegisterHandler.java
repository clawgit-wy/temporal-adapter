package com.yutong.safety.core.handler;

import com.yutong.safety.core.db.entity.enter.EnterFactoryRecord;

import java.util.List;

public interface EnvironSyncRegisterHandler {


    void syncEnvironRegister(List<EnterFactoryRecord> factoryRecords, String sessionKey);
}
