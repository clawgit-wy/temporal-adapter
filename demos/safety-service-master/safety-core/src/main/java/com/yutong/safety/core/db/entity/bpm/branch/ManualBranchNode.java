package com.yutong.safety.core.db.entity.bpm.branch;

import lombok.Data;

import java.io.Serializable;

@Data
public class ManualBranchNode implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String type;
    private String desc;
    private String manualNodeId;
    private String manualNodeName;
    private String manualNodeType;
}
