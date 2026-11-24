package com.yutong.modules.user.entity;


import java.io.Serializable;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 用户菜单数据规则表
 * @Author: jeecg-boot
 * @Date:   2026-05-26
 * @Version: V1.0
 */
@Data
@TableName("ytsafety_sys_user_data_rule")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ytsafety_sys_user_data_rule对象", description="用户菜单数据规则表")
public class YtsafetySysUserDataRule {
    
	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
	private Long id;
	/**用户主键*/
	@Excel(name = "用户主键", width = 15)
    @ApiModelProperty(value = "用户主键")
	private Long userId;
	/**菜单主键*/
	@Excel(name = "菜单主键", width = 15)
    @ApiModelProperty(value = "菜单主键")
	private Long permId;
	/**fieldCode*/
	@Excel(name = "fieldCode", width = 15)
    @ApiModelProperty(value = "fieldCode")
	private String fieldCode;
	/**字段名称*/
	@Excel(name = "字段名称", width = 15)
    @ApiModelProperty(value = "字段名称")
	private String fieldName;
	/**关系: 0 或 1 且*/
	@Excel(name = "关系: or 或 and 且", width = 15)
    @ApiModelProperty(value = "关系: or 或 and 且")
	private String relation;
	/**rule conditions*/
	@Excel(name = "rule conditions", width = 15)
    @ApiModelProperty(value = ">,=,!=,like")
	private String ruleConditions;
	/**ruleValue*/
	@Excel(name = "ruleValue", width = 15)
    @ApiModelProperty(value = "ruleValue")
	private String ruleValue;
	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private Date createTime;
	/**createUser*/
	@Excel(name = "createUser", width = 15)
    @ApiModelProperty(value = "createUser")
	private String createUser;
	/**修改时间*/
	@Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
	private Date updateTime;
	/**修改人*/
	@Excel(name = "修改人", width = 15)
    @ApiModelProperty(value = "修改人")
	private String updateUser;
	/**状态：0 - 启用 1 - 删除*/
	@Excel(name = "状态：0 - 启用 1 - 删除", width = 15)
    @ApiModelProperty(value = "状态：0 - 启用 1 - 删除")
	private Integer delFlag;
	/**修改版本*/
	@Excel(name = "修改版本", width = 15)
    @ApiModelProperty(value = "修改版本")
	private Integer version;
    /**排序*/
    @Excel(name = "排序", width = 15)
    @ApiModelProperty(value = "排序")
	private Integer sort;

    @ApiModelProperty(value = "菜单编码")
    private String permCode;
}
