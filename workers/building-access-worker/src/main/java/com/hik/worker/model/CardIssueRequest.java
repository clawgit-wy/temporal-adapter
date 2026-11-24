package com.hik.worker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.List;

/**
 * 卡发行请求（接口4: DoorIssue）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardIssueRequest implements Serializable {
    private int useModel;
    private String orginazitionId;
    private String staffNo;
    private String tokenId;
    private String endDate;
    private int tokenOper;
    private String stcm;
    private String tcm;
    private String serialNo;
    private String token;
    private String icStartTimeL1;
    private String icStartTimeL2;
    private String icStartTimeL3;
    private String icStartTimeL4;
    private String icStartTimeR1;
    private String icStartTimeR2;
    private String icStartTimeR3;
    private String icStartTimeR4;
    private String CarPass;
    private String beginDate;
    private String staffName;
    private int tokenType;
    private List<String> AuthDevice;
}
