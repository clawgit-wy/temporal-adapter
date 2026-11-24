package com.yutong.safety.core.db.entity.build.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 部门对应责任部门临时对象包含门禁ID
 */
@Data
public class BuildGroupDutyEmpNoVO implements Serializable {


    /**
     * 楼宇，门禁设备ID
     */
    private String deviceCode;
    /**
     * 组织编码
     */
    private String code;

    /**
     * 组织名称
     */
    private String name;

    /**
     * 组织编码全路径
     */
    private String orgCodePath;

    /**
     * 责任部门编码全路径
     */
    private String dutyDeptCode;

    /**
     * 责任部门名称全路径
     */
    private String dutyDeptName;
}
