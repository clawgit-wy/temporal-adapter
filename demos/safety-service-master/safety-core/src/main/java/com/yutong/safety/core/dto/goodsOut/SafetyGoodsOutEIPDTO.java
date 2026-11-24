package com.yutong.safety.core.dto.goodsOut;

import lombok.Data;

@Data
public class SafetyGoodsOutEIPDTO {
    //接收人员工号
    private String toEmpNo;
    private String toEmpEip;
    private String toEmpName;
    private String toEmpDept;
    private String toEmpPhone;
    //提交人员工号
    private String submitNo;
    //主题
    private String subject;
    //接收数据的ID
    private String receiveId;
    //BPM流程编号
    private String flowId;
    //出门证流程ID
    private String processId;
    //原来的EIP待办人
    private String fromEIPEmpNo;

}
