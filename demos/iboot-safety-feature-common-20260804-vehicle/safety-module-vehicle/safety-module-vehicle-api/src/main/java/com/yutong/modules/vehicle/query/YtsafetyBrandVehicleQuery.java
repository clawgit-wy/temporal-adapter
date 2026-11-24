package com.yutong.modules.vehicle.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description: 日韩品牌库查询对象
 * @Author: wxzhangmc
 * @Date:   2026-08-07
 */
@Data
public class YtsafetyBrandVehicleQuery {

    /**品牌名称*/
    @Excel(name = "品牌名称")
    @ApiModelProperty(value = "品牌名称")
    private String brandName;

    /**品牌产地*/
    @Excel(name = "品牌产地")
    @ApiModelProperty(value = "品牌产地")
    private String brandPlace;

    /**创建人姓名*/
    @Excel(name = "创建人姓名")
    @ApiModelProperty(value = "创建人姓名")
    private String createName;

    /**修改人姓名*/
    @Excel(name = "修改人姓名")
    @ApiModelProperty(value = "修改人姓名")
    private String updateName;

    /**创建时间开始*/
    @ApiModelProperty(value = "创建时间开始")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTimeStart;

    /**创建时间结束*/
    @ApiModelProperty(value = "创建时间结束")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTimeEnd;

    /**修改时间开始*/
    @ApiModelProperty(value = "修改时间开始")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTimeStart;

    /**修改时间结束*/
    @ApiModelProperty(value = "修改时间结束")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTimeEnd;
}
