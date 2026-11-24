package com.yutong.safety.common.param.web;

import com.yutong.safety.common.param.Check;
import lombok.Data;

/**
 * create by gwcuiyx on 2023.07.17
 */
@Data
public class EmpowerDeviceAddParam implements Check {
    private Integer factoryId;
    private String factoryName;
    private String regionName;
    private String empowerType;
    private Integer regionId;
    private String empowerNo;
    private String remark;

    @Override
    public void check() {

    }
}
