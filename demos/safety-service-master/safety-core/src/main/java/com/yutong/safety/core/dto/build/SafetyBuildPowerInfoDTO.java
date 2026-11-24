package com.yutong.safety.core.dto.build;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SafetyBuildPowerInfoDTO implements Serializable {
    @ExcelProperty("责任部门")
    private String dutyDeptName;
    private String dutyDeptCode;
    private String buildSysManagerRole;
    private List<SafetyBuildPowerDTO> enList;

}