package com.yutong.modules.risk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description: 节假日表
 * @Author: jeecg-boot
 * @Date:   2026-07-17
 * @Version: V1.0
 */
@Data
@TableName("ytsafety_holiday")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ytsafety_holiday对象", description="节假日表")
public class YTSafetyHoliday {

	/**主键*/
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "主键")
	private Integer id;

	/**日期*/
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "日期")
	private String holidayDate;

	/**节日名称*/
    @ApiModelProperty(value = "节日名称")
	private String holidayName;

	/**1-法定节假日 2-调休工作日*/
    @ApiModelProperty(value = "1-法定节假日 2-调休工作日")
	private Integer dayType;

	/**备注*/
    @ApiModelProperty(value = "备注")
	private String description;

    /**年份*/
    @ApiModelProperty(value = "年份")
    private String holidayYear;


}
