package com.yutong.safety.core.db.entity.enter;

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
public class SafetyEnterApprove implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    private String fdFilingId;

    /**
     * 审批类型（1审核通过；2驳回；3废弃）
     * */
    private Integer approveType;

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
    private Date fdFilingBegin;

    /**
     * 备案时间-终
     */
    private Date fdFilingEnd;

    /**
     * 处理人工号
     */
    private String approveCode;

    /**
     * 处理人姓名
     */
    private String approveName;

    //接口人部门
    private String approveDepart;

}
