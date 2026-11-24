package com.yutong.modules.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

import javax.validation.constraints.NotBlank;

/**
 * @Description: EHR员工信息
 * @Author: jeecg-boot
 * @Date:   2026-05-22
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="safety_employee_message对象", description="EHR员工信息")
public class SafetyEmployeeMessageDTO {
    

	/**员工号*/
	@Excel(name = "员工号", width = 15)
    @ApiModelProperty(value = "员工号")
    @NotBlank(message = "员工号或姓名不能为空")
	private String empNo;


}
