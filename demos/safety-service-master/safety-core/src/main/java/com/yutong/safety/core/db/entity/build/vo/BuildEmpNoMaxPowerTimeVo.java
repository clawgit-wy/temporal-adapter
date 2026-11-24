package com.yutong.safety.core.db.entity.build.vo;

import lombok.Data;

@Data
public class BuildEmpNoMaxPowerTimeVo {

    /**
     * 员工号
     */
    private String empNo;

    /**
     * 楼栋编码
     */
    private String buildingCode;

    /**
     * 最大结束时间
     */
    private String maxEndTime;
}
