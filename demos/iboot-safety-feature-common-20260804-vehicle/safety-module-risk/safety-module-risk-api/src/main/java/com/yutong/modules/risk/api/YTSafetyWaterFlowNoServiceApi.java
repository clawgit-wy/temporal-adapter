package com.yutong.modules.risk.api;

import java.util.Date;

public interface YTSafetyWaterFlowNoServiceApi {

    public Long autoIncrementKey(String type);

    public String getNowByType(String type);

    public String getNowByTypeDay(String type, Date curDate);

    public String getNowByTypeDayNum(String type, Date curDate);

    public String getNowByTypeNoDay(String type);

}
