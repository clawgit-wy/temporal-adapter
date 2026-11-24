package com.yutong.safety.core.dto;

import lombok.Data;

/**
 *关闭楼栋公共门禁的实体类
 */
@Data
public class SafetyClosePublicBuildDto {
    /**
     * 员工号
     */
    private String empNo;
    /**
     * 楼栋编码
     */
    private String buildingCode;
    /**
     * 是否是公共区域 0-否 1-是
     */
    private String isPublic;
    /**
     * 员工楼宇门禁权限信息表的数据行ID
     */
    private String ids;

    /**
     * 设备ID
     */
    private String deviceCode;

    /**
     * 设备名称
     */
    private String deviceName;
}
