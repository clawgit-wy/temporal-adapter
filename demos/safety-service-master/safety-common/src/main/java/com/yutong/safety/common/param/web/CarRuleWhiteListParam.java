package com.yutong.safety.common.param.web;

import lombok.Data;

/**
 * @author lkp
 * @since 2024-04-21 09:55
 */
@Data
public class CarRuleWhiteListParam extends WebPageParam {

    private Integer id;
    private String empNo; //员工号
    private String empName; //员工姓名
    private String carNo; //车牌号
    private String dept; //部门
    private String remark; //备注
}
