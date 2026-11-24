package com.yutong.safety.core.db.entity.spot;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SafetyProcessPatrolDO implements Serializable {

    private Integer id;

    private String flowId;

    private String title;

    private String creator;

    private Date createTime;

    private Date publishTime;

    private String status;

    private String flowNo;

    private String submitName;

    private String submitNo;

    private String submitDept;

    private String occurTime;

    private Integer factoryId;

    private String factory;

    private Integer eventSource;

    private Integer planId;

    private Integer taskId;

    private String planName;

    private String eventType;

    private Integer eventId;

    private Integer teamId;

    private String teamName;

    private String eventTime;

    private String planDesc;

    private Integer isFinish;

    private Integer isException;

    private String patrolImage;

    private String eventLevel;

    private String dutyDept;

    private String eventAddress;

    private Integer isKeynote;

    private String problemDesc;

    private String problemImage;

    private Integer isScene;

    private String dealRecord;

    private String dealImage;

    private String dutyPersonNo;

    private String dutyPerson;

    private String guardPersonNo;

    private String guardPerson;

    private String guardPhone;

    private String planDealTime;

    private String planMeasure;

    private Integer isTransfer;

    private String transferDept;

    private String dealTime;

    private String dealPersonNo;

    private String dealPerson;

    private String dealPersonGroup;

    private String resultScore;

    private String resultRemark;

    private String resultDesc;

    private String resultPerson;

    private String resultPersonNo;

    private Date resultTime;

    private Integer isGrab;

    private String grabPerson;

    private String grabPersonNo;

    private String grabTime;

    private Integer isDelay;

    private Integer isDispatch;

    private String dispatchPerson;

    private String dispatchPersonNo;

    private Integer nodeFlag;

    private String patrolRoleUsers;

    private Integer isDealTransfer;

    private String curDealHandler;

    private String curNodeId;

    private static final long serialVersionUID = 1L;

}