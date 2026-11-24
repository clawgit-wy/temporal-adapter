package com.yutong.safety.core.db.entity.fireInspection.excel;

import lombok.Data;

@Data
public class SafetyFireInspectionBaseExcel {
    /**
     * 编号
     */
    private Integer num;


    /**
     * 异常信息
     */
    public String failInfo;
}
