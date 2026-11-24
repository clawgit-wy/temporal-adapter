package com.yutong.modules.user.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.desensitization.annotation.DataPermissionField;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description: 系统角色
 * @Author: jeecg-boot
 * @Date:   2026-05-22
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="系统角色VO", description="系统角色")
public class SafetySysRoleVO {

    /**记录ID*/
    @ApiModelProperty(value = "记录ID")
    private Long id;
    /**角色名称*/
    @DataPermissionField(deCode = "role_name", deName = "角色名称")
    @Excel(name = "角色名称", width = 15, orderNum = "1")
    @ApiModelProperty(value = "角色名称")
    private String roleName;
    /**角色编码*/
    @DataPermissionField(deCode = "role_code", deName = "角色编码")
    @Excel(name = "角色编码", width = 15, orderNum = "2")
    @ApiModelProperty(value = "角色编码")
    private String roleCode;
    /**角色类型：0非安保角色改为安全管理角色 1 安保角色 2 系统角色  3业务管理角色 */
    @DataPermissionField(deCode = "role_type", deName = "角色类型",enumMappings={"0:安全管理角色","1:安保角色","2:系统角色","3:业务管理角色"})
    @Excel(name = "角色类型", width = 15, orderNum = "3", replace = {"安全管理角色_0", "安保角色_1", "系统角色_2","3:业务管理角色"})
    @ApiModelProperty(value = "角色类型：0非安保角色改为安全管理角色 1 安保角色 2 系统角色  3业务管理角色")
    private String roleType;
    /**用户数量*/
    @Excel(name = "用户数量", width = 15, orderNum = "4")
    @ApiModelProperty(value = "用户数量")
    private Long userCount;
    /**修改时间*/
    @DataPermissionField(deCode = "last_time", deName = "修改时间")
    @Excel(name = "修改时间", width = 20, orderNum = "5", format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
    private Date lastTime;
}
