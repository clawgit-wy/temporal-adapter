package com.hik.worker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

/**
 * 海康数据同步结果模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HkSyncResult implements Serializable {
    /** 同步类型 */
    private String syncType;
    /** 黑名单数据数 */
    private int blackListCount;
    /** 员工台账数据数 */
    private int employeeCount;
    /** 外来人员备案数据数 */
    private int registerCount;
    /** 访客数据数 */
    private int visitorCount;
    /** 过滤后总数据数 */
    private int totalAfterFilter;
    /** 去重后总数据数 */
    private int totalAfterDedup;
    /** 成功写入数 */
    private int successCount;
    /** 失败数 */
    private int failCount;
    /** 状态: COMPLETED / PARTIAL_FAILED / FAILED */
    private String status;
    /** 错误信息 */
    private String errorMessage;
}
