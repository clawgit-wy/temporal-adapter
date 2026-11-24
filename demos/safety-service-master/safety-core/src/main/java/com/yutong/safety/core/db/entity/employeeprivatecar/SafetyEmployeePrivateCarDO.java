package com.yutong.safety.core.db.entity.employeeprivatecar;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.yutong.safety.common.param.web.WebPageParam;
import lombok.Data;

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

    @ExcelProperty(value = "车辆型号")
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

}