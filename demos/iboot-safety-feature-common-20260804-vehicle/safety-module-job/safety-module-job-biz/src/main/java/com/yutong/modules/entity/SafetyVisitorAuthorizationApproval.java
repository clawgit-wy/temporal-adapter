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
 * @Description: 访客审批授权表
 * @Author: jeecg-boot
 * @Date:   2026-08-10
 * @Version: V1.0
 */
@Data
@TableName("safety_visitor_authorization_approval")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="safety_visitor_authorization_approval对象", description="访客审批授权表")
public class SafetyVisitorAuthorizationApproval {
    
	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
	private Integer id;
    @ApiModelProperty(value = "提交人名称")
	private String submitName;
    @ApiModelProperty(value = "提交人工号")
	private String submitNo;
    @ApiModelProperty(value = "提交人组织编码")
	private String submitOrgCode;
    @ApiModelProperty(value = "提交人组织名称")
	private String submitOrgName;
    @ApiModelProperty(value = "授权人名称")
	private String grantorName;
    @ApiModelProperty(value = "授权人工号")
	private String grantorNo;
    @ApiModelProperty(value = "授权人组织编码")
	private String grantorOrgCode;
    @ApiModelProperty(value = "授权人组织名称")
	private String grantorOrgName;
    @ApiModelProperty(value = "授权人岗位")
	private String grantorPosition;
    @ApiModelProperty(value = "授权人职种")
	private String grantorJobName;
    @ApiModelProperty(value = "授权原因")
	private String authorizationReason;
    @ApiModelProperty(value = "授权时间-开始")
	private String authorizationBeginTime;
    @ApiModelProperty(value = "授权时间-结束")
	private String authorizationEndTime;
    @ApiModelProperty(value = "审批人员工号")
	private String applyEmpNo;
    @ApiModelProperty(value = "审批人Login")
	private String applyEmpLogin;
    @ApiModelProperty(value = "审批人姓名")
	private String applyEmpName;
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
    @ApiModelProperty(value = "是否科级管理及以上，0：否 1：是")
	private Integer sectionChiefType;
    @ApiModelProperty(value = "授权人EIP登录账号")
	private String grantorEipLogin;
    @ApiModelProperty(value = "流程ID")
	private String bpmFlowId;
}
