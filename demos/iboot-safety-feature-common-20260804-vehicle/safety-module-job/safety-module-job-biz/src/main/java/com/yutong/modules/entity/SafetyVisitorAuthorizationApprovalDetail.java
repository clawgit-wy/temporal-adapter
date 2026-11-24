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
 * @Description: 访客审批授权明细表
 * @Author: jeecg-boot
 * @Date:   2026-08-10
 * @Version: V1.0
 */
@Data
@TableName("safety_visitor_authorization_approval_detail")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="safety_visitor_authorization_approval_detail对象", description="访客审批授权明细表")
public class SafetyVisitorAuthorizationApprovalDetail {
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
	private Integer id;
    @ApiModelProperty(value = "访客审批授权表-主表ID")
	private Integer authorizationApprovalId;
    @ApiModelProperty(value = "被授权人名称")
	private String granteeName;
    @ApiModelProperty(value = "被授权人工号")
	private String granteeNo;
    @ApiModelProperty(value = "被授权人组织编码")
	private String granteeOrgCode;
    @ApiModelProperty(value = "被授权人组织名称")
	private String granteeOrgName;
    @ApiModelProperty(value = "被授权人岗位")
	private String granteePosition;
    @ApiModelProperty(value = "被授权人职种")
	private String granteeJobName;
    @ApiModelProperty(value = "数据状态， 0-审批中，1-正常，2-注销")
	private Integer status;
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
    @ApiModelProperty(value = "被授权人EIP账号")
	private String granteeEipLogin;
    @ApiModelProperty(value = "注销原因")
	private String cancelReason;
}
