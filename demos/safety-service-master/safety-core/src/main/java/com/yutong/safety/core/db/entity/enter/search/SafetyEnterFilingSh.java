package com.yutong.safety.core.db.entity.enter.search;

import com.yutong.safety.common.param.Page;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

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
public class SafetyEnterFilingSh extends Page implements Serializable {

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
     * 入场类型
     */
    private String fdEnterType;

    /**
     * 车牌号
     */
    private String fdCarNo;

    public String getFdCarNo() {
        return fdCarNo;
    }

    public void setFdCarNo(String fdCarNo) {
        if(StringUtils.isNotBlank(fdCarNo)){
            fdCarNo = fdCarNo.replaceAll("\\s+", "");
        }
        this.fdCarNo = fdCarNo;
    }

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
     * 供应商名称
     */
    private String fdSupplierName;

    /**
     * 运输货物名称
     */
    private String fdGoodsName;

    /**
     * 货物装载量（吨）
     */
    private String fdGoodsWeight;

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
     * 操作方式
     */
    private String fdOperation;

    /**
     * 车主姓名/运输公司名称
     */
    private String fdDriverName;

    /**
     * 处理人员工号
     */
    private String handler;

    /**
     * 创建时间
     */
    private String createTimeBegin;
    private String createTimeEnd;

    private String isAdmin;

    //车辆类型
    private String fdFilingCarType;
    private String fdFilingCarTypeName;

    public String getCreateTimeBegin() {
        return createTimeBegin;
    }

    public void setCreateTimeBegin(String createTimeBegin) {
        if(StringUtils.isNotBlank(createTimeBegin)){
            createTimeBegin = createTimeBegin + " 00:00:00";
        }
        this.createTimeBegin = createTimeBegin;
    }

    public String getCreateTimeEnd() {
        return createTimeEnd;
    }

    public void setCreateTimeEnd(String createTimeEnd) {
        if(StringUtils.isNotBlank(createTimeEnd)){
            createTimeEnd = createTimeEnd + " 23:59:59";
        }
        this.createTimeEnd = createTimeEnd;
    }
}
