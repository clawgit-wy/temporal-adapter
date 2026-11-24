package com.yutong.safety.common.param.web;

import lombok.Data;

/**
 * create by gwcuiyx on 2023.07.17
 */
@Data
public class RegionDeviceListParam extends WebPageParam {
    private String deviceNo;
    private String deviceName;
    private String factoryName;
    private String regionName;
    private Integer deviceType;
    private Integer factoryId;
}
