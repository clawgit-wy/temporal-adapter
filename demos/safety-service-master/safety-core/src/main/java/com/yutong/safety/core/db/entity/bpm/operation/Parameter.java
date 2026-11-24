package com.yutong.safety.core.db.entity.bpm.operation;

import lombok.Data;

import java.io.Serializable;

@Data
public class Parameter implements Serializable {

    private static final long serialVersionUID = 1L;

    private String taskId;
    private String processId;
    private String activityType;
    private String operationType;
    private Param param;

    public Parameter(String taskId, String processId, String activityType, String operationType){
        this.taskId = taskId;
        this.processId = processId;
        this.activityType = activityType;
        this.operationType = operationType;
    }

}
