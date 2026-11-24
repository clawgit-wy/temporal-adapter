package com.yutong.modules.risk.vo;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 风险数据表
 * @Author: jeecg-boot
 * @Date:   2026-06-30
 * @Version: V1.0
 */
@Data
@ApiModel(value="ytsafety_risk_data对象", description="风险数据表")
public class YtsafetyTaskRiskDataVO {

    /**主键ID*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键ID")
    private Long id;
    @ApiModelProperty(value = "流程主键")
    private Long processId;
    /**风险编码*/
    @ApiModelProperty(value = "风险编码")
    private String riskCode;
    /**风险类别主键*/
    @ApiModelProperty(value = "风险类别主键")
    private Long riskCategoryId;
    /**风险类别名称*/
    @ApiModelProperty(value = "风险类别名称")
    private String riskCategoryName;
    /**所属厂区主键*/
    @ApiModelProperty(value = "所属厂区主键")
    private Long factoryId;
    /**厂区名称*/
    @ApiModelProperty(value = "厂区名称")
    private String factoryName;
    /**所属区域主键*/
    @ApiModelProperty(value = "所属区域主键")
    private Long areaId;
    /**区域名称*/
    @ApiModelProperty(value = "区域名称")
    private String areaName;
    /**所属组织主键*/
    @ApiModelProperty(value = "所属组织主键")
    private Long orgId;
    /**所属组织编码*/
    @ApiModelProperty(value = "所属组织编码")
    private String orgCode;
    /**所属组织名称*/
    @ApiModelProperty(value = "所属组织名称")
    private String orgName;
    /**位置主键*/
    @ApiModelProperty(value = "位置主键")
    private Long locationId;
    /**位置名称*/
    @ApiModelProperty(value = "位置名称")
    private String locationName;
    /**风险类别主键*/
    @ApiModelProperty(value = "风险类别主键")
    private Long riskTypeId;
    /**风险类型*/
    @ApiModelProperty(value = "风险类型")
    private String riskTypeName;
    /**作业环节主键*/
    @ApiModelProperty(value = "作业环节主键")
    private Long jobStepId;
    /**作业环节编码*/
    @ApiModelProperty(value = "作业环节编码")
    private String jobStepCode;
    /**作业环节名称*/
    @ApiModelProperty(value = "作业环节名称")
    private String jobStepName;
    /**安全员角色编码*/
    @ApiModelProperty(value = "安全员角色编码")
    private String safetyRoleCode;
    /**安全员角色编码*/
    @ApiModelProperty(value = "安全员姓名")
    private String safetyOfficerName;
    /**安全主管角色编码*/
    @ApiModelProperty(value = "安全主管角色编码")
    private String managerRoleCode;
    /**检查对象*/
    @ApiModelProperty(value = "检查对象")
    private String checkObject;
    /**风险因素主键*/
    @ApiModelProperty(value = "风险因素主键")
    private Long riskFactorId;
    /**风险因素名称*/
    @ApiModelProperty(value = "风险因素名称")
    private String riskFactorName;
    /**风险识别维度主键*/
    @ApiModelProperty(value = "风险识别维度主键")
    private Long riskIdentifyId;
    /**风险识别维度名称*/
    @ApiModelProperty(value = "风险识别维度名称")
    private String riskIdentifyName;
    /**风险暴露频率主键*/
    @ApiModelProperty(value = "风险暴露频率主键")
    private Long exposeFreqId;
    /**风险暴露频率名称*/
    @ApiModelProperty(value = "风险暴露频率名称")
    private String exposeFreqName;
    /**风险可能性主键*/
    @ApiModelProperty(value = "风险可能性主键")
    private Long possibilityId;
    /**风险可能性名称*/
    @ApiModelProperty(value = "风险可能性名称")
    private String possibilityName;
    /**后果严重性主键*/
    @ApiModelProperty(value = "后果严重性主键")
    private Long severityId;
    /**后果严重性名称*/
    @ApiModelProperty(value = "后果严重性名称")
    private String severityName;
    /**风险值*/
    @ApiModelProperty(value = "风险值")
    private Integer riskValue;
    /**风险警示色*/
    @ApiModelProperty(value = "风险警示色")
    private String riskWarnColor;
    /**风险等级*/
    @ApiModelProperty(value = "风险等级")
    private String riskLevel;
    /**风险描述*/
    @ApiModelProperty(value = "风险描述")
    private String riskDescription;
    /**防范措施*/
    @ApiModelProperty(value = "防范措施")
    private String preventMeasure;
    /**更新状态*/
    @ApiModelProperty(value = "更新状态")
    private Integer updateStatus;
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
    /**修改人姓名*/
    @Excel(name = "修改人姓名", width = 15)
    @ApiModelProperty(value = "修改人姓名")
    private String updateName;
    /**修改人工号*/
    @Excel(name = "修改人工号", width = 15)
    @ApiModelProperty(value = "修改人工号")
    private String updateUser;
    /**修改时间*/
    @Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
    private Date updateTime;
    /**删除标记 0未删除 1已删除*/
    @Excel(name = "删除标记 0未删除 1已删除", width = 15)
    @ApiModelProperty(value = "删除标记 0未删除 1已删除")
    private Integer delFlag;
    @Excel(name = "修改版本（乐观锁）", width = 15)
    @ApiModelProperty(value = "修改版本（乐观锁）")
    private Integer version;
}
