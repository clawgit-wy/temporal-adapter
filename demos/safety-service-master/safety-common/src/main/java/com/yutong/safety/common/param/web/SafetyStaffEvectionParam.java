package com.yutong.safety.common.param.web;

import lombok.Data;

/**
 * @author lukunpeng
 * @since 2024-04-11 10:47
 */
@Data
public class SafetyStaffEvectionParam {

    private String beginDate;
    private String endDate;
    private String staffNo;
    private String staffName;
    private String remark;
    private int pageNum;
    private int pageSize;
}
