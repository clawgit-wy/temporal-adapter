package com.yutong.modules.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * @Description: 第三方接口请求报文信息
 * @Author: jeecg-boot
 * @Date:   2026-06-04
 * @Version: V1.0
 */
@Data
@TableName("safety_interface_log")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="safety_interface_log对象", description="第三方接口请求报文信息")
public class SafetyInterfaceLog {

	/**id*/
	@TableId(type = IdType.AUTO)
	@ApiModelProperty(value = "id")
	private Long id;
	/**接口url*/
	@Excel(name = "接口url", width = 15)
	@ApiModelProperty(value = "接口url")
	private String interfaceUrl;
	/**接口名称,区分接口的注释说明*/
	@Excel(name = "接口名称", width = 15)
	@ApiModelProperty(value = "接口名称,区分接口的注释说明")
	private String interfaceName;
	/**请求时间*/
	@Excel(name = "请求时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "请求时间")
	private Date requestTime;
	/**请求报文*/
	@Excel(name = "请求报文", width = 15)
	@ApiModelProperty(value = "请求报文")
	private String requestBody;
	/**返回时间*/
	@Excel(name = "返回时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "返回时间")
	private Date responseTime;
	/**返回报文*/
	@Excel(name = "返回报文", width = 15)
	@ApiModelProperty(value = "返回报文")
	private String responseBody;
	/**状态（成功/失败）*/
	@Excel(name = "状态（成功/失败）", width = 15)
	@ApiModelProperty(value = "状态（成功/失败）")
	private String status;
	/**耗时（毫秒）*/
	@Excel(name = "耗时（毫秒）", width = 15)
	@ApiModelProperty(value = "耗时（毫秒）")
	private Integer elapsedTime;
	/**记录创建时间*/
	@Excel(name = "记录创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "记录创建时间")
	private Date createdTime;
}
