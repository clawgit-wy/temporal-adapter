package com.yutong.modules.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 安全责任信息表
 * @Author: jeecg-boot
 * @Date:   2026-05-27
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="安全责任信息表DTO", description="安全责任信息表")
public class YtsafetySysResponsibleQueryDTO {

    /**员工号*/
    @ApiModelProperty(value = "员工号")
    private String empNo;

    /**员工名*/
    @ApiModelProperty(value = "员工名")
    private String empName;

    /**手机号*/
    @ApiModelProperty(value = "手机号")
    private String phone;

    /**所属厂区*/
    @ApiModelProperty(value = "所属厂区")
    private String belongFactoryId;

    /**部门主键*/
    @ApiModelProperty(value = "部门主键")
    private Long departId;

    /**部门主键*/
    @ApiModelProperty(value = "部门主键")
    private String departPath;

    /**用户类型*/
    @ApiModelProperty(value = "用户类型")
    private Integer userType;

    /**平台角色*/
    @ApiModelProperty(value = "平台角色")
    private String roleId;
}
