package com.hik.worker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.List;

/**
 * 权限下发结果模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionDistributionResult implements Serializable {
    private String processId;
    private String batchId;
    private int total;
    private int successCount;
    private int failCount;
    private List<StaffResult> details;
    /** 状态: COMPLETED / PARTIAL_FAILED / FAILED / SUSPENDED */
    private String status;
    /** 失败人员列表（用于重试） */
    private List<String> failedStaffNos;
    /** 下发日志ID列表 */
    private List<Integer> issuedLogIds;
}
