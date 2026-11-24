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
 * @Description: 楼宇门禁最新进出记录
 * @Author: jeecg-boot
 * @Date:   2026-08-10
 * @Version: V1.0
 */
@Data
@TableName("safety_build_in_out_history")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="safety_build_in_out_history对象", description="楼宇门禁最新进出记录")
public class SafetyBuildInOutHistory {
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
	private Integer id;
    @ApiModelProperty(value = "员工号")
	private String empNo;
    @ApiModelProperty(value = "姓名")
	private String empName;
    @ApiModelProperty(value = "门禁设备号")
	private String deviceCode;
    @ApiModelProperty(value = "进出时间")
	private String operTime;
    @ApiModelProperty(value = "进出类型 进/出")
	private String ioFlag;
    @ApiModelProperty(value = "进出方式 刷卡/人脸识别")
	private String typeName;
    @ApiModelProperty(value = "更新时间")
	private String updateTime;
    @ApiModelProperty(value = "门禁系统自动生成ID")
	private Integer historyId;
}
