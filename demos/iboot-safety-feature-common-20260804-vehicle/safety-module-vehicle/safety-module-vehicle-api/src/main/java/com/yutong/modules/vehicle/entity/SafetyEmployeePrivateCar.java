package com.yutong.modules.vehicle.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.Date;

/**
 * @Description: 员工入厂私家车信息表
 * @Author: jeecg-boot
 * @Date:   2026-08-05
 * @Version: V1.0
 */
@Data
@TableName("safety_employee_private_car")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="safety_employee_private_car对象", description="员工入厂私家车信息表")
public class SafetyEmployeePrivateCar {
    
	/**主键ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键ID")
	private java.lang.Integer id;
	/**车牌号*/
	@Excel(name = "车牌号", width = 15)
    @ApiModelProperty(value = "车牌号")
	private java.lang.String carNo;
	/**车辆颜色*/
	@Excel(name = "车辆颜色", width = 15)
    @ApiModelProperty(value = "车辆颜色")
	private java.lang.String carColor;
	/**车辆型号*/
	@Excel(name = "车辆型号", width = 15)
    @ApiModelProperty(value = "车辆型号")
	private java.lang.String vehicleType;
	/**行驶证*/
	@Excel(name = "行驶证", width = 15)
    @ApiModelProperty(value = "行驶证")
	private java.lang.String drivingLicenseUrl;
	/**驾驶证*/
	@Excel(name = "驾驶证", width = 15)
    @ApiModelProperty(value = "驾驶证")
	private java.lang.String driverCard;
	/**员工姓名*/
	@Excel(name = "员工姓名", width = 15)
    @ApiModelProperty(value = "员工姓名")
	private java.lang.String employeeName;
	/**员工号*/
	@Excel(name = "员工号", width = 15)
    @ApiModelProperty(value = "员工号")
	private java.lang.String employeeNo;
	/**手机号*/
	@Excel(name = "手机号", width = 15)
    @ApiModelProperty(value = "手机号")
	private java.lang.String phoneNo;
	/**所属部门*/
	@Excel(name = "所属部门", width = 15)
    @ApiModelProperty(value = "所属部门")
	private java.lang.String deptNo;
	/**是否本人车辆*/
	@Excel(name = "是否本人车辆", width = 15)
    @ApiModelProperty(value = "是否本人车辆")
	private java.lang.String myselfCar;
	/**发布时间*/
	@Excel(name = "发布时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "发布时间")
	private java.util.Date publishTime;
	/**锁定标记 0-否 -是*/
	@Excel(name = "锁定标记 0-否 -是", width = 15)
    @ApiModelProperty(value = "锁定标记 0-否 -是")
	private java.lang.String lockFlag;
	/**其他图片1*/
	@Excel(name = "其他图片1", width = 15)
    @ApiModelProperty(value = "其他图片1")
	private java.lang.String imgUrl1;
	/**其他图片2*/
	@Excel(name = "其他图片2", width = 15)
    @ApiModelProperty(value = "其他图片2")
	private java.lang.String imgUrl2;
	/**其他图片3*/
	@Excel(name = "其他图片3", width = 15)
    @ApiModelProperty(value = "其他图片3")
	private java.lang.String imgUrl3;
	/**与车主关系*/
	@Excel(name = "与车主关系", width = 15)
    @ApiModelProperty(value = "与车主关系")
	private java.lang.String relationship;
	/**其他-关系备注*/
	@Excel(name = "其他-关系备注", width = 15)
    @ApiModelProperty(value = "其他-关系备注")
	private java.lang.String otherRemark;
	/**租赁到期时间*/
	@Excel(name = "租赁到期时间", width = 15)
    @ApiModelProperty(value = "租赁到期时间")
	private java.lang.String serverEndDate;
    @ApiModelProperty(value = "授权厂区")
    private String authFactory;
    @ApiModelProperty(value = "申请人科长姓名")
    private String sectionChiefName;
    @ApiModelProperty(value = "申请人科长员工号")
    private String sectionChiefNo;
    @Excel(name = "车辆分类", width = 15)
    @ApiModelProperty(value = "车辆分类")
    private String carType;
    @Excel(name = "车辆型号", width = 15)
    @ApiModelProperty(value = "车辆型号")
    private String carModel;
	/**车辆所有人*/
	@Excel(name = "车辆所有人", width = 15)
    @ApiModelProperty(value = "车辆所有人")
	private java.lang.String carAffiliationPersonnel;
	/**驾驶人姓名*/
	@Excel(name = "驾驶人姓名", width = 15)
    @ApiModelProperty(value = "驾驶人姓名")
	private java.lang.String driverPersonnelName;
	/**驾驶证有效期*/
    @Excel(name = "驾驶证有效期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ExcelProperty(value = "驾驶证有效期开始时间")
    private Date driverLicenseStartDate;
    @Excel(name = "驾驶证有效期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ExcelProperty(value = "驾驶证有效期结束时间")
    private Date driverLicenseEndDate;
	/**删除标记：0：正常； 1：删除*/
	@Excel(name = "删除标记：0：正常； 1：删除", width = 15)
    @ApiModelProperty(value = "删除标记：0：正常； 1：删除")
	private java.lang.String deleteFlag;
}
