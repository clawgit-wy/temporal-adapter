package com.yutong.safety.core.db.entity.enter;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SafetyEmpCard implements Serializable {

    private static final long serialVersionUID = 1L;
    private String empNo;
    private String crkId;
    private String tokenId;
    private String ver;
    private String cardStatus;
    private Date updateTime;

}
