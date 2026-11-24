package com.yutong.modules.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.Date;

/**
 * @Description: 组织架构映射表
 * @Author: jeecg-boot
 * @Date:   2026-05-26
 * @Version: V1.0
 */
@Data
@TableName("ytsafety_sys_org_factory")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ytsafety_sys_org_factory对象", description="组织架构映射表")
public class YTSafetySysOrgFactory {
    
	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
	private Long id;
	/**组织编码*/
	@Excel(name = "组织编码", width = 15)
    @ApiModelProperty(value = "组织编码")
	private String orgCode;
	/**组织名称*/
	@Excel(name = "组织名称", width = 15)
    @ApiModelProperty(value = "组织名称")
	private String orgName;
    /**厂区id*/
    @ApiModelProperty(value = "厂区id")
    private Long factoryId;
	/**厂区编码*/
	@Excel(name = "厂区编码", width = 15)
    @ApiModelProperty(value = "厂区编码")
	private String factoryCode;
	/**厂区名称*/
	@Excel(name = "厂区名称", width = 15)
    @ApiModelProperty(value = "厂区名称")
	private String factoryName;
	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
	private Date createTime;
	/**创建人*/
	@Excel(name = "创建人", width = 15)
    @ApiModelProperty(value = "创建人")
	private String createUser;
	/**修改时间*/
	@Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
	private Date updateTime;
	/**修改人*/
	@Excel(name = "修改人", width = 15)
    @ApiModelProperty(value = "修改人")
	private String updateUser;
	/**删除标识*/
	@Excel(name = "删除标识", width = 15)
    @ApiModelProperty(value = "删除标识")
//    @TableLogic(value = "0", delval = "1")
	private Integer delFlag;
	/**修改版本*/
	@Excel(name = "修改版本", width = 15)
    @ApiModelProperty(value = "修改版本")
    @Version
	private Integer version;
}
