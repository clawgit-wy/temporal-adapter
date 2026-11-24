package com.yutong.modules.vehicle.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @Description: 车辆白名单新增/修改参数
 * @Author: jeecg-boot
 * @Date: 2026-07-31
 * @Version: V1.0
 */
@Data
public class CarWhitelistAddParam {

    @ApiModelProperty("主键（修改时必填）")
    private Long id;

    @NotNull(message = "白名单类型不能为空")
    @ApiModelProperty(value = "白名单类型：1-人员白名单 2-车辆类型白名单", required = true)
    private Integer whitelistType;

    @NotBlank(message = "员工号不能为空")
    @ApiModelProperty(value = "员工号", required = true)
    private String empNo;

    @ApiModelProperty("姓名")
    private String empName;

    @ApiModelProperty("组织名称")
    private String orgName;

    @ApiModelProperty("车辆类型（车辆类型白名单必填）")
    private String carType;

    @NotNull(message = "有效期开始时间不能为空")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "有效期开始时间", required = true)
    private Date validStartTime;

    @NotNull(message = "有效期结束时间不能为空")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "有效期结束时间", required = true)
    private Date validEndTime;

    @NotNull(message = "状态不能为空")
    @ApiModelProperty(value = "状态：0-停用 1-启用", required = true)
    private Integer status;

    @ApiModelProperty("创建人")
    private String createBy;

    @ApiModelProperty("创建人工号")
    private String createUser;

    @ApiModelProperty("更新人")
    private String updateBy;

    @ApiModelProperty("更新人工号")
    private String updateUser;
}
