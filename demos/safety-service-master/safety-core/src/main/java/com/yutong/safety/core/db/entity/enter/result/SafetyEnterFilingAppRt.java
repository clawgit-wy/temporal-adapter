package com.yutong.safety.core.db.entity.enter.result;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 物流车备案信息表
 * </p>
 *
 * @author 任昊翔
 * @since 2024-07-10
 */
@Data
public class SafetyEnterFilingAppRt implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    private String id;

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
     * 车主姓名/运输公司名称
     */
    private String fdDriverName;

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
     * 车牌类型
     */
    private String fdFilingCarType;

}
