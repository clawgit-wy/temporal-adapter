package com.yutong.modules.user.entity;


import java.util.Date;

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

/**
 * @Description: 字典子表
 * @Author: jeecg-boot
 * @Date:   2026-05-27
 * @Version: V1.0
 */
@Data
@TableName("safety_dict_item")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ytsafety_sys_dict_item对象", description="字典子表")
public class SafetyDictItem {
    
	/**bigint*/
	@Excel(name = "bigint", width = 15)
    @ApiModelProperty(value = "bigint")
    @TableId(type = IdType.AUTO)
	private Long id;
	/**字典id*/
	@Excel(name = "字典id", width = 15)
    @ApiModelProperty(value = "字典id")
	private Long supperId;
    /**字典编码*/
    @Excel(name = "字典id", width = 15)
    @ApiModelProperty(value = "字典id")
    private String supperCode;
    /**字典子项目名称*/
    @Excel(name = "字典子项目名称", width = 15)
    @ApiModelProperty(value = "字典子项目名称")
    private String itemName;
    /**字典子项目编码*/
    @Excel(name = "字典子项目编码", width = 15)
    @ApiModelProperty(value = "字典子项目编码")
    private String itemCode;

	/**排序*/
	@Excel(name = "排序", width = 15)
    @ApiModelProperty(value = "排序")
	private Integer itemOrder;
    /**排序*/
    @Excel(name = "状态", width = 15)
    @ApiModelProperty(value = "状态：0编辑状态1使用状态2注销状态")
    private String isUse;
	/**updateBy*/
	@Excel(name = "updateUser", width = 15)
    @ApiModelProperty(value = "updateUser")
	private String updateUser;
	/**updateTime*/
	@Excel(name = "updateTime", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "updateTime")
	private Date updateTime;

    @ApiModelProperty(value = "父级id")
    private Long pid;
}
