package com.hik.worker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.List;

/**
 * 设备数据同步结果模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceSyncResult implements Serializable {
    /** 同步类型 */
    private String syncType;
    /** 设备类型 */
    private String deviceType;
    /** 总设备数 */
    private int totalDevices;
    /** 新增设备数 */
    private int newDevices;
    /** 更新设备数 */
    private int updatedDevices;
    /** 移除设备数 */
    private int removedDevices;
    /** 失败数 */
    private int failCount;
    /** 状态: COMPLETED / PARTIAL_FAILED / FAILED */
    private String status;
    /** 新增设备编码列表（用于通知） */
    private List<String> newDeviceCodes;
    /** 错误信息 */
    private String errorMessage;
}
