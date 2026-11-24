package com.yutong.modules.risk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
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
 * @Description: 排班表
 * @Author: jeecg-boot
 * @Date: 2026-07-31
 * @Version: V1.0
 */
@Data
@TableName("ytsafety_shift_schedule")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ytsafety_shift_schedule对象", description = "排班表")
public class YTSafetyShiftSchedule {

    /**主键（雪花 ID）*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private Long id;

    /**排班日期*/
    @ApiModelProperty(value = "排班日期")
    private String scheduleDate;

    /**厂区 ID*/
    @ApiModelProperty(value = "厂区 ID")
    private Long factoryId;

    /**员工号*/
    @ApiModelProperty(value = "员工号")
    private String empNo;

    /**人员类型：安保带班、消防值班*/
    @ApiModelProperty("人员类型 1：在岗人员 2：安保带班 3：消防值班")
    private String personType;

    /**所属厂区*/
    @ApiModelProperty(value = "所属厂区")
    private String factory;

    /**班次代码*/
    @ApiModelProperty(value = "班次代码")
    private String shiftCode;

    /**创建人工号*/
    @ApiModelProperty(value = "创建人工号")
    private String createUser;

    /**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;

    /**创建时间*/
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**修改人工号*/
    @ApiModelProperty(value = "修改人工号")
    private String updateUser;

    /**修改人*/
    @ApiModelProperty(value = "修改人")
    private String updateBy;

    /**修改时间*/
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    /**删除标志：0-未删除 1-已删除*/
    @ApiModelProperty(value = "删除标志：0-未删除 1-已删除")
    @TableLogic(value = "0", delval = "1")
    private Integer delFlag;
}
