package com.yutong.modules.risk.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description: 节假日表DTO
 * @Author: jeecg-boot
 * @Date:   2026-07-17
 * @Version: V1.0
 */
@Data
@ApiModel(value="节假日表DTO", description="节假日表")
public class YTSafetyHolidayDTO {

    /**主键*/
    @ApiModelProperty(value = "主键")
    private Integer id;

    /**日期（新增时支持逗号分割多个日期，如：2026-07-17,2026-07-18）*/
    @ApiModelProperty(value = "日期（多个用逗号分割，如：2026-07-17,2026-07-18）")
    private String holidayDate;

    /**节日名称*/
    @ApiModelProperty(value = "节日名称")
    private String holidayName;

    /**年份*/
    @ApiModelProperty(value = "年份")
    private String holidayYear;

    /**1-法定节假日 2-调休工作日*/
    @ApiModelProperty(value = "1-法定节假日 2-调休工作日")
    private Integer dayType;

    /**备注*/
    @ApiModelProperty(value = "备注")
    private String description;

    /**页码*/
    @ApiModelProperty(value = "页码")
    private Integer pageNo = 1;

    /**每页条数*/
    @ApiModelProperty(value = "每页条数")
    private Integer pageSize = 10;

}
