package com.yutong.modules.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Description: 岗位角色映射表
 * @Author: jeecg-boot
 * @Date:   2026-05-26
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="YTSafetySysJobRoleMappingDTO对象", description="岗位角色映射表DTO")
public class YTSafetySysJobRoleMappingDTO {
	/**id*/
    @ApiModelProperty(value = "id")
	private Long id;
	/**岗位编码*/
    @NotBlank(message = "岗位编码不能为空")
    @Length(min = 1, max = 30, message = "岗位编码长度必须在2-30位之间")
    @ApiModelProperty(value = "岗位编码")
	private String jobCode;
	/**角色主键*/
    @NotNull(message = "角色主键不能为空")
    @ApiModelProperty(value = "角色主键")
	private Long roleId;
}
