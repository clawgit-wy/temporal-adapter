package com.yutong.safety.core.db.entity.enter.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class SafetyEnterRegisterEx implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 姓名*/
	@ExcelProperty(value = "姓名", index = 0)
	private String fdName;

	/** 身份证号*/
	@ExcelProperty(value = "身份证号", index = 1)
	private String fdCardNo;

	/** 手机号*/
	@ExcelProperty(value = "手机号", index = 2)
	private String fdPhone;

	/** 证件照*/
	@ExcelProperty(value = "证件照", index = 3)
	private String fdImg;

	/** 当前是否在厂*/
	@ExcelProperty(value = "证件照", index = 4)
	private String fdInFactory;

	/** 创建时间*/
	@ExcelProperty(value = "创建时间", index = 5)
	private String createTime;


}
