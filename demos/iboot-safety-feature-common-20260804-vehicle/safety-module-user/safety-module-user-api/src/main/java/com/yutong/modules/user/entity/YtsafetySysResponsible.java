package com.yutong.modules.user.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 安全责任信息表
 * @Author: jeecg-boot
 * @Date:   2026-05-27
 * @Version: V1.0
 */
@Data
@TableName("ytsafety_sys_responsible")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ytsafety_sys_responsible对象", description="安全责任信息表")
public class YtsafetySysResponsible {
    
	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
	private Long id;
	/**用户主键*/

    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "用户主键")
	private Long userId;
    /**用户主键*/
    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "用户名")
    private String empName;
    /**用户主键*/
    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "员工号")
    private String empNo;
    /**用户主键*/
    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "EIP登录名称")
    private String userName;
    /**用户主键*/
    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "人员类型 0 非保卫 1 保卫人员")
    private Integer userType;
	/**有效期*/
    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "有效期")
	private Date validPeriod;
	/**更新来源：0 手工创建 1 EHR匹配*/
    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "更新来源：0 手工创建 1 EHR匹配")
	private Integer updateSource;
	/**状态:0 正常 1 冻结*/
    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "状态:0 正常 1 冻结")
	private Integer status;
    /**描述*/
    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "描述")
    private String remark;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private Date createTime;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
	private String createUser;
	/**修改时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
	private Date updateTime;
	/**修改人*/
    @ApiModelProperty(value = "修改人")
	private String updateUser;
	/**删除标识*/
    @TableLogic(value = "0", delval = "1")
	@Excel(name = "删除标识", width = 15)
    @ApiModelProperty(value = "删除标识")
	private Integer delFlag;
	/**version*/
	@Excel(name = "version", width = 15)
    @ApiModelProperty(value = "version")
	private Integer version;
}
