package com.yutong.modules.vehicle.entity;

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

/**
 * @Description: 车辆人员变更信息表
 * @Author: jeecg-boot
 * @Date:   2026-08-04
 * @Version: V1.0
 */
@Data
@TableName("ytsafety_car_personnel_change_info")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ytsafety_car_personnel_change_info对象", description="车辆人员变更信息表")
public class YTSafetyCarPersonnelChangeInfo {
    
	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
	private Long id;
	/**流程表id*/
	@Excel(name = "流程表id", width = 15)
    @ApiModelProperty(value = "流程表id")
	private Long processId;
	/**车辆id*/
	@Excel(name = "车辆id", width = 15)
    @ApiModelProperty(value = "车辆id")
	private java.lang.Integer employeeCarId;
	/**车牌号*/
	@Excel(name = "车牌号", width = 15)
    @ApiModelProperty(value = "车牌号")
	private java.lang.String carNo;
	/**车辆类型*/
	@Excel(name = "车辆类型", width = 15)
    @ApiModelProperty(value = "车辆类型")
	private java.lang.String carType;
	/**车辆颜色*/
	@Excel(name = "车辆颜色", width = 15)
    @ApiModelProperty(value = "车辆颜色")
	private java.lang.String carColor;
	/**当前所属人员*/
	@Excel(name = "当前所属人员", width = 15)
    @ApiModelProperty(value = "当前所属人员")
	private java.lang.String currentAffiliatedPersonnel;
	/**是否更换人员*/
	@Excel(name = "是否更换人员", width = 15)
    @ApiModelProperty(value = "是否更换人员:1-是,0-否")
	private java.lang.String isReplacePersonnel;
	/**是否更换人员*/
	@Excel(name = "是否更换人员", width = 15)
    @ApiModelProperty(value = "更换人员")
	private java.lang.String changePersonnel;
	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private java.util.Date createTime;
	/**修改人姓名*/
	@Excel(name = "修改人姓名", width = 15)
    @ApiModelProperty(value = "修改人姓名")
	private java.lang.String updateName;
	/**修改人工号*/
	@Excel(name = "修改人工号", width = 15)
    @ApiModelProperty(value = "修改人工号")
	private java.lang.String updateUser;
	/**修改时间*/
	@Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
	private java.util.Date updateTime;
}
