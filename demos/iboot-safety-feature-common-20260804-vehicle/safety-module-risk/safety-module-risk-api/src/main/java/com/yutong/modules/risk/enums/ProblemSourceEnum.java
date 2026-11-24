package com.yutong.modules.risk.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ProblemSourceEnum {
    UNKNOWN("未知",""),
    COMPANIES_INSPECTOR("公司级检查","COMPANY_INSPECTION"),
    DEPARTMENT_INSPECTOR("部门自查","DEPARTMENT_SELF_CHECK"),
    EMPLOYEE_PHOTO("员工随手拍","EMPLOYEE_SNAPSHOT"),
    ABNORMAL_INSPECTION("巡查异常","PATROL_EXCEPTION");

    private final String name;
    private final String vale;

    ProblemSourceEnum(String name,String vale){
        this.name = name;
        this.vale = vale;
    }

    public static ProblemSourceEnum getByVale(String vale){
        return Arrays.stream(values())
                .filter(ps -> ps.getVale().equals(vale))
                .findFirst()
                .orElse(UNKNOWN);
    }
}
