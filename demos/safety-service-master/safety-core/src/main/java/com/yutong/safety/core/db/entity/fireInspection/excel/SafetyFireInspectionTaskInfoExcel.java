package com.yutong.safety.core.db.entity.fireInspection.excel;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class SafetyFireInspectionTaskInfoExcel {
    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 作业环节名称
     */
    private String workStepName;

    /**
     * 作业部门
     */
    private String workDeptName;

    /**
     * 风险等级
     */
    private String riskLevel;

    /**
     * 检查级别
     */
    private String inspectionLevel;


    /**
     * 检查周期
     */
    private String inspectionCycle;

    /**
     * 任务周期
     */
    private String taskCycleBeginDate;
    private String taskCycleEndDate;

    /**
     * 任务下发时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date taskIssuedTime;

    /**
     * 巡检人
     */
    private String empNo;

    /**
     * 任务状态
     */
    private String taskStatus;

    /**
     * 任务完成时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date taskCompleteTime;

    /**
     * 检查结果
     */
    private String inspectionResult;

    /**
     * 整改结果
     */
    private String rectificationResult;


    /**
     * 检查编码
     */
    private String inspectionCode;

    /**
     * 检查项目
     */
    private String inspectionItem;
    /**
     * 检查对象
     */
    private String inspectionObject;

    /**
     * 检查类别
     */
    private String inspectionCategory;

    /**
     * 检查细分维度
     */
    private String inspectionSubDimension;

    /**
     * 检查内容
     */
    private String inspectionContent;

    /**
     * 检查方法
     */
    private String inspectionMethod;

    /**
     * 检查结果
     */
    private String inspectionResultDetail;

    /**
     * 检查时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date inspectionTime;

    /**
     * 异常流程
     */
    private String processId;

    private String workDeptCode;

    private String empOrgNamePath;

    private String empName;
}
