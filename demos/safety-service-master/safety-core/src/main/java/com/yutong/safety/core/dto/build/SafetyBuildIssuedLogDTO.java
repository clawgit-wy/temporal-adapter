package com.yutong.safety.core.dto.build;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.yutong.safety.common.param.web.WebPageParam;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SafetyBuildIssuedLogDTO extends WebPageParam implements Serializable {
    @ExcelIgnore
    private Integer id;

    @ExcelProperty(value = "员工号", index = 1)
    private String employeeCode;

    @ExcelProperty(value = "姓名", index = 0)
    private String employeeName;

    @ExcelProperty(value = "责任部门", index = 2)
    private String dutyDeptName;

    @ExcelIgnore
    private String deviceCode;

    @ExcelProperty(value = "楼宇门禁名称", index = 3)
    private String deviceName;

    @ExcelProperty(value = "下发异常门禁", index = 12)
    private String exceptDeviceName;

    @ExcelIgnore
    private String exceptDeviceCode;

    @ExcelProperty(value = "大门名称", index = 4)
    private String doorName;

    @ExcelIgnore
    private String beginTime;

    @ExcelIgnore
    private String endTime;

    @ExcelIgnore
    private String sourceType;

    @ExcelProperty(value = "来源", index = 6)
    private String targetId;

    @ExcelProperty(value = "类型", index = 7)
    private String issuedType;

    @ExcelProperty(value = "原因", index = 8)
    private String remark;

    @ExcelProperty(value = "操作人", index = 9)
    private String createBy;

    @ExcelProperty(value = "操作时间", index = 10)
    private String createTime;

    @ExcelIgnore
    private String createTimeBegin;
    @ExcelIgnore
    private String createTimeEnd;

    @ExcelProperty(value = "下发结果", index = 11)
    private String issuedStatus;

    @ExcelIgnore
    private String dutyDeptCode;

    @ExcelIgnore
    private static final long serialVersionUID = 1L;

    @ExcelIgnore
    private String export;

    @ExcelIgnore
    private String bpmFlowId;

    @ExcelIgnore
    private String issuedRemark;

    @ExcelIgnore
    private String roleType;

    @ExcelProperty(value = "授权有效期", index = 5)
    private String validDate;

    @ExcelIgnore
    private String modelId;
}