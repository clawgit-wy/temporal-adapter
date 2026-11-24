package com.yutong.modules.user.vo;

import com.yutong.modules.user.model.SysDictSelect;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="安全责任人变更人员信息", description="安全责任人变更人员信息")
public class YtsafetyResponsibleChangeDropVO {
    /**变更人员主键*/
    @ApiModelProperty(value = "变更人员主键")
    private Long userId;
    /**授权厂区*/
    @ApiModelProperty(value = "授权厂区")
    private List<SysDictSelect> factoryId;
    /**责任部门*/
    @ApiModelProperty(value = "责任部门")
    private List<SysDictSelect> departId;
    /**授权角色*/
    @ApiModelProperty(value = "授权角色")
    private List<SysDictSelect> roleId;
}
