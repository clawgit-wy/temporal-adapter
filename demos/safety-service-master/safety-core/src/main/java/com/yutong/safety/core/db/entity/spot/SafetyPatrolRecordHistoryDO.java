package com.yutong.safety.core.db.entity.spot;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SafetyPatrolRecordHistoryDO implements Serializable {

    private Integer id;

    private Integer processId;

    private String eventFlowNo;

    private String eventType;

    private String teamName;

    private String eventLevel;

    private String dutyDept;

    private String address;

    private Integer isKeynote;

    private String problemDesc;

    private String problemImage;

    private Date createTime;

    private String createperson;

    private String createpersonno;

    private static final long serialVersionUID = 1L;

}