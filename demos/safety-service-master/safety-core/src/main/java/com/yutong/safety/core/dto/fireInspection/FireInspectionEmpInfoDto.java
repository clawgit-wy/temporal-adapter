package com.yutong.safety.core.dto.fireInspection;

import lombok.Data;

/**
 * 巡检人信息
 */
@Data
public class FireInspectionEmpInfoDto {
    private String empNo;
    private String empName;
    private String orgCodePath;
    private String orgNamePath;
}
