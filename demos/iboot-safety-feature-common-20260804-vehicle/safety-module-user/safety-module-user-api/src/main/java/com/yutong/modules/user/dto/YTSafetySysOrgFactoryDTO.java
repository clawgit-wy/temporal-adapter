package com.yutong.modules.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Description: 组织架构映射表
 * @Author: jeecg-boot
 * @Date:   2026-05-26
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ytsafety_sys_org_factoryDTO对象", description="组织架构映射表DTO")
public class YTSafetySysOrgFactoryDTO {
    
	/**id*/
    @ApiModelProperty(value = "id")
	private Long id;
    /**组织主键*/
    @NotBlank(message = "组织主键不能为空")
    @ApiModelProperty(value = "组织Code")
    private String orgCode;
    /**厂区主键*/
    @NotNull(message = "厂区主键不能为空")
    @ApiModelProperty(value = "厂区主键")
    private Long factoryId;
}
