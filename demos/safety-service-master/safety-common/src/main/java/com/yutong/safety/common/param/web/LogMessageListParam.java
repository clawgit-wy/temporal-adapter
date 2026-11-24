package com.yutong.safety.common.param.web;

import lombok.Data;

/**
 * create by gwcuiyx on 2023.07.13
 */
@Data
public class LogMessageListParam extends WebPageParam {

    private String operatePage;

    private String operator;

    private String startTime;

    private String endTime;

    private String message;

}
