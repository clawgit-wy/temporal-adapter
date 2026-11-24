package com.yutong.safety.core.db.entity.enter.result;

import com.yutong.safety.core.db.entity.enter.SafetyEnterFilingDock;
import com.yutong.safety.core.db.entity.enter.SafetyEnterFilingPer;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 物流车备案信息表
 * </p>
 *
 * @author 任昊翔
 * @since 2024-07-10
 */
@Data
public class SafetyEnterFilingInfoRt implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    private String id;

    private String fdKey;

    /**
     * 状态编号
     */
    private String fdStateCode;

    /**
     * 状态
     */
    private String fdState;

    /**
     * 备案类型
     */
    private String fdFilingTypeCode;
    private String fdFilingType;

    /**
     * 入场类型
     */
    private String fdEnterTypeCode;
    private String fdEnterType;

    /**
     * 车牌号
     */
    private String fdCarNo;

    /**
     * 备案厂区编号
     */
    private String fdFilingFactoryCode;

    /**
     * 备案厂区
     */
    private String fdFilingFactory;

    /**
     * 备案时间-始
     */
    private String fdFilingBegin;

    /**
     * 备案时间-终
     */
    private String fdFilingEnd;

    /**
     * 供应商名称
     */
    private String fdSupplierName;

    /**
     * 供应商代码
     */
    private String fdSupplierCode;

    /**
     * 运输货物名称
     */
    private String fdGoodsName;

    /**
     * 货物装载量（吨）
     */
    private String fdGoodsWeight;

    /**
     * 备注
     */
    private String fdRemark;

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

    /** 货物更新时间 */
    private String goodsUpdateTime;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 审批后的数据
     */
    private String approveFilingFactoryCode;
    private String approveFilingFactory;
    private String approveFilingBegin;
    private String approveFilingEnd;

    // 接口人信息
    private List<SafetyEnterFilingDock> dockList;
    // 备案人员信息
    private List<SafetyEnterFilingPer> perList;

    /**
     * 车牌类型
     */
    private String fdFilingCarType;
    private String fdFilingCarTypeName;
}
