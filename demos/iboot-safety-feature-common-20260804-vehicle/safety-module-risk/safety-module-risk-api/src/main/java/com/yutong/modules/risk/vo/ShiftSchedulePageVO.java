package com.yutong.modules.risk.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description: 排班表分页查询 VO（关联员工表查询姓名和岗位）
 * @Author: jeecg-boot
 * @Date: 2026-08-10
 * @Version: V1.0
 */
@Data
@ApiModel(value = "排班表分页查询 VO", description = "排班表分页查询结果（含员工姓名、岗位）")
public class ShiftSchedulePageVO {

    /**主键*/
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

    /**姓名（关联 safety_employee_message 表查询）*/
    @ApiModelProperty(value = "姓名")
    private String empName;

    /**人员类型：1-在岗人员 2-安保带班 3-消防值班*/
    @ApiModelProperty("人员类型 1：在岗人员 2：安保带班 3：消防值班")
    private String personType;

    /**岗位（关联 safety_employee_message 表查询）*/
    @ApiModelProperty(value = "岗位")
    private String position;

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
    private Integer delFlag;
}
