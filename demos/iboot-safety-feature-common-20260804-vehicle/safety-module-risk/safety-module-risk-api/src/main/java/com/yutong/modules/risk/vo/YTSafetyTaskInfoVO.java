package com.yutong.modules.risk.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yutong.modules.common.entity.YTSafetyFileInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @Description: 任务台账表VO
 * @Author: jeecg-boot
 * @Date:   2026-06-29
 * @Version: V1.0
 */
@Data
@ApiModel(value="任务台账表VO", description="任务台账表")
public class YTSafetyTaskInfoVO {

    /**主键*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private Long id;
    /**任务编号*/
    @Excel(name = "任务编号", width = 15)
    @ApiModelProperty(value = "任务编号")
    private String taskCode;
    /**任务名称*/
    @Excel(name = "任务名称", width = 15)
    @ApiModelProperty(value = "任务名称")
    private String taskName;
    /**任务类型(巡查、专项、定点)*/
    @Excel(name = "任务类型", width = 15)
    @ApiModelProperty(value = "任务类型(巡查、专项、定点)")
    private String taskType;
    /**风险类别(风险类型字典表id)*/
    @Excel(name = "风险类别", width = 15)
    @ApiModelProperty(value = "风险类别(风险类型字典表id)")
    private String riskCategoryId;
    /**任务描述(任务要求)*/
    @Excel(name = "任务描述", width = 15)
    @ApiModelProperty(value = "任务描述(任务要求)")
    private String taskDescription;
    /**开始日期*/
    @Excel(name = "开始日期", width = 20, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "开始日期")
    private Date startDate;
    /**结束日期*/
    @Excel(name = "结束日期", width = 20, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "结束日期")
    private Date endDate;
    /**检查周期(每周一次/每月一次)*/
    @Excel(name = "检查周期", width = 15)
    @ApiModelProperty(value = "检查周期(每周一次/每月一次)")
    private String inspectionCycle;
    /**任务执行范围(电脑:1、手机:2)*/
    @Excel(name = "任务执行范围", width = 15)
    @ApiModelProperty(value = "任务执行范围(电脑:1、手机:2)")
    private String execScope;
    /**任务状态(待执行、执行中、已过期、已休眠)*/
    @Excel(name = "任务状态", width = 15)
    @ApiModelProperty(value = "任务状态(待执行、执行中、已过期、已休眠)")
    private String taskStatus;
    /**附件id(文件服务回传id)*/
    @Excel(name = "附件id", width = 15)
    @ApiModelProperty(value = "附件id(文件服务回传id)")
    private String attachmentId;
    /**附件图片id(文件服务回传id)*/
    @Excel(name = "附件图片id", width = 15)
    @ApiModelProperty(value = "附件图片id(文件服务回传id)")
    private String attachmentImgId;
    /**休眠日期(休眠状态下展示)*/
    @Excel(name = "休眠日期", width = 20, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "休眠日期(休眠状态下展示)")
    private Date dormantDate;
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
    /**删除标记*/
    @Excel(name = "删除标记", width = 15)
    @ApiModelProperty(value = "删除标记")
    @TableLogic(value = "0", delval = "1")
    private Integer delFlag;
    /**修改版本*/
    @Excel(name = "修改版本", width = 15)
    @ApiModelProperty(value = "修改版本")
    @Version
    private Integer version;

    private YtsafetyTaskInfoExtensionVO taskInfoExtension;

    @Excel(name = "岗位类型", width = 15)
    @ApiModelProperty(value = "岗位验证类型 319：监控岗位上报，355：现场岗位上报，356：模拟岗位上报")
    private String postType;

    @Excel(name = "下发类型", width = 15)
    @ApiModelProperty(value = "下发类型")
    private String issueType;

    @Excel(name = "任务对象", width = 15)
    @ApiModelProperty(value = "任务对象")
    private String personnelName;

    @Excel(name = "任务对象工号", width = 15)
    @ApiModelProperty(value = "任务对象工号")
    private String personnelId;

    @Excel(name = "岗位code", width = 15)
    @ApiModelProperty(value = "岗位code")
    private String personnelPostId;

    @Excel(name = "自定义人员姓名", width = 15)
    @ApiModelProperty(value = "自定义人员姓名")
    private String responsibleEmpNos;

    @Excel(name = "自定义人员工号", width = 15)
    @ApiModelProperty(value = "自定义人员工号")
    private String responsibleEmpNames;

    @Excel(name = "岗位名称", width = 15)
    @ApiModelProperty(value = "自定义人员工号")
    private String personnelPostName;

    @Excel(name = "模型id", width = 15)
    @ApiModelProperty(value = "模型id")
    private String modelFlag;

    @Excel(name = "流程编码", width = 15)
    @ApiModelProperty(value = "流程编码")
    private String flowCode;

    @Excel(name = "流程主题", width = 15)
    @ApiModelProperty(value = "流程主题")
    private String flowSubject;

    /**下发审批*/
    @ApiModelProperty(value = "下发审批:0:部审批,1:发起人审批,2:消防员审批,3:自定义")
    private String issueApproval;

    /**审批人*/
    @ApiModelProperty(value = "审批人 多个逗号分割")
    private String approvalUser;

    /**审批人*/
    @ApiModelProperty(value = "审批人 多个逗号分割")
    private String approvalName;

    List<YTSafetyFileInfo> attachmentImgList;

    List<YTSafetyFileInfo> attachmentList;

    List<YtsafetyTaskUserVO> taskUserList;

}
