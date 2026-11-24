package com.yutong.safety.core.db.entity.fireInspection.excel;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
public class SafetyFireInspectionWorkStepExcel extends SafetyFireInspectionBaseExcel {

    /**
     * 作业环节编码
     */
    private String workStepCode;
    /**
     * 作业环节名称
     */
    private String workStepName;
    /**
     * 工序
     */
    private String process;
    /**
     * 作业单元
     */
    private String workUnit;
    /**
     * 作业类型
     */
    private String workType;
    /**
     * 作业部门
     */
    private String workDeptCode;
    /**
     * 作业部门
     */
    private String workDeptName;
    /**
     * 最近更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date updateTime;
    /**
     * 最近提交人
     */
    private String updateBy;

    /**
     * 校验
     * @param list 唯一值校验
     */
    public SafetyFireInspectionWorkStepExcel check(List<String> list) {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isBlank(this.workStepCode)) {
            sb.append("作业环节编码为空，");
        } else {
            if (list.contains(this.workStepCode)) {
                sb.append("作业环节编码重复，");
            } else {
                list.add(this.workStepCode);
            }
        }
        if (StringUtils.isBlank(this.workStepName)) {
            sb.append("作业环节名称为空，");
        }
        if (StringUtils.isBlank(this.process)) {
            sb.append("工序为空，");
        }
        if (StringUtils.isBlank(this.workUnit)) {
            sb.append("作业单元为空，");
        }
        if (StringUtils.isBlank(this.workType)) {
            sb.append("作业类型为空，");
        }
        if (StringUtils.isBlank(this.workDeptCode)) {
            sb.append("作业部门编码为空；");
        }
        if (StringUtils.isNotBlank(sb.toString())) {
            sb.append("\r\n");
        }
        this.failInfo = sb.toString();
        return this;
    }
}
