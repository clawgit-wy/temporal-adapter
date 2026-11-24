package com.yutong.safety.core.db.entity.bpm.operation;

import com.yutong.safety.core.db.entity.bpm.branch.BranchParam;
import lombok.Data;

import java.io.Serializable;

@Data
public class Operation implements Serializable {

    private static final long serialVersionUID = 1L;

    private String operationType;
    private String operationName;
    private String operationHandlerType;
    private BranchParam branch;
}
