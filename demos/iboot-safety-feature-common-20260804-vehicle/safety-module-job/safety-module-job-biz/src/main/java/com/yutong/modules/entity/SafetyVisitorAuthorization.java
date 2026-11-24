package com.yutong.modules.entity;

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
 * @Description: 访客授权配置
 * @Author: jeecg-boot
 * @Date:   2026-08-10
 * @Version: V1.0
 */
@Data
@TableName("safety_visitor_authorization")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="safety_visitor_authorization对象", description="访客授权配置")
public class SafetyVisitorAuthorization {
    
	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
	private Integer id;
    @ApiModelProperty(value = "员工号")
	private String empNo;
    @ApiModelProperty(value = "姓名")
	private String empName;
    @ApiModelProperty(value = "组织路径编码")
	private String empOrgCodePath;
    @ApiModelProperty(value = "组织路径名称")
	private String empOrgNamePath;
    @ApiModelProperty(value = "被授权人组织名称")
	private Integer intervieweeNum;
    @ApiModelProperty(value = "删除标记，0-正常，1-删除")
	private Integer delFlag;
    @ApiModelProperty(value = "创建人")
	private String createBy;
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "记录创建时间")
	private Date createTime;
    @ApiModelProperty(value = "修改人")
	private String updateBy;
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "记录修改时间")
	private Date updateTime;
    @ApiModelProperty(value = "配置类型， 0：组织， 1：人员")
	private Integer configType;
}
