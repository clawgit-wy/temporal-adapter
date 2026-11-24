package com.hik.worker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

/**
 * Demo 工作流结果模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemoResult implements Serializable {
    /** 是否成功 */
    private boolean success;
    /** 消息/汇总报告 */
    private String message;
    /** 处理数量 */
    private int processedCount;
    /** mokapi 接口响应 */
    private String apiResponse;
    /** 执行时长(毫秒) */
    private long duration;
}
