package com.hik.worker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

/**
 * 设备数据同步请求模型
 * 从第三方门禁系统全量/增量同步设备数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceSyncRequest implements Serializable {
    /** 同步类型: FULL=全量同步, INCREMENTAL=增量同步 */
    private String syncType;
    /** 设备类型: FUJI=富士, XINKAIPU=新开普 */
    private String deviceType;
    /** 区域ID（可选，指定区域同步） */
    private Integer areaId;
    /** 分页大小 */
    private int pageSize;
    /** 并行页数 */
    private int parallelPages;
    /** 增量同步起始时间（INCREMENTAL模式使用） */
    private String syncStartTime;
}
