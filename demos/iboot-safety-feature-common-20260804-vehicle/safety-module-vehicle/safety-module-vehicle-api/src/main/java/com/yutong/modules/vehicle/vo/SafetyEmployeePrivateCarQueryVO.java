package com.yutong.modules.vehicle.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
public class SafetyEmployeePrivateCarQueryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    @ApiModelProperty(value = "车牌号")
    @ExcelProperty(value = "车牌号")
    private String carNo;

    @ApiModelProperty(value = "车辆颜色")
    @ExcelProperty(value = "车辆颜色")
    private String carColor;

    @ApiModelProperty(value = "车辆型号")
    @ExcelProperty(value = "车辆型号")
    private String vehicleType;

    @ApiModelProperty(value = "行驶证")
    @ExcelProperty(value = "行驶证")
    private String drivingLicenseUrl;

    @ApiModelProperty(value = "驾驶证")
    @ExcelProperty(value = "驾驶证")
    private String driverCard;

    @ApiModelProperty(value = "员工姓名")
    @ExcelProperty(value = "员工姓名")
    private String employeeName;

    @ApiModelProperty(value = "员工号")
    @ExcelProperty(value = "员工号")
    private String employeeNo;

    @ApiModelProperty(value = "是否锁定 0-未锁定 1-锁定")
    @ExcelProperty(value = "是否锁定 0-未锁定 1-锁定")
    private String lockFlag;

    @ApiModelProperty(value = "手机号")
    @ExcelProperty(value = "手机号")
    private String phoneNo;

    @ApiModelProperty(value = "所属部门")
    @ExcelProperty(value = "所属部门")
    private String deptNo;

    @ApiModelProperty(value = "是否本人车辆")
    @ExcelProperty(value = "是否本人车辆")
    private String myselfCar;

    @ApiModelProperty(value = "发布时间")
    @ExcelProperty(value = "发布时间")
    private Date publishTime;

    @ApiModelProperty(value = "其他图片1")
    @ExcelProperty(value = "其他图片1")
    private String imgUrl1;

    @ApiModelProperty(value = "其他图片2")
    @ExcelProperty(value = "其他图片2")
    private String imgUrl2;

    @ApiModelProperty(value = "其他图片3")
    @ExcelProperty(value = "其他图片3")
    private String imgUrl3;

    @ApiModelProperty(value = "与车主关系")
    @ExcelProperty(value = "与车主关系")
    private String relationship;

    @ApiModelProperty(value = "租赁到期时间")
    @ExcelProperty(value = "租赁到期时间")
    private String serverEndDate;

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
    private String carAffiliationPersonnel;
    /**驾驶人姓名*/
    @Excel(name = "驾驶人姓名", width = 15)
    @ApiModelProperty(value = "驾驶人姓名")
    private String driverPersonnelName;
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

    @ApiModelProperty(value = "其他-关系备注")
    @ExcelProperty(value = "其他-关系备注")
    private String otherRemark;

    /**
     * 删除标记：0：正常； 1：删除
     */
    private String deleteFlag = "0";

}