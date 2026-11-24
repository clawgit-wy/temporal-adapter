package com.yutong.modules.vehicle.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "YtsafetyQueryPermInfoDTO对象", description = "信息查询权限表DTO")
public class YtsafetyQueryPermInfoDTO {

    @ApiModelProperty(value = "角色编码")
    private String roleCode;

    @ApiModelProperty(value = "角色名称")
    private String roleName;

    @ApiModelProperty(value = "权限配置列表")
    private List<YtsafetyQueryPermItemDTO> permList;
}
