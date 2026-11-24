package com.yutong.safety.core.db.entity.build;

import lombok.Data;

import java.io.Serializable;

@Data
public class SafetyBuildAutoDeptDO implements Serializable {

    private String id;

    /**
     * 授权门禁设备编码
     */
    private String deviceCode;

    /**
     * 授权门禁设备名称
     */
    private String deviceName;

    /**
     * 授权部门编码
     */
    private String deptCode;

    /**
     * 授权部门名称
     */
    private String deptName;

    /**
     * 创建人员工号
     */
    private String createEmployeeNo;

    /**
     * 创建人登录名
     */
    private String createEmployeeLogin;

    /**
     * 创建人姓名
     */
    private String createEmployeeName;

    /**
     * 创建人部门编码
     */
    private String createDeptCode;

    private String createDeptName;

    /**
     * 对接部门编码
     */
    private String dockingDeptCode;

    /**
     * 对接部门明细
     */
    private String dockingDeptName;

    /**
     * 开始时间
     */
    private String beginTime;

    /**
     * 截止时间
     */
    private String endTime;

    private String createTime;

    private String updateTime;

    private String updateBy;

    /**
     * 对应流程ID
     */
    private String processId;

    /**
     * 对应BPM流程ID
     */
    private String bpmFlowId;

    /**
     * 是否终止 0-正常 1-终止
     */
    private String autoFlag;

    /**
     * 公共区域门禁编码
     */
    private String publicDeviceCode;

    /**
     * 公告呢区域门禁名称
     */
    private String publicDeviceName;

    private Integer deleteFlag;

    /**
     * 生效日期
     */
    private String effectiveDate;

    //事由备注
    private String remark;

    private static final long serialVersionUID = 1L;


}