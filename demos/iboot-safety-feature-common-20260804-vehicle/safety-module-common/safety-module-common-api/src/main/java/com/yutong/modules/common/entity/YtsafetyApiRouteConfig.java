package com.yutong.modules.common.entity;

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
 * @Description: 统一接口路由表
 * @Author: jeecg-boot
 * @Date:   2026-05-25
 * @Version: V1.0
 */
@Data
@TableName("ytsafety_api_route_config")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ytsafety_api_route_config对象", description="统一接口路由表")
public class YtsafetyApiRouteConfig {
    
	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
	private Long id;
    /**系统名称*/
    @Excel(name = "系统名称", width = 15)
    @ApiModelProperty(value = "系统名称")
    private String systemName;
	/**路由编码*/
	@Excel(name = "路由编码", width = 15)
    @ApiModelProperty(value = "路由编码")
	private String routeCode;
	/**系统地址*/
	@Excel(name = "系统地址", width = 15)
    @ApiModelProperty(value = "系统地址")
	private String sysUrl;
	/**接口路径*/
	@Excel(name = "接口路径", width = 15)
    @ApiModelProperty(value = "接口路径")
	private String intefacePath;
	/**接口名称*/
	@Excel(name = "接口名称", width = 15)
    @ApiModelProperty(value = "接口名称")
	private String interfaceName;
	/**接口服务*/
	@Excel(name = "接口服务", width = 15)
    @ApiModelProperty(value = "接口服务")
	private String interfaceServer;
	/**请求方式*/
	@Excel(name = "请求方式", width = 15)
    @ApiModelProperty(value = "请求方式")
	private String interfaceMethod;
	/**接口状态*/
	@Excel(name = "接口状态", width = 15)
    @ApiModelProperty(value = "接口状态")
	private Integer interfaceStatus;
	/**接口说明*/
	@Excel(name = "接口说明", width = 15)
    @ApiModelProperty(value = "接口说明")
	private String interfaceRemark;
    /**接口账号*/
    @Excel(name = "接口账号", width = 15)
    @ApiModelProperty(value = "接口账号")
    private String interfaceAccount;
    /**接口密码*/
    @Excel(name = "接口密码", width = 15)
    @ApiModelProperty(value = "接口密码")
    private String interfacePwd;
	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
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
	private Date updateTime;
	/**修改人*/
	@Excel(name = "修改人", width = 15)
    @ApiModelProperty(value = "修改人")
	private String updateUser;
	/**删除标识*/
	@Excel(name = "删除标识", width = 15)
    @ApiModelProperty(value = "删除标识")
	private Integer delFlag;
	/**修改版本*/
	@Excel(name = "修改版本", width = 15)
    @ApiModelProperty(value = "修改版本")
	private Integer version;
}
