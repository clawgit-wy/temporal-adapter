package com.hik.adapter.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 批量校验最终结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchResult implements Serializable {

    /** 批次ID */
    private String batchId;

    /** 总数 */
    private int total;

    /** 成功数 */
    private int successCount;

    /** 失败数 */
    private int failCount;

    /** 每个人的详细结果 */
    private List<PersonResult> details;

    /** 处理状态: COMPLETED / PARTIAL_FAILED / TIMEOUT */
    private String status;
}
