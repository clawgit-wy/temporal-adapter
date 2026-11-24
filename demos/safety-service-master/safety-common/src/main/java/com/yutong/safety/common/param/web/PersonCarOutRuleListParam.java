package com.yutong.safety.common.param.web;

import lombok.Data;


/**
 * @author gwxiexj
 * @since 2024-04-01 17:15:00
 */
@Data
public class PersonCarOutRuleListParam extends WebPageParam {

    private String checkFlag;

    private String empNo;

    private String empName;

    private String positionName;

}
