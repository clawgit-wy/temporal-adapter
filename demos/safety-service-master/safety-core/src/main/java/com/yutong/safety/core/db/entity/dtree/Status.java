package com.yutong.safety.core.db.entity.dtree;

import lombok.Data;

import java.io.Serializable;

/** 树状态类*/
@Data
public class Status implements Serializable {

	private static final long serialVersionUID = 1L;

	private int code = 200;
	
	private String message = "success";

}
