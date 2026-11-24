package com.yutong.modules.risk.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Description: 在岗签到分页列表 VO（含中文翻译字段）
 * @Author: jeecg-boot
 * @Date: 2026-08-07
 * @Version: V1.0
 */
@Data
@ApiModel(value = "在岗签到分页列表 VO")
public class DutyCheckinPageVO {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "厂区 ID")
    private Long factoryId;

    @ApiModelProperty(value = "员工号")
    private String empNo;

    @ApiModelProperty(value = "姓名")
    private String empName;

    @ApiModelProperty(value = "手机号")
    private String phoneNo;

    /** 人员类型 */
    @ApiModelProperty(value = "人员类型")
    private String personType;

    @ApiModelProperty(value = "岗位")
    private String position;

    @ApiModelProperty(value = "厂区")
    private String factory;

    @ApiModelProperty(value = "位置")
    private String location;

    @ApiModelProperty(value = "位置 ID")
    private Long locationId;

    @ApiModelProperty(value = "部门班组")
    private String departmentTeam;

    @ApiModelProperty(value = "班次代码")
    private String shiftCode;

    @ApiModelProperty(value = "班次上班时间")
    private String shiftStartTime;

    @ApiModelProperty(value = "班次下班时间")
    private String shiftEndTime;

    @ApiModelProperty(value = "值班日期")
    private String dutyDate;

    @ApiModelProperty(value = "签到时间")
    private Date checkinTime;

    /**第三方打卡上班时间*/
    @ApiModelProperty(value = "第三方打卡上班时间")
    private String clockInTime;

    /**第三方打卡下班时间*/
    @ApiModelProperty(value = "第三方打卡下班时间")
    private String clockOutTime;

    /** 岗位状态 */
    @ApiModelProperty(value = "岗位状态")
    private String dutyStatus;

    @ApiModelProperty(value = "是否补签：0-否 1-是")
    private Integer isMakeup;

    @ApiModelProperty(value = "补岗原因")
    private String applyReason;

    @ApiModelProperty(value = "申请时间")
    private Date applyTime;

    @ApiModelProperty(value = "审批人工号")
    private String approveUser;

    @ApiModelProperty(value = "审批备注")
    private String approveRemark;

    @ApiModelProperty(value = "创建人工号")
    private String createUser;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新人工号")
    private String updateUser;

    @ApiModelProperty(value = "更新人")
    private String updateBy;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "删除标志")
    private Integer delFlag;

    @ApiModelProperty(value = "模型标识")
    private String modelFlag;

    @ApiModelProperty(value = "流程编码")
    private String flowCode;

    @ApiModelProperty(value = "流程主题")
    private String flowSubject;
}
