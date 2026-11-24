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
 * @Description: 字段管理表
 * @Author: jeecg-boot
 * @Date:   2026-05-22
 * @Version: V1.0
 */
@Data
@TableName("safety_field_manage")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="safety_field_manage对象", description="字段管理")
public class SafetyFieldManage {
    
	/**id*/
	@TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "id")
	private Long id;
	/**名称*/
	@Excel(name = "名称", width = 15)
    @ApiModelProperty(value = "名称")
	private String fieldName;
	/**排序*/
	@Excel(name = "排序", width = 15)
    @ApiModelProperty(value = "排序")
	private Integer fieldSort;
	/**类型*/
	@Excel(name = "类型", width = 15)
    @ApiModelProperty(value = "类型")
	private String fieldType;
	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private Date createTime;
	/**更新时间*/
	@Excel(name = "更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
	private Date updateTime;
	/**是否删除:0否，1是*/
	@Excel(name = "是否删除:0否，1是", width = 15)
    @ApiModelProperty(value = "是否删除:0否，1是")
	private Integer deleteFalg;
	/**父类id*/
	@Excel(name = "父类id", width = 15)
    @ApiModelProperty(value = "父类id")
	private Long parentId;
	/**字段编码*/
	@Excel(name = "字段编码", width = 15)
    @ApiModelProperty(value = "字段编码")
	private String fieldCode;
	/**字段level*/
	@Excel(name = "字段level", width = 15)
    @ApiModelProperty(value = "字段level")
	private Integer fieldLevel;
	/**标记（1：出门证专属），其他信息*/
	@Excel(name = "标记（1：出门证专属），其他信息", width = 15)
    @ApiModelProperty(value = "标记（1：出门证专属），其他信息")
	private String remark;
	/**是否临时运输车辆,重新下发类*/
	@Excel(name = "是否临时运输车辆,重新下发类", width = 15)
    @ApiModelProperty(value = "是否临时运输车辆,重新下发类")
	private String fieldValue;
	/**是否填写环保信息*/
	@Excel(name = "是否填写环保信息", width = 15)
    @ApiModelProperty(value = "是否填写环保信息")
	private String envirInfo;
}
