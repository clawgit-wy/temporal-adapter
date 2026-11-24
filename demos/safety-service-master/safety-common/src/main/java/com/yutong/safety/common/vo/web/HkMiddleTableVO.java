package com.yutong.safety.common.vo.web;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 海康中间库
 * 包含，人员信息表、人脸表、组织表  所有字段
 */
@Data
public class HkMiddleTableVO {

    /**
     * 人员信息表 组织表 通用字段 组织ID
     */
    private String orgId;

    /**
     * 人员信息表 人脸表 通用字段
     */
    private String jobNo;
    private String certificateNo;

    /**
     * 通用字段
     */
    private Integer status = 0;
    private LocalDateTime updateTimeStr = LocalDateTime.now();

    /**
     * 人员信息表
     */
    private String name;
    private Integer personType = 1;
    private Integer sex;
    private String mobile;
    private Integer certType = 111;
    private LocalDate timeStr;
    private String positionOrgId;

    /**
     * 组织表
     */
    private String orgName;
    private String parentOrgId;

    /**
     * 人脸表
     */
    private String faceData;
    private String faceUrl;

}
