package com.yutong.safety.core.db.entity.goodsOut;

import lombok.Data;

import java.io.Serializable;

@Data
public class SafetyGoodsOutDetailReceiveDO implements Serializable {

    private Integer id;
    private String processId;
    private Integer targetFactoryId;
    private String targetFactory;
    private String receiveEmpNo;
    private String receiveEmpEip;
    private String receiveEmp;
    private String receiveEmpDept;
    private String receiveTime;
    private String isException;
    private String receiveStatus;
    private String receiveImg;
    private String remark;
    private String receivePhone;

    private static final long serialVersionUID = 1L;

}