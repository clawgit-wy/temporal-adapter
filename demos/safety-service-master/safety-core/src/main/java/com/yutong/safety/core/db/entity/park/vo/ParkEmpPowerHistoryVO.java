package com.yutong.safety.core.db.entity.park.vo;

import com.alibaba.fastjson.JSONArray;
import com.yutong.safety.core.db.entity.park.SafetyParkEmployeePowerHistoryDO;
import lombok.Data;

@Data
public class ParkEmpPowerHistoryVO extends SafetyParkEmployeePowerHistoryDO {
    private String createTimeStr;
    private String updateTimeStr;
    private String detailsStr;
    private JSONArray detailArray;
}
