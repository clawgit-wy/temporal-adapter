package com.yutong.safety.core.db.entity.bpm.operation;

import lombok.Data;

import java.io.Serializable;

@Data
public class Param implements Serializable {

    private static final long serialVersionUID = 1L;

    private String operationName;
    private String notifyType;
    private Boolean notifyOnFinish;
    private String auditNote;

}
