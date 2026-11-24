package com.yutong.safety.common.param.web;

import lombok.Data;

/**
 * @author gwcuiyx
 * @date 2024-2-28 15:40
 */
@Data
public class ExternalDetectionListParam extends WebPageParam {
    /**
     * 员工号
     */
    private String empNo;
    /**
     * 员工姓名
     */
    private String empName;
}
