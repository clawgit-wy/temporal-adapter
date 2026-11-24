package com.yutong.modules.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Description: 设置角色权限DTO
 * @Author: jeecg-boot
 * @Date:   2026-05-25
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SetRolePermDTO {

    @NotNull(message = "角色主键不能为空！")
    @ApiModelProperty(value = "角色id")
    private Long roleId;

    @ApiModelProperty(value = "权限id列表")
    private List<Long> permIds;
}
