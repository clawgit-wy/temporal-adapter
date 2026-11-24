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
 * @Description: 用户授权厂区表
 * @Author: jeecg-boot
 * @Date:   2026-05-22
 * @Version: V1.0
 */
@Data
@TableName("ytsafety_sys_user_factory")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ytsafety_sys_user_factory对象", description="用户授权厂区表")
public class YtsafetySysUserFactory {
    
	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
	private Long id;
	/**用户主键*/
	@Excel(name = "用户主键", width = 15)
    @ApiModelProperty(value = "用户主键")
	private Long userId;
	/**厂区主键*/
	@Excel(name = "厂区主键", width = 15)
    @ApiModelProperty(value = "厂区主键")
	private Long factoryId;
    /**厂区主键*/
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
	private Date createTime;
}
