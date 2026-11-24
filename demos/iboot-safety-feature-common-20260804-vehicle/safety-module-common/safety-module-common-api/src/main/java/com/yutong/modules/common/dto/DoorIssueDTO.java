package com.yutong.modules.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DoorIssueDTO {
    private Integer useModel;
    private String orginazitionId;
    private String staffNo;
    private String tokenId;
    private String endDate;
    private Integer tokenOper;
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
    @JsonProperty("CarPass")
    private String CarPass;
    private String beginDate;
    private String staffName;
    private Integer tokenType;
    private List<String> AuthDevice;
}
