package com.yutong.modules.risk.web;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Description: 补岗申请参数
 * @Author: jeecg-boot
 * @Date: 2026-07-31
 * @Version: V1.0
 */
@Data
public class MakeupApplyParam {

    @NotNull(message = "签到记录 ID 不能为空")
    @ApiModelProperty(value = "签到记录 ID", required = true)
    private Long id;

    @NotBlank(message = "补岗原因不能为空")
    @ApiModelProperty(value = "补岗原因", required = true)
    private String applyReason;

    /**审批人工号*/
    @NotBlank(message = "审批人工号不能为空")
    @ApiModelProperty(value = "审批人工号")
    private String approveUser;

}
