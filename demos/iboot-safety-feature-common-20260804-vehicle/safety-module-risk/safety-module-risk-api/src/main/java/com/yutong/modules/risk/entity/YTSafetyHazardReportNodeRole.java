package com.yutong.modules.risk.entity;

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
 * @Description: 隐患上报表流程节点配置角色
 * @Author: jeecg-boot
 * @Date:   2026-07-15
 * @Version: V1.0
 */
@Data
@TableName("ytsafety_hazard_report_node_role")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ytsafety_hazard_report_node_role对象", description="隐患上报表流程节点配置角色")
public class YTSafetyHazardReportNodeRole {
    
	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
	private java.lang.Long id;
    /**标题*/
    @Excel(name = "标题", width = 15)
    @ApiModelProperty(value = "标题")
    private java.lang.String title;
    /**优先级:节点对应角色优先顺序*/
    @Excel(name = "优先级:节点对应角色优先顺序", width = 15)
    @ApiModelProperty(value = "优先级:节点对应角色优先顺序")
    private java.lang.Integer priority;
    /**节点类型*/
    @Excel(name = "节点类型", width = 15)
    @ApiModelProperty(value = "节点类型")
    private java.lang.String nodeType;
	/**回调编码*/
	@Excel(name = "回调编码", width = 15)
    @ApiModelProperty(value = "回调编码")
	private java.lang.String functionFlag;
	/**模型标识*/
	@Excel(name = "模型标识", width = 15)
    @ApiModelProperty(value = "模型标识")
	private java.lang.String modelFlag;
	/**隐患类别：0-未分类，1-消防，2-治安*/
	@Excel(name = "隐患类别：0-未分类，1-消防，2-治安", width = 15)
    @ApiModelProperty(value = "隐患类别：0-未分类，1-消防，2-治安")
	private java.lang.Integer hazardCategory;
	/**问题来源：COMPANY_INSPECTION-公司级检查,DEPARTMENT_SELF_CHECK-部门自查,EMPLOYEE_SNAPSHOT-员工随手拍,PATROL_EXCEPTION-巡查异常*/
	@Excel(name = "问题来源：COMPANY_INSPECTION-公司级检查,DEPARTMENT_SELF_CHECK-部门自查,EMPLOYEE_SNAPSHOT-员工随手拍,PATROL_EXCEPTION-巡查异常", width = 15)
    @ApiModelProperty(value = "问题来源：COMPANY_INSPECTION-公司级检查,DEPARTMENT_SELF_CHECK-部门自查,EMPLOYEE_SNAPSHOT-员工随手拍,PATROL_EXCEPTION-巡查异常")
	private java.lang.String problemSource;
	/**问题等级：2-一般，3-较大，4-重大*/
	@Excel(name = "问题等级：2-一般，3-较大，4-重大", width = 15)
    @ApiModelProperty(value = "问题等级：2-一般，3-较大，4-重大")
	private java.lang.Integer problemLevel;
	/**范围类型：FACTORY-厂区,DEPT-部门*/
	@Excel(name = "范围类型：FACTORY-厂区,DEPT-部门", width = 15)
    @ApiModelProperty(value = "范围类型：FACTORY-厂区,DEPT-部门")
	private java.lang.String scopeType;
	/**角色code*/
	@Excel(name = "角色code", width = 15)
    @ApiModelProperty(value = "角色code")
	private java.lang.String roleCode;
	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private java.util.Date createTime;
}
