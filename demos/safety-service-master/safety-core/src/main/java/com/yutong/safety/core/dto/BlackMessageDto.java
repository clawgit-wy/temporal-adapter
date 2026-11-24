package com.yutong.safety.core.dto;

import lombok.Data;

@Data
public class BlackMessageDto {
    /**
     * 身份证号
     */
    private String idCardNo;
    /**
     * 员工类型
     */
    private String positionType;
    /**
     * 任职部门
     */
    private String orgNamePath;
    /**
     * 任职部门编码
     */
    private String orgCodePath;
}
