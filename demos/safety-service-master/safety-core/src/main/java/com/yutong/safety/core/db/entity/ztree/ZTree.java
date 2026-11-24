package com.yutong.safety.core.db.entity.ztree;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**ztree 树*/
@Data
public class ZTree implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 节点ID*/
	private String id;

	/** 父节点ID*/
	private String pId;

	/** 节点名称*/
	private String name;

	/** 是否打开*/
	private Boolean open;

	/** checkBox / radio 的勾选状态*/
	private Boolean checked;

	/** checkBox / radio 是否禁用*/
	private Boolean chkDisabled;

	/** 用于判断 chkDisabled 0是true 1是false*/
	private int dt = 1;

	/** 是否有子节点 true、false*/
	private String isParent = "false";

	/** 子节点集合*/
	private List<ZTree> children;

}
