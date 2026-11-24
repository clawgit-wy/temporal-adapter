package com.yutong.safety.core.db.entity;

import java.io.Serializable;
import java.util.Date;

public class SafetyCarRuleWhiteDO implements Serializable {
    private int id;
    private String empNo; //员工号
    private String empName; //员工姓名
    private String carNo; //车牌号
    private String dept; //部门
    private String remark; //备注
    private int status; //是否删除 0--否 1--是

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmpNo() {
        return empNo;
    }

    public void setEmpNo(String empNo) {
        this.empNo = empNo;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getCarNo() {
        return carNo;
    }

    public void setCarNo(String carNo) {
        this.carNo = carNo;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}