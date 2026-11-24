package com.yutong.safety.core.db.entity.bpm;

import com.yutong.safety.core.db.entity.bpm.operation.Operation;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class BpmOperations implements Serializable {

    private static final long serialVersionUID = 1L;

    private String activityType;
    private String taskId;
    private String nodeId;
    private String expectedName;
    private List<Operation> operations;
    private String taskFrom;
}
