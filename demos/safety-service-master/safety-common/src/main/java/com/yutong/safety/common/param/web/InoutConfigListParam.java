package com.yutong.safety.common.param.web;

import lombok.Data;

/**
 * @author gwcuiyx
 * @date 2024-4-8 16:22
 */
@Data
public class InoutConfigListParam extends WebPageParam {
    /**
     * 员工号
     */
    private String empCode;
    /**
     * 姓名
     */
    private String empName;
    /**
     * 厂区
     */
    private Integer factoryId;
    /**
     * 权限类型
     */
    private String permissionType;
}
