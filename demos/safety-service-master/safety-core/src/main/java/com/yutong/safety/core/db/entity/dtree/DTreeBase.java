package com.yutong.safety.core.db.entity.dtree;

import lombok.Data;

import java.io.Serializable;

/** 树类*/
@Data
public class DTreeBase implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 节点ID*/
	private String id;

	/** 上级节点ID*/
	private String parentId;

	/** 节点名称*/
	private String title;

	/** 是否最后一级节点*/
	private Boolean isLast;

	/** 自定义图标class*/
	private String iconClass;

	/** 表示用户自定义需要存储在树节点中的数据*/
	private Object basicData;

	/** 复选框选中状态 0-未选中，1-选中，2-半选*/
	private String checkArr = "0";

	/** 节点是否可用，false可用，true不可用*/
	private Boolean disabled = false;

	/** 用于判断 disabled 0是true 1是false*/
	private int dt = 0;
}
