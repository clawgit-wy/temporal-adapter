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
 * @Description: 下一节点审核人
 * @Author: jeecg-boot
 * @Date:   2026-06-29
 * @Version: V1.0
 */
@Data
@TableName("ytsafety_summary_process_nowapprover")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ytsafety_summary_process_nowapprover对象", description="下一节点审核人")
public class YtsafetySummaryProcessNowapprover {
	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
	private Long id;
	/**流程实例主键*/
    @ApiModelProperty(value = "流程实例主键")
	private Long processId;
	/**节点处理人*/
    @ApiModelProperty(value = "节点处理人")
	private String nodeHandlerNo;
	/**节点处理人名称*/
    @ApiModelProperty(value = "节点处理人名称")
	private String nodeHandlerName;
	/**节点处理人登录名*/
    @ApiModelProperty(value = "节点处理人登录名")
	private String nodeHandlerLogin;
	/**节点处理人*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "节点处理人")
	private Date createTime;
}
