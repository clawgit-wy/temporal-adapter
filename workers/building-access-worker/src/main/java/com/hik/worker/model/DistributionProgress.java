package com.hik.worker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

/**
 * 工作流进度查询结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistributionProgress implements Serializable {
    /** 已处理数 */
    private int processed;
    /** 总数 */
    private int total;
    /** 成功数 */
    private int successCount;
    /** 失败数 */
    private int failCount;
    /** 进度百分比 */
    private int progressPercent;
    /** 当前状态 */
    private String status;
    /** 当前处理人员工号 */
    private String currentStaffNo;
}
