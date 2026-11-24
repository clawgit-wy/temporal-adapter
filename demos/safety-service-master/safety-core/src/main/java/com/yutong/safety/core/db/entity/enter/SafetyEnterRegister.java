package com.yutong.safety.core.db.entity.enter;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SafetyEnterRegister implements Serializable {

	private static final long serialVersionUID = 1L;

	/** ID*/
	private Integer id;

	/** 姓名*/
	private String fdName;

	/** 身份证号*/
	private String fdCardNo;

	/** 手机号*/
	private String fdPhone;

	/** 证件照*/
	private String fdImg;

	/** 当前是否在厂*/
	private String fdInFactory;

	/** 创建时间*/
	private Date createTime;

	/** 创建人*/
	private String createUser;

	/** 更新时间*/
	private Date updateTime;

	/** 更新人*/
	private String updateUser;

}
