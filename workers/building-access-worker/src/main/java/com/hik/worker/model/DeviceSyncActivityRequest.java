package com.hik.worker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

/**
 * 设备同步Activity请求（单页同步）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceSyncActivityRequest implements Serializable {
    /** 设备类型 */
    private String deviceType;
    /** 页码 */
    private int pageNo;
    /** 分页大小 */
    private int pageSize;
    /** 区域ID */
    private Integer areaId;
}
