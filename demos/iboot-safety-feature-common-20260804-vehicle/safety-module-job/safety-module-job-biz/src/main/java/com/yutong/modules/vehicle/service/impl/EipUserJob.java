package com.yutong.modules.vehicle.service.impl;

import com.yutong.modules.vehicle.service.ISafetyEmployeeMessageJob;
import com.yutong.modules.vehicle.service.IUserJob;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EipUserJob implements IUserJob {

    @Autowired
    private ISafetyEmployeeMessageJob employeeMessageService;

    @Override
    public void tasksMessage(Boolean isAll, String dateTime) {
        if(StringUtils.isNotBlank(dateTime)){
            dateTime = dateTime+" 00:00:00";
        }
        employeeMessageService.tasksMessage(isAll,dateTime);
    }
}
