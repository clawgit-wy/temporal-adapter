package com.yutong.safety.core.db.entity.enter;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 物流车备案信息表 接口人
 * </p>
 *
 * @author 任昊翔
 * @since 2024-07-10
 */
@Data
public class SafetyEnterFilingDock implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * 最大授权天数
     */
    private Integer dayNum;

    /**
     * 可备案厂区
     */
    private String fdFactoryCode;
    private String fdFactory;
    private List factoryList;
    private List perTypeList;
}
