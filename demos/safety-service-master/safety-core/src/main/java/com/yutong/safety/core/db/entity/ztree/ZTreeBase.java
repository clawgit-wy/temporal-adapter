package com.yutong.safety.core.db.entity.ztree;

import lombok.Data;

import java.io.Serializable;

/**ztree 树*/
@Data
public class ZTreeBase implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 节点ID*/
	private String id;

	/** 父节点ID*/
	private String pId;

	/** 节点名称*/
	private String name;

	/** 是否打开*/
	private Boolean open = false;

	/** checkBox / radio 的勾选状态*/
	private Boolean checked = false;

	/** checkBox / radio 是否禁用*/
	private Boolean chkDisabled = false;

	/** 用于判断 chkDisabled 0是true 1是false*/
	private int dt = 1;

	/** 是否有子节点 true、false*/
	private String isParent = "false";
}
