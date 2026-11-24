package com.yutong.safety.core.db.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SafetyBlackMessageDO implements Serializable {

    private Integer id;

    private String companyName;

    private String empName;

    private String cardNo;

    private String phoneNo;

    private String idCardNo;

    private String empType;

    private String carNo;

    private String operator;

    private Date createTime;

    private Date updateTime;

    private String remark;

    private Boolean deleteFalg;

    private String source;

    private Integer state;
    /**
     * 人员信息名称，对应中台的empType字段
     */
    private String positionType;
    /**
     * 人员信息名称，对应中台的empTypeCode字段
     */
    private String positionCode;

    /**
     * 特批授权截止日期
     */
    private String authorizationDate;

    /**
     * 证件照(数据库字段)
     */
    private String idPhoto;

    /**
     * 生活照(数据库字段)
     */
    private String lifePhoto;

    /**
     * 照片（展示字段）
     */
    private String photo;

    /**
     * 照片类型（展示字段）
     */
    private String photoType;

    private static final long serialVersionUID = 1L;

}