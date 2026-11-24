package com.hik.worker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.List;

/**
 * 人员变更权限确认结果模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartConfirmResult implements Serializable {
    private String batchId;
    private int total;
    private int successCount;
    private int failCount;
    private List<StaffResult> details;
    private String status;
}
