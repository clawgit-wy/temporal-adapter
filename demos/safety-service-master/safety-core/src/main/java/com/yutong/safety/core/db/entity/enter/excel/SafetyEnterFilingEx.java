package com.yutong.safety.core.db.entity.enter.excel;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 物流车备案信息表
 * </p>
 *
 * @author 任昊翔
 * @since 2024-07-10
 */
@Data
public class SafetyEnterFilingEx implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态
     */
    private String fdState;

    /**
     * 备案类型
     */
    private String fdFilingType;

    /**
     * 车牌号
     */
    private String fdCarNo;

    /**
     * 车主姓名/运输公司名称
     */
    private String fdDriverName;

    /**
     * 备案厂区
     */
    private String fdFilingFactory;

    /**
     * 备案时间-始
     */
    private Date fdFilingBegin;

    /**
     * 备案时间-终
     */
    private Date fdFilingEnd;

    /**
     * 人员类型
     */
    private String fdPerType;

    /**
     * 姓名
     */
    private String fdPerName;

    /**
     * 身份证号
     */
    private String fdPerCard;

    /**
     * 手机号
     */
    private String fdPerPhone;

    /**
     * 证件照
     */
    private String fdPerImg;

    /**
     * 环保等级
     */
    private String fdProtectGrade;

    /**
     * 随车清单
     */
    private String fdDetailedImg;

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
     * 行驶证正面照片
     */
    private String fdCarImg;

    /**
     * 接口人员工号
     */
    private String fdDockNo;

    /**
     * 接口人姓名
     */
    private String fdDockName;

    /**
     * 接口人部门
     */
    private String fdDockDepartment;

    /**
     * 接口人电话
     */
    private String fdDockPhone;

    /**
     * 备注
     */
    private String fdRemark;
}
