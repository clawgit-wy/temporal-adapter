package com.yutong.safety.core.db.entity.bpm.branch;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ManualBranchNodeByDrafter implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String type;
    private String desc;
    private List<Branch> branchs;
}
