/**
 * @copyright: Copyright (c) 2015-2030 yutong.com All Rights Reserved
 * @file: SyncFireApproveParam.java project: environ
 * @creator: zhangpand
 * @date: 2021/6/23
 */


package com.yutong.safety.core.db.entity.hot.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.Objects;

/**
 * @author: zhangpand
 * @requireNo:
 * @createdate: 2021-06-23 14:15
 * @lastdate:
 */
@Data
@ApiModel(value = "SyncFireApproveParam", description = "EIP同步单据至掌宇参数")
public class SyncFireApproveParam implements Check {
    /**
     * 来源：掌宇、小程序
     */
    @ApiModelProperty(value = "来源：掌宇、小程序")
    private String source;

    /**
     * 单据号
     */
    @ApiModelProperty(value = "EIP单据号")
    private String documentNum;

    /**
     * 审批状态
     */
    @ApiModelProperty(value = "EIP审批状态")
    private String approveStatus;

    /**
     * 申请人编号，员工号或手机号
     */
    @ApiModelProperty(value = "申请人编号，员工号或手机号")
    private String applicantNum;

    /**
     * 审批级别
     */
    @ApiModelProperty(value = "审批级别")
    private String approveLevel;

    /**
     * 厂区
     */
    @ApiModelProperty(value = "厂区")
    private String region;

    /**
     * 动火级别
     */
    @ApiModelProperty(value = "动火级别")
    private String fireLevel;

    /**
     * 是否涉及车辆改造
     */
    @ApiModelProperty(value = "是否涉及车辆改造")
    private String carChange;

    /**
     * 施工单位
     */
    @ApiModelProperty(value = "施工单位")
    private String workCompany;

    /**
     * 动火部门
     */
    @ApiModelProperty(value = "动火部门")
    private String fireDept;

    /**
     * 动火详细位置
     */
    @ApiModelProperty(value = "动火详细位置")
    private String fireLocation;
    /**
     * 动火作业内容说明
     */
    @ApiModelProperty(value = "动火作业内容说明")
    private String fireDesc;

    /**
     * 车工号
     */
    @ApiModelProperty(value = "车工号")
    private String carNum;

    /**
     * 动火开始时间
     */
    @ApiModelProperty(value = "动火开始时间，时间格式yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date fireStartTime;

    /**
     * 动火结束时间
     */
    @ApiModelProperty(value = "动火结束时间，时间格式yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date fireEndTime;

    /**
     * 动火形式
     */
    @ApiModelProperty(value = "动火形式")
    private String fireForm;

    /**
     * 动火形式其他原因
     */
    @ApiModelProperty(value = "动火形式其他原因")
    private String otherReason;

    /**
     * 项目负责人姓名
     */
    @ApiModelProperty(value = "项目负责人姓名")
    private String principalName;

    /**
     * 项目负责人员工号
     */
    @ApiModelProperty(value = "项目负责人员工号")
    private String principalNum;

    /**
     * 共识过的安全防范措施
     */
    @ApiModelProperty(value = "共识过的安全防范措施")
    private String securityMeasure;

    /**
     * 审批过的动火安全方案照片
     */
    @ApiModelProperty(value = "审批过的动火安全方案照片，多张英文半角逗号分割")
    private String approvedPic;

    /**
     * 现场安全措施落实后的照片
     */
    @ApiModelProperty(value = "现场安全措施落实后的照片，多张英文半角逗号分割")
    private String fufilPic;

    /**
     * 动火人员信息
     */
    @ApiModelProperty(value = "动火人员信息，JSON数组字符串格式")
    private String fireEmpInfo;

    /**
     * 监护人信息
     */
    @ApiModelProperty(value = "监护人信息，JSON数组字符串格式")
    private String monitorInfo;
    /**
     * 当前EIP审核节点id
     */
    @ApiModelProperty(value = "当前EIP审核节点id")
    private String nodeId;
    /**
     * 当前EIP审核节点名称
     */
    @ApiModelProperty(value = "当前EIP审核节点名称")
    private String nodeName;
    /**
     * 当前EIP审核人员手机
     */
    @ApiModelProperty(value = "当前EIP审核人员手机")
    private String nodeReviewerMobile;
    /**
     * 当前EIP审核人员名字
     */
    @ApiModelProperty(value = "当前EIP审核人员名字")
    private String nodeReviewerName;

    /**
     * 消防员审批意见
     */
    @ApiModelProperty(value = "消防员审批意见")
    private String fireMenView;

    /**
     * 消防员组长审批意见
     */
    @ApiModelProperty(value = "消防组长审批意见")
    private String fireChiefView;

    /**
     * 监控镜头分配编码
     */
    @ApiModelProperty(value = "监控镜头分配编码")
    private String lensNums;

    /**
     * 监控镜头分配名称
     */
    @ApiModelProperty(value = "监控镜头分配名称")
    private String lensNames;

    /**
     * 具体动火部位照片
     */
    @ApiModelProperty(value = "具体动火部位照片")
    private String hotWorkArea;

    /**
     * 动火点周边大环境照片
     */
    @ApiModelProperty(value = "动火点周边大环境照片")
    private String hotPointEnvironment;

    /**
     * 现场防火布防护照片
     */
    @ApiModelProperty(value = "现场防火布防护照片")
    private String fireproofClothProtection;

    /**
     * 消防器材及水桶照片
     */
    @ApiModelProperty(value = "消防器材及水桶照片")
    private String fireEquipmentAndBucket;

    /**
     * 其他照片
     */
    @ApiModelProperty(value = "其他照片")
    private String hotWorkOther;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(StringUtils.isNotBlank(documentNum), ResponseEnum.PARAM_ERROR.getCode(), "单据号不能为空！");
        PreconditonsEx.checkArgument(StringUtils.isNotBlank(applicantNum), ResponseEnum.PARAM_ERROR.getCode(), "申请人编号不能为空！");
        PreconditonsEx.checkArgument(StringUtils.isNotBlank(source), ResponseEnum.PARAM_ERROR.getCode(), "来源不能为空！");
        PreconditonsEx.checkArgument(StringUtils.isNotBlank(approveStatus), ResponseEnum.PARAM_ERROR.getCode(), "审批状态不能为空！");
    }
}
