package com.yutong.safety.common.vo.web;

import lombok.Data;

import java.io.Serializable;

@Data
public class RetentionNoticeVO implements Serializable {

    private Integer id;
    private Integer retentionHour;
    private String carType;
    private String content;
    private String remindRule;
    private String remindNum;
    private String remindDecide;
    private Integer sendNum;
    private Integer remindGap;
    private String remindType;
    private Integer isUsed;
    private String updateBy;
    private String updateTime;
}
