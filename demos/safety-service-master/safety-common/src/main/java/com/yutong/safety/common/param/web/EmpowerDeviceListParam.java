package com.yutong.safety.common.param.web;

import lombok.Data;

import java.util.List;

/**
 * create by gwcuiyx on 2023.07.17
 */
@Data
public class EmpowerDeviceListParam extends WebPageParam {
    private String factoryName;
    private String regionName;
    private String empowerType;
    private String empowerNo;
    private Integer factoryId;
    private String regionId;

    private List<Integer> factoryIdList;
}
