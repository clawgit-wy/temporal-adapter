package com.yutong.safety.core.db.entity.emp.common;

import com.google.common.collect.Lists;

import java.util.List;

public interface SafetyEmployeeConstants {
    String EMP_LEDGER_EMP_TYPE = "emp_ledger_emp_type";
    String EMP_LEDGER_JOB_TYPE = "emp_ledger_job_type";

    List<String> JOB_TYPE_NAME = Lists.newArrayList("高层管理", "科级管理", "部级管理", "处级管理", "室级管理", "班组级管理", "模块级管理", "院级管理");
}
