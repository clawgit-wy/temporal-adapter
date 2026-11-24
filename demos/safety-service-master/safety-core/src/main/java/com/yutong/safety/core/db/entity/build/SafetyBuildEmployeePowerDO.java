package com.yutong.safety.core.db.entity.build;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SafetyBuildEmployeePowerDO implements Serializable {

    private String id;

    /**
     * 员工号
     */
    private String employeeCode;

    /**
     * 员工姓名
     */
    private String employeeName;

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 生效时间
     */
    private String beginTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 来源类型 1-流程 2-人员调整
     */
    private String sourceType;

    /**
     * 流程ID
     */
    //private String targetId;
    private String processId;

    private String bpmFlowId;

    private String remark;

    /**
     * 创建人
     */
    private String createBy;

    private Date createTime;

    private Date updateTime;

    /**
     * 流程提交时的 责任部门
     */
    private String dutyDeptCode;

    /**
     * 流程提交时的 责任部门
     */
    private String dutyDeptName;

    /**
     * 本部门节点审批人或管理员 登录名
     */
    private String dutyEmpLogin;
    /**
     * 本部门节点审批人或管理员 姓名
     */
    private String dutyEmpName;
    /**
     * 本部门节点审批人或管理员 员工号
     */
    private String dutyEmpNo;

    /**
     * 相关流程的角色类型 1-员工 2-管理员 3-绝密负责人
     */
    private String roleType;

    /**
     * 删除标记 0-正常 1-异常
     */
    private String deleteFlag;

    private Date deleteTime;
    private Date departChangeTime;

    /**
     * 关闭的流程ID
     */
    private String deleteProcessId;

    private String deviceType;  //1-新开普 2-富士

    private static final long serialVersionUID = 1L;
}