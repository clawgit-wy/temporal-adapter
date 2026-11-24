package com.hik.worker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

/**
 * 海康数据同步请求模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HkSyncRequest implements Serializable {
    /** 同步类型: INCREMENTAL=增量同步, FULL=全量同步 */
    private String syncType;
    /** 时间窗口(分钟), 增量同步时使用, 默认123 */
    private int timeWindowMinutes;
    /** 自定义开始时间 (格式: yyyy-MM-dd HH:mm:ss), 可选 */
    private String customStartTime;
    /** 自定义结束时间 (格式: yyyy-MM-dd HH:mm:ss), 可选 */
    private String customEndTime;
    /** 批量大小, 默认900 */
    private int batchSize;
    /** 是否并行下载图片 */
    private boolean parallelImageDownload;
    /** 最大并行图片下载数 */
    private int maxParallelImages;
}
