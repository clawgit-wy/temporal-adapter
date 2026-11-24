package com.yutong.modules.user.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description: 部门表
 * @Author: jeecg-boot
 * @Date:   2026-05-22
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SafetyGroupInfoDTO {
    
	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
	private Long id;
	/**编码*/
	@Excel(name = "编码", width = 15)
    @ApiModelProperty(value = "编码")
	private String code;
	/**名称*/
	@Excel(name = "名称", width = 15)
    @ApiModelProperty(value = "名称")
	private String name;
	/**上级组织code*/
	@Excel(name = "上级组织code", width = 15)
    @ApiModelProperty(value = "上级组织code")
	private String parentCode;
	/**status*/
	@Excel(name = "status", width = 15)
    @ApiModelProperty(value = "status")
	private String status;
	/**层级编码*/
	@Excel(name = "层级编码", width = 15)
    @ApiModelProperty(value = "层级编码")
	private String levelCode;
	/**层级名称*/
	@Excel(name = "层级名称", width = 15)
    @ApiModelProperty(value = "层级名称")
	private String levelName;
	/**上级组织路径名称*/
	@Excel(name = "上级组织路径名称", width = 15)
    @ApiModelProperty(value = "上级组织路径名称")
	private String orgCodePath;
	/**上级组织路径名称*/
	@Excel(name = "上级组织路径名称", width = 15)
    @ApiModelProperty(value = "上级组织路径名称")
	private String orgNamePath;
	/**排序编码*/
	@Excel(name = "排序编码", width = 15)
    @ApiModelProperty(value = "排序编码")
	private Integer deptShort;
	/**更新时间*/
	@Excel(name = "更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
	private Date updateTime;
	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private Date createTime;
}
