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
 * @Description: 用户授权部门表
 * @Author: jeecg-boot
 * @Date:   2026-05-27
 * @Version: V1.0
 */
@Data
@TableName("ytsafety_sys_user_depart")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ytsafety_sys_user_depart对象", description="用户授权部门表")
public class YtsafetySysUserDepart {
    
	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
	private Long id;
	/**用户主键*/
	@Excel(name = "用户主键", width = 15)
    @ApiModelProperty(value = "用户主键")
	private Long userId;
	/**部门主键*/
	@Excel(name = "部门主键", width = 15)
    @ApiModelProperty(value = "部门主键")
	private Long departId;
    /**厂区主键*/
    @Excel(name = "部门编码", width = 15)
    @ApiModelProperty(value = "部门编码")
    private String departCode;
	/**部门名称*/
	@Excel(name = "部门名称", width = 15)
    @ApiModelProperty(value = "部门名称")
	private String departName;
	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private Date createTime;
}
