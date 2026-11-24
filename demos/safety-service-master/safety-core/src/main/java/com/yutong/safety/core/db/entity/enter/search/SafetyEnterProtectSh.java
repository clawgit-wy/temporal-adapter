package com.yutong.safety.core.db.entity.enter.search;

import com.yutong.safety.common.param.Page;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 物流车环保信息
 * </p>
 *
 * @author 任昊翔
 * @since 2024-07-10
 */
@Data
public class SafetyEnterProtectSh extends Page implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * VIN号
     */
    private String fdVin;

    /**
     * 发动机号码
     */
    private String fdEngine;

    /**
     * 行驶证注册日期
     */
    private String fdCarDate;

    /**
     * 当前是否在厂
     */
    private String fdInFactory;

    private String idStr;

    private String id;

    private String enterTimeBegin;
}
