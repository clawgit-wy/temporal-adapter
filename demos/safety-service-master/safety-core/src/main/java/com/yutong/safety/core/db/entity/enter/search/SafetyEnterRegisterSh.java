package com.yutong.safety.core.db.entity.enter.search;

import com.yutong.safety.common.param.Page;
import lombok.Data;

import java.io.Serializable;

@Data
public class SafetyEnterRegisterSh extends Page implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 姓名*/
	private String fdName;

	/** 身份证号*/
	private String fdCardNo;

	/** 手机号*/
	private String fdPhone;

	/** 当前是否在厂*/
	private String fdInFactory;

	/** 创建时间*/
	private String createTime;

	/** 创建人*/
	private String createUser;

}
