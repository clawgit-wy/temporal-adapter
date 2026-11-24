package com.yutong.safety.core.db.entity.enter;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 物流车环保信息
 * </p>
 *
 * @author 任昊翔
 * @since 2024-07-10
 */
@Data
public class SafetyEnterProtect implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    private Integer id;

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

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人
     */
    private String createUser;


}
