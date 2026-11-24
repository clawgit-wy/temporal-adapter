package com.yutong.safety.core.db.entity.fireInspection.excel;

import com.yutong.safety.common.constants.FireInspectionEnum;
import com.yutong.safety.common.utils.DateUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;

@Data
public class SafetyFireInspectionTaskSetExcel extends SafetyFireInspectionBaseExcel {
    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 作业环节编码
     */
    private String workStepCode;

    /**
     * 风险等级
     */
    private String riskLevel;

    /**
     * 任务对象
     */
    private String empNo;

    /**
     * 任务开始日期
     */
    private String taskBeginDate;

    /**
     * 任务结束日期
     */
    private String taskEndDate;

    /**
     * 备注
     */
    private String remark;


    private String workStepName;
    private String empName;

    /**
     * 校验
     */
    public SafetyFireInspectionTaskSetExcel check() {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isBlank(this.taskName)) {
            sb.append("任务名称为空；");
        }
        if (StringUtils.isBlank(this.workStepCode)) {
            sb.append("作业环节编码为空；");
        }
        if (StringUtils.isBlank(this.riskLevel)) {
            sb.append("风险等级为空；");
        } else {
            List<String> riskLevelList = FireInspectionEnum.getRiskLevelList();
            if (!riskLevelList.contains(this.riskLevel)) {
                sb.append("风险等级输入不规范：要求").append(riskLevel).append(";");
            } else {
                this.riskLevel = FireInspectionEnum.getFireInspectionEnumByRiskLevel(this.riskLevel).getCode();
            }
        }
        if (StringUtils.isBlank(this.taskBeginDate) || !DateUtils.isDate(this.taskBeginDate)) {
            sb.append("任务开始日期格式不正确，请用【yyyy-MM-dd】格式；");
        } else{
            String format = DateUtils.format(new Date(), 1);
            boolean beforeStr = DateUtils.isBeforeStr(this.taskBeginDate, format);
            if (beforeStr) {
                sb.append("任务开始日期需要大于当前日期；");
            }
        }
        if (StringUtils.isBlank(this.taskEndDate) || !DateUtils.isDate(this.taskEndDate)) {
            sb.append("任务结束日期格式不正确，请用【yyyy-MM-dd】格式；");
        }
        if (StringUtils.isNotBlank(this.taskBeginDate) && StringUtils.isNotBlank(this.taskEndDate) && this.taskBeginDate.equals(this.taskEndDate) && DateUtils.isAfterStr(this.taskBeginDate, this.taskEndDate)) {
            sb.append("任务结束日期需要大于任务开始日期；");
        }

        if (StringUtils.isNotBlank(sb.toString())) {
            sb.append("\r\n");
        }
        this.failInfo = sb.toString();
        return this;
    }
}
