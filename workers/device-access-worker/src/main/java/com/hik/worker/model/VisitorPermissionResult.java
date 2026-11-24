package com.hik.worker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.List;

/**
 * 访客权限下发结果模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitorPermissionResult implements Serializable {
    private String batchId;
    private int total;
    private int successCount;
    private int failCount;
    private List<StaffResult> details;
    private String status;
    /**
     * 已下发厂区列表
     */
    private List<String> issuedFactoryIds;
    /**
     * 未下发厂区列表（涉密排除的厂区）
     */
    private List<String> unissuedFactoryIds;
}
