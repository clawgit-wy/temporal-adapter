package com.yutong.safety.core.db.entity.spot;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SafetyPatrolDealHistoryDO implements Serializable {

    private Integer id;

    private Integer processId;

    private String dealTime;

    private String dealRecord;

    private String dealImage;

    private String dealPerson;

    private String dealPersonNo;

    private Integer isFinish;

    private Date createTime;

    private String resultScore;

    private String resultDesc;

    private String resultPerson;

    private String resultPersonNo;

    private String resultRemark;

    private static final long serialVersionUID = 1L;

}