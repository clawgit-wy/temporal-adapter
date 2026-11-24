package com.yutong.safety.core.db.entity.enter.result;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class SafetyEnterApproveRt implements Serializable {

	private static final long serialVersionUID = 1L;

	// 对接人员姓名
	private String buttJointName;

	// 对接人员工号
	private String buttJointCode;

	// 对接人员电话
	private String buttJointPhone;

	/** 姓名*/
	private String name;

	/** 公司*/
	private String companyName;

	/** 备案类型*/
	private String recordType;

	/** 备案类型编码*/
	private String recordTypeCode;

	/** 身份证号*/
	private String cardId;

	/** 手机号*/
	private String phone;

	/** 准入厂区*/
	private String enterFactory;

	/** 准入厂区编码*/
	private String enterFactoryCode;

	/** 证件照*/
	private String photographUrl;

	/** 准入时间*/
	private String enterStartTime;

	private String enterEndTime;

	/** 是否黑名单*/
	private String isBlack;

	/** 二维码*/
	private String erwinCode;
	/** 流程审批ID */
	private String filingId;
	/** 车牌号 */
	private String carNo;
	//权限类型 1-当前有权限 2-以后有权限
	private Integer powerType;
	private String updateTime;
	/**
	 * 入厂方式 0：刷脸+二维码，1：刷脸，2：二维码
	 */
	private Integer entryMethod;

	private List<Map> factorys;
}
