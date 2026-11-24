package com.yutong.modules.risk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description: 隐患规则配置
 * @Author: jeecg-boot
 * @Date:   2026-08-04
 * @Version: V1.0
 */
@Data
@TableName("ytsafety_hazard_rule_config")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ytsafety_hazard_rule_config对象", description="隐患规则配置")
public class YtsafetyHazardRuleConfig {
    
	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
	private Integer id;
	/**名称*/
	@Excel(name = "规则名称", width = 15)
    @ApiModelProperty(value = "规则名称")
	private String ruleName;
    /**名称*/
    @Excel(name = "规则编码", width = 15)
    @ApiModelProperty(value = "规则编码")
    private String ruleCode;
	/**数据值*/
	@Excel(name = "规则值", width = 15)
    @ApiModelProperty(value = "规则值")
	private String ruleValue;
    /**数据值*/
    @Excel(name = "描述", width = 15)
    @ApiModelProperty(value = "描述")
    private String description;
	/**创建人姓名*/
	@Excel(name = "创建人姓名", width = 15)
    @ApiModelProperty(value = "创建人姓名")
	private String createName;
	/**创建人工号*/
	@Excel(name = "创建人工号", width = 15)
    @ApiModelProperty(value = "创建人工号")
	private String createUser;
	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private Date createTime;
	/**删除标记：0-未删除，1-已删除*/
	@Excel(name = "删除标记：0-未删除，1-已删除", width = 15)
    @ApiModelProperty(value = "删除标记：0-未删除，1-已删除")
	private Integer delFlag;
}
