package com.yutong.hksync.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 海康中间库数据传输对象
 */
@Data
public class HkMiddleTableVO {

    /** 组织ID */
    private String orgId;

    /** 员工号 */
    private String jobNo;

    /** 证件号 */
    private String certificateNo;

    /** 状态 0-正常 -1-禁用 */
    private Integer status = 0;

    /** 更新时间 */
    private LocalDateTime updateTimeStr = LocalDateTime.now();

    /** 姓名 */
    private String name;

    /** 人员类型 */
    private Integer personType = 1;

    /** 性别 0-未知 1-男 2-女 */
    private Integer sex;

    /** 手机号 */
    private String mobile;

    /** 证件类型 */
    private Integer certType = 111;

    /** 时间 */
    private LocalDate timeStr;

    /** 岗位组织ID */
    private String positionOrgId;

    /** 组织名称 */
    private String orgName;

    /** 父级组织ID */
    private String parentOrgId;

    /** 人脸数据(Base64) */
    private String faceData;

    /** 人脸URL */
    private String faceUrl;
}
