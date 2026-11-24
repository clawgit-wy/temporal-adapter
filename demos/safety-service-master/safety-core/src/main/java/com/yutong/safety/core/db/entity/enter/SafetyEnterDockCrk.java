package com.yutong.safety.core.db.entity.enter;

import io.swagger.annotations.ApiModelProperty;
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
public class SafetyEnterDockCrk implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String fdKey;

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
    private String beginTime;
    private String endTime;
    private String operNo;
    private String operName;
    private Integer dockNum;
    private String fdStateCode;
    private String flowId;
    //接口人员工号
    private String fdDockNo;
    //接口人姓名
    private String fdDockName;
    //入厂司机
    private String enterDrvierName;
    //入厂司机电话
    private String enterDrvierPhone;
    private String fdFilingCarType;
    private String fdFilingCarTypeName;

    //备案开始时间
    private String filingBeginDate;
    //备案结束时间
    private String filingEndDate;

    @ApiModelProperty(value = "供应商代码")
    private String providerCode;
    @ApiModelProperty(value = "供应商名称", required = true)
    private String providerName;

    private List<EnterApproveCrk> approveList;

    private List<SafetyEnterFilingPer> perList;
}
