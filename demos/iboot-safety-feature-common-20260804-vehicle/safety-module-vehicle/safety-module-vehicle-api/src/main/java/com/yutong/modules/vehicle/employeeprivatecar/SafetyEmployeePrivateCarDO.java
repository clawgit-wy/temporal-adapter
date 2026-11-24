package com.yutong.modules.vehicle.employeeprivatecar;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.common.param.WebPageParam;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class SafetyEmployeePrivateCarDO extends WebPageParam implements Serializable {

    @ExcelIgnore
    private Integer id;

    @ExcelProperty(value = "车牌号")
    private String carNo;

    @ExcelProperty(value = "车辆颜色")
    private String carColor;

    @ExcelProperty(value = "车辆分类")
    private String vehicleType;

    @ExcelProperty(value = "行驶证")
    private String drivingLicenseUrl;

    @ExcelProperty(value = "驾驶证")
    private String driverCard;

    @ExcelProperty(value = "员工姓名")
    private String employeeName;

    @ExcelProperty(value = "员工号")
    private String employeeNo;

    @ExcelProperty(value = "是否锁定 0-未锁定 1-锁定")
    private String lockFlag;

    @ExcelProperty(value = "手机号")
    private String phoneNo;

    @ExcelProperty(value = "所属部门")
    private String deptNo;

    @ExcelProperty(value = "是否本人车辆")
    private String myselfCar;

    @ExcelProperty(value = "发布时间")
    private Date publishTime;

    @ExcelProperty(value = "其他图片1")
    private String imgUrl1;

    @ExcelProperty(value = "其他图片2")
    private String imgUrl2;

    @ExcelProperty(value = "其他图片3")
    private String imgUrl3;

    @ExcelProperty(value = "与车主关系")
    private String relationship;

    @ExcelProperty(value = "租赁到期时间")
    private String serverEndDate;

    @ExcelProperty(value = "其他-关系备注")
    private String otherRemark;

    @ExcelProperty(value = "员工号集合")
    private List<String> empNoList;

    @ExcelProperty(value = "分页标识")
    private Boolean pageFlag;

    private static final long serialVersionUID = 1L;

    /**
     * 删除标记：0：正常； 1：删除
     */
    private String deleteFlag = "0";

    @ApiModelProperty(value = "授权厂区")
    private String authFactory;
    @ApiModelProperty(value = "申请人科长姓名")
    private String sectionChiefName;
    @ApiModelProperty(value = "申请人科长员工号")
    private String sectionChiefNo;
    @ApiModelProperty(value = "车辆分类")
    private String carType;
    @ApiModelProperty(value = "车辆型号")
    private String carModel;
    /**车辆所有人*/
    @ApiModelProperty(value = "车辆所有人")
    private java.lang.String carAffiliationPersonnel;
    /**驾驶人姓名*/
    @ApiModelProperty(value = "驾驶人姓名")
    private java.lang.String driverPersonnelName;
    /**驾驶证有效期*/
    @ApiModelProperty(value = "驾驶证有效期开始时间")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date driverLicenseStartDate;
    @ApiModelProperty(value = "驾驶证有效期结束时间")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date driverLicenseEndDate;

}