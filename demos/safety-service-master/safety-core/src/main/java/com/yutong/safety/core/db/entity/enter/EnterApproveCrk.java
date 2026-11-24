package com.yutong.safety.core.db.entity.enter;

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
public class EnterApproveCrk implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    private String fdFilingId;

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
     * 审批人信息
     */
    private String operNo;
    private String operName;
    private String operPhone;

    private String createTime;
}
