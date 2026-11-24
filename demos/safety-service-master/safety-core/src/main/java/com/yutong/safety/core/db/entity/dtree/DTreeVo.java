package com.yutong.safety.core.db.entity.dtree;

import lombok.Data;

import java.io.Serializable;

/** 树结果类*/
@Data
public class DTreeVo implements Serializable {

	private static final long serialVersionUID = 1L;

	/*状态*/
	private Status status = new Status();

	/*数据*/
	private Object data;

}
