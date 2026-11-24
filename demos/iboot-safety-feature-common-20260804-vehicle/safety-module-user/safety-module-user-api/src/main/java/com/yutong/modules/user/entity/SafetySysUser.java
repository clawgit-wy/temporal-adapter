package com.yutong.modules.user.entity;

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

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * @Description: 系统用户表
 * @Author: jeecg-boot
 * @Date:   2026-05-22
 * @Version: V1.0
 */
@Data
@TableName("safety_sys_user")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="safety_sys_user对象", description="系统用户表")
public class SafetySysUser {
    
	/**id*/
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "id")
	private Long id;
	/**账号*/
	@Excel(name = "eip用户名", width = 15)
    @ApiModelProperty(value = "eip用户名")
	private String userName;
    /**账号*/
    @Excel(name = "用户类型", width = 15)
    @ApiModelProperty(value = "用户类型")
    private Integer userType;
	/**密码*/
	@Excel(name = "密码", width = 15)
    @ApiModelProperty(value = "密码")
	private String passWord;
	/**员工名*/
	@Excel(name = "员工名", width = 15)
    @ApiModelProperty(value = "员工名")
	private String empName;
	/**员工号*/
	@Excel(name = "员工号", width = 15)
    @ApiModelProperty(value = "员工号")
	private String empNo;
	/**修改人*/
	@Excel(name = "修改人", width = 15)
    @ApiModelProperty(value = "修改人")
	private String modifyEmp;
	/**修改人号*/
	@Excel(name = "修改人号", width = 15)
    @ApiModelProperty(value = "修改人号")
	private String modifyEmpNo;
	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private Date createTime;
	/**修改时间*/
	@Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
	private Date updateTime;
	/**是否删除:0否，1是*/
	@Excel(name = "是否删除:0否，1是", width = 15)
    @ApiModelProperty(value = "是否删除:0否，1是")
	private Integer deleteFalg;

	/**访客发卡工厂id，多个以逗号分割*/
	@Excel(name = "访客发卡工厂id，多个以逗号分割", width = 15)
    @ApiModelProperty(value = "访客发卡工厂id，多个以逗号分割")
    @NotBlank
	private String factoryId;
	/**授权厂区厂id，多个以逗号分割*/
	@Excel(name = "授权厂区厂id，多个以逗号分割", width = 15)
    @ApiModelProperty(value = "授权厂区厂id，多个以逗号分割")
	private String powerFactoryId;



}
