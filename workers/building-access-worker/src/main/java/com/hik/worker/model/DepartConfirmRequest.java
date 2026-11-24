package com.hik.worker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.List;

/**
 * 人员变更权限确认请求模型
 * 员工部门变动时批量处理门禁权限
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartConfirmRequest implements Serializable {
    /** 批次ID */
    private String batchId;
    /** 操作类型: RETAIN=保留权限, CANCEL=取消权限 */
    private String operationType;
    /** 变更人员列表 */
    private List<StaffInfo> staffList;
    /** 设备类型: FUJI / XINKAIPU */
    private String deviceType;
    /** 授权设备编号列表 */
    private List<Integer> authorizeDeviceNo;
    /** 授权设备列表（卡设备） */
    private List<String> authDevice;
    /** 是否跨部门确认 */
    private boolean crossDepartment;
    /** 原责任部门编码 */
    private String originalDeptCode;
    /** 新责任部门编码 */
    private String newDeptCode;
    /** 原保密负责人员工号 */
    private String originalEmpCode;
    /** 新保密负责人员工号 */
    private String newEmpCode;
}
