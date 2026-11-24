package com.yutong.safety.core.db.entity.enter;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 下发权限
 * </p>
 *
 * @author 任昊翔
 * @since 2024-07-10
 */
@Data
public class SafetyEnterCrk implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    /**
     * 人员信息
     */
    //private String fdName;
    //private String fdPhone;
    //private String fdImg;
    //private String fdCardId;
    /**
     * 审批人信息
     */
    private String operNo;
    private String operName;

    /**
     * 厂区
     * */
    private String factory;

    /**
     * 车牌号
     */
    private String fdCarNo;

    /**
     * 车辆归属
     */
    private String fdCarBelong;

    /**
     * 车主姓名/运输公司名称
     */
    private String fdDriverName;

    /**
     * 环保等级
     */
    private String fdProtectGrade;

    /**
     * 随车清单
     */
    private String fdDetailedImg;

    /**
     * VIN号
     */
    private String fdVin;

    /**
     * 发动机号码
     */
    private String fdEngine;

    /**
     * 行驶证正面照片
     */
    private String fdCarImg;

    /**
     * 行驶证注册日期
     */
    private String fdCarDate;

    /**
     * 当前是否在厂
     */
    private String fdInFactory;

    private String  beginTime;
    private String  endTime;

    private List<SafetyEnterFilingPer> perList;
}
