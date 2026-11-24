package com.yutong.safety.common.vo.web;

import lombok.Data;

import java.util.Date;

@Data
public class SpotCheckPlanVO {

    private Integer id;
    private String planName;
    private String factoryId;
    private String factoryName;
    private Integer cycle;
    private Integer teamId;
    private String teamName;
    private String beginDate;
    private String endDate;
    private Integer status;
    private String remark;
    private Date createTime;
    private Date updateTime;
    private String updatePerson;
    private String updatePersonNo;
    private String dormancyBegin;
    private String dormancyEnd;
}
