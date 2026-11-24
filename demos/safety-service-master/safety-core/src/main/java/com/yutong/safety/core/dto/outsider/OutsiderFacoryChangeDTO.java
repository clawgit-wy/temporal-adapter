package com.yutong.safety.core.dto.outsider;

import lombok.Data;

import java.io.Serializable;

@Data
public class OutsiderFacoryChangeDTO implements Serializable {
    private String empNo;
    private String empName;
    //变更前厂区
    private String oldFactory;
    //变更后厂区
    private String newFactory;
    
    public OutsiderFacoryChangeDTO(){}
    
    public OutsiderFacoryChangeDTO(String empNo,String empName,String oldFactory,String newFactory){
        this.empNo = empNo;
        this.empName = empName;
        this.oldFactory = oldFactory;
        this.newFactory = newFactory;
    }
}
