package com.yutong.safety.core.db.entity.dtree;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/** 树类*/
@Data
@EqualsAndHashCode(callSuper = false)
public class DTree extends DTreeBase {
	
	/** 子节点集合*/
	private List<DTree> children;
	
}
