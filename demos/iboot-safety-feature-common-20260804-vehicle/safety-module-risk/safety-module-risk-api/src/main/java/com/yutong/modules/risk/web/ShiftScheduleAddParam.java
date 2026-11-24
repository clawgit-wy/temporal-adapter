package com.yutong.modules.risk.web;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Description: 排班表新增/修改参数
 * @Author: jeecg-boot
 * @Date: 2026-07-31
 * @Version: V1.0
 */
@Data
public class ShiftScheduleAddParam {

    @ApiModelProperty("主键（修改时必填）")
    private Long id;

    @NotBlank(message = "排班日期不能为空")
    @ApiModelProperty(value = "排班日期", required = true)
    private String scheduleDate;

    @ApiModelProperty("厂区 ID")
    private Long factoryId;

    @NotBlank(message = "员工号不能为空")
    @ApiModelProperty(value = "员工号", required = true)
    private String empNo;

    @ApiModelProperty("姓名")
    private String empName;

    @NotBlank(message = "人员类型不能为空")
    @ApiModelProperty(value = "人员类型 1：在岗人员 2：安保带班 3：消防值班",required = true)
    private String personType;

    @ApiModelProperty(value = "岗位", required = true)
    private String position;

    @NotBlank(message = "厂区不能为空")
    @ApiModelProperty(value = "所属厂区", required = true)
    private String factory;

    @ApiModelProperty("班次代码")
    private String shiftCode;

    @ApiModelProperty("创建人工号")
    private String createUser;

    @ApiModelProperty("创建人")
    private String createBy;

    @ApiModelProperty("修改人工号")
    private String updateUser;

    @ApiModelProperty("修改人")
    private String updateBy;
}
