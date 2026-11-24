package com.yutong.safety.core.db.entity.enter;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 物流车备案信息表-人员记录
 * </p>
 *
 * @author 任昊翔
 * @since 2024-07-10
 */
@Data
public class SafetyEnterFilingPerHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    private Integer id;

    /**
     * 主表ID
     */
    private Integer filingId;

    /**
     * 人员类型编号
     */
    private String fdPerTypeCode;

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

}
