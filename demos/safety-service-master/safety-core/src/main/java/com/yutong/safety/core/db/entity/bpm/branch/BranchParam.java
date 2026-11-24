package com.yutong.safety.core.db.entity.bpm.branch;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class BranchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Identity> identityList;
    private List<ManualBranchNodeByDrafter> manualBranchNodeListByDrafter;
    private List<ManualBranchNode> manualBranchNodeList;
    private List<Branch> mustModifyHandlerNodeList;
    private List<Branch> canModifyHandlerNodeList;

}
