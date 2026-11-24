package com.yutong.modules.vehicle.entity;

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
 * @Description: 车辆白名单表
 * @Author: jeecg-boot
 * @Date: 2026-07-31
 * @Version: V1.0
 */
@Data
@TableName("ytsafety_car_whitelist")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ytsafety_car_whitelist对象", description = "车辆白名单表")
public class YTSafetyCarWhitelist {

    /**主键*/
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "主键")
    private Long id;

    /**白名单类型：1-人员白名单 2-车辆类型白名单*/
    @ApiModelProperty(value = "白名单类型：1-人员白名单 2-车辆类型白名单")
    private Integer whitelistType;

    /**员工号*/
    @ApiModelProperty(value = "员工号")
    private String empNo;

    /**姓名*/
    @ApiModelProperty(value = "姓名")
    private String empName;

    /**组织名称*/
    @ApiModelProperty(value = "组织名称")
    private String orgName;

    /**车辆类型*/
    @ApiModelProperty(value = "车辆类型")
    private String carType;

    /**有效期开始时间*/
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "有效期开始时间")
    private Date validStartTime;

    /**有效期结束时间*/
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "有效期结束时间")
    private Date validEndTime;

    /**状态：0-停用 1-启用*/
    @ApiModelProperty(value = "状态：0-停用 1-启用")
    private Integer status;

    /**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;

    /**创建人工号*/
    @ApiModelProperty(value = "创建人工号")
    private String createUser;

    /**创建时间*/
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;

    /**更新人工号*/
    @ApiModelProperty(value = "更新人工号")
    private String updateUser;

    /**更新时间*/
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    /**删除标志：0-未删除 1-已删除*/
    @ApiModelProperty(value = "删除标志：0-未删除 1-已删除")
    @TableLogic
    private Integer delFlag;
}
