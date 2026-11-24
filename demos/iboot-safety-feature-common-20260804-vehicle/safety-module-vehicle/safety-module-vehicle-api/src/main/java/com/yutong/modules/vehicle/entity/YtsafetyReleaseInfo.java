package com.yutong.modules.vehicle.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@TableName("Ytsafety_release_info")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="YtsafetyReleaseInfo", description="放行台账表")
public class YtsafetyReleaseInfo{

    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "所属厂区名称")
    private String factoryName;

    @ApiModelProperty(value = "所属厂区ID")
    private Long factoryId;

    @ApiModelProperty(value = "位置名称")
    private String locationName;

    @ApiModelProperty(value = "位置ID")
    private Long locationId;

    @ApiModelProperty(value = "车辆类型")
    private String vehicleType;

    @ApiModelProperty(value = "车牌号")
    private String plateNumber;

    @ApiModelProperty(value = "进出类型（1-进场 2-出场）")
    private Integer inOutType;

    @ApiModelProperty(value = "放行人员工号")
    private String empNo;

    @ApiModelProperty(value = "放行人姓名")
    private String empName;

    @ApiModelProperty(value = "放行日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date releaseDate;

    @ApiModelProperty(value = "放行时间")
    @JsonFormat(timezone = "GMT+8", pattern = "HH:mm:ss")
    private Date releaseTime;

    @ApiModelProperty(value = "录像链接")
    private String videoHref;

    @ApiModelProperty(value = "环保等级")
    private String protectGrade;

    @ApiModelProperty(value = "随车清单")
    private String detailedImg;

    @ApiModelProperty(value = "VIN号")
    private String vin;

    @ApiModelProperty(value = "发动机号码")
    private String engineNum;

    @ApiModelProperty(value = "行驶证正面照片")
    private String carImg;

    @ApiModelProperty(value = "行驶证注册日期")
    private String carDate;

    @ApiModelProperty(value = "车辆归属")
    private String carBelong;

    @ApiModelProperty(value = "车主姓名/运输公司名称")
    private String driverName;

    @ApiModelProperty(value = "供应商名称")
    private String supplierName;

    @ApiModelProperty(value = "供应商代码")
    private String supplierCode;

    @ApiModelProperty(value = "运输货物名称")
    private String goodsName;

    @ApiModelProperty(value = "货物装载量（吨）")
    private String goodsWeight;

    @ApiModelProperty(value = "创建人姓名")
    private String createName;

    @ApiModelProperty(value = "创建人工号")
    private String createUser;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "修改人姓名")
    private String updateName;

    @ApiModelProperty(value = "修改人工号")
    private String updateUser;

    @ApiModelProperty(value = "修改时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty(value = "删除标记")
    private Integer delFlag;

    @ApiModelProperty(value = "修改版本")
    private Integer version;
}
