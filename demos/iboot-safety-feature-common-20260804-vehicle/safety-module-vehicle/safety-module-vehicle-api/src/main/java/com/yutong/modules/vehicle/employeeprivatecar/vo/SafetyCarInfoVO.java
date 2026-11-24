package com.yutong.modules.vehicle.employeeprivatecar.vo;

import lombok.Data;

@Data
public class SafetyCarInfoVO {
    /**
     * 员工号
     */
    private String empNo;

    /**
     * 员工车辆，多个用逗号分隔
     */
    private String carInfos;

    /**
     * 锁定车辆，多个用逗号分隔
     */
    private String lockCars;
}
