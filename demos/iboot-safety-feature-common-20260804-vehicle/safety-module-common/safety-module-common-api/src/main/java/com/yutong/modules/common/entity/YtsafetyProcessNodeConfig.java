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
 * @Description: 流程节点配置
 * @Author: jeecg-boot
 * @Date:   2026-07-23
 * @Version: V1.0
 */
@Data
@TableName("ytsafety_process_node_config")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ytsafety_process_node_config对象", description="流程节点配置")
public class YtsafetyProcessNodeConfig {
    
	/**雪花主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "雪花主键")
	private Integer id;
	/**模型标识*/
    @ApiModelProperty(value = "模型标识")
	private String modelFlag;
	/**节点标识*/
    @ApiModelProperty(value = "节点标识")
	private String nodeFlag;
	/**数据类型*/
    @ApiModelProperty(value = "数据类型")
	private String dataType;
	/**角色编码*/
    @ApiModelProperty(value = "角色编码")
	private String roleCode;
	/**角色范围 0 厂区 1 部门 2 厂区部门*/
    @ApiModelProperty(value = "角色范围 0 厂区 1 部门 2 厂区部门")
	private Integer roleScode;
	/**执行类型 0 顺序执行 1 有跳过 */
    @ApiModelProperty(value = "执行类型 0 顺序执行 1 有跳过 ")
	private Integer runType;
	/**执行顺序*/
    @ApiModelProperty(value = "执行顺序")
	private Integer sortIndex;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private Date createTime;
	/**创建人姓名*/
    @ApiModelProperty(value = "创建人姓名")
	private String createName;
	/**创建人工号*/
    @ApiModelProperty(value = "创建人工号")
	private String createUser;
}
