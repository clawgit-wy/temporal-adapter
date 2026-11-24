package com.yutong.safety.core.db.entity.bpm.operation;

import lombok.Data;

import java.io.Serializable;

/**
 * 废弃
 */
@Data
public class ParamFQ extends Param implements Serializable {

    private static final long serialVersionUID = 1L;

    private String toOtherHandlerIds;
    //驳回目标节点编号
    private String jumpToNodeId;
    //驳回通过后是否直接返回本节点
    private Boolean refusePassedToThisNode;
}
