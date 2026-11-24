package com.yutong.modules.entity;

import java.io.Serializable;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 车辆出入场记录表
 * @Author: jeecg-boot
 * @Date:   2026-08-10
 * @Version: V1.0
 */
@Data
@TableName("safety_crk_car_out_history")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="safety_crk_car_out_history对象", description="车辆出入场记录表")
public class SafetyCrkCarOutHistory {
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
	private Integer id;
    @ApiModelProperty(value = "出入口记录ID")
	private Integer crkId;
    @ApiModelProperty(value = "车牌号")
	private String tokenNo;
    @ApiModelProperty(value = "车辆类型 运输车辆 员工车辆 外协车辆等")
	private String tcmName;
    @ApiModelProperty(value = "员工号/手机号")
	private String staffNo;
    @ApiModelProperty(value = "司机姓名")
	private String staffName;
    @ApiModelProperty(value = "入厂时间")
	private String inTime;
    @ApiModelProperty(value = "入厂地点")
	private String inLaneName;
    @ApiModelProperty(value = "入厂大门")
	private String inOperatorName;
    @ApiModelProperty(value = "出厂时间")
	private String outTime;
    @ApiModelProperty(value = "出厂地点")
	private String outLaneName;
    @ApiModelProperty(value = "出厂大门")
	private String outOperatorName;
    @ApiModelProperty(value = "停车时长")
	private String stayLasts;
    @ApiModelProperty(value = "入厂识别图片")
	private String inPicture;
    @ApiModelProperty(value = "出厂识别图片")
	private String outPicture;
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private Date createTime;
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
	private Date updateTime;
    @ApiModelProperty(value = "备注")
	private String remark;
}
