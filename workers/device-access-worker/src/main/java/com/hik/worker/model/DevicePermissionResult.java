package com.hik.worker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.List;

/**
 * 统一设备权限下发结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DevicePermissionResult implements Serializable {
    private String batchId;
    private int total;
    private int successCount;
    private int failCount;
    private List<StaffResult> details;
    private String status;
}
