package com.yutong.safety.core.db.entity.park;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SafetyParkPowerRuleHistoryDO implements Serializable {

    private Integer id;

    private String postName;

    private String organCode;

    private String remark;

    private Integer priority;

    private String factoryPower;

    private Date createTime;

    private Date updateTime;

    private Short isDelete;

    private String targetId;

    private static final long serialVersionUID = 1L;
}