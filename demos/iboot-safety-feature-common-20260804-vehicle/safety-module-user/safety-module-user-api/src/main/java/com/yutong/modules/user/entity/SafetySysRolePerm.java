package com.yutong.modules.user.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description: 角色权限关联表
 * @Author: jeecg-boot
 * @Date:   2026-05-25
 * @Version: V1.0
 */
@Data
@TableName("safety_sys_role_perm")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="safety_sys_role_perm对象", description="角色权限关联表")
public class SafetySysRolePerm {
    
	/**角色ID*/
	@Excel(name = "角色ID", width = 15)
    @ApiModelProperty(value = "角色ID")
	private Long roleId;
	/**权限ID*/
	@Excel(name = "权限ID", width = 15)
    @ApiModelProperty(value = "权限ID")
	private Long permId;
    /**是否删除:0否，1是*/
    @Excel(name = "是否删除:0否，1是", width = 15)
    @ApiModelProperty(value = "是否删除:0否，1是")
    @TableLogic(value = "0", delval = "1")
    private Integer delFlag;
    /**创建时间*/
    @Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}
