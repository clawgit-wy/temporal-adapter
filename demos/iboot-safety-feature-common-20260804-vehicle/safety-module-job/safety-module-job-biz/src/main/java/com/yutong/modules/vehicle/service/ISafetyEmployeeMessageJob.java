package com.yutong.modules.vehicle.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yutong.modules.entity.SafetyEmployeeMessage;

/**
 * @Description: 人员信息表
 * @Author: jeecg-boot
 * @Date:   2026-08-10
 * @Version: V1.0
 */
public interface ISafetyEmployeeMessageJob extends IService<SafetyEmployeeMessage> {

    /**
     * 同步人员信息
     * @param isAll
     * @param dateTime
     */
   public void tasksMessage(Boolean isAll, String dateTime);
}
