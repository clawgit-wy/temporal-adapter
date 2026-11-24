package com.yutong.modules.common.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FaceTokenCheckResultVO {
    private Integer ID;
    @JsonProperty("TokenId")
    private String TokenId;
    @JsonProperty("TokenType")
    private Integer TokenType;
    private String SerialNo;
    private String StaffId;
    @JsonProperty("StaffNo")
    private String StaffNo;
    @JsonProperty("StaffName")
    private String StaffName;
    @JsonProperty("StaffStatus")
    private Integer StaffStatus;
    @JsonProperty("IdentNumber")
    private BigDecimal IdentNumber;
    @JsonProperty("TelphoneNo")
    private String TelphoneNo;
    @JsonProperty("MobileNo")
    private String MobileNo;
    private String Address;
    @JsonProperty("IdNo")
    private String IdNo;
    private String Email;
    private String Skills;
    private String SubSystem;
    @JsonProperty("OperName")
    private String OperName;
    private String RegDate;
    @JsonProperty("UseModel")
    private Integer UseModel;
    @JsonProperty("TcmId")
    private String TcmId;
    @JsonProperty("TcmType")
    private Integer TcmType;
    @JsonProperty("TcmName")
    private String TcmName;
    @JsonProperty("TcmCode")
    private String TcmCode;
    @JsonProperty("StcmId")
    private String StcmId;
    @JsonProperty("StcmType")
    private Integer StcmType;
    @JsonProperty("StcmName")
    private String StcmName;
    @JsonProperty("StcmCode")
    private String StcmCode;
    private Integer State;
    @JsonProperty("BeginDate")
    private String BeginDate;
    @JsonProperty("EndDate")
    private String EndDate;
    @JsonProperty("AuthDevice")
    private String AuthDevice;
    @JsonProperty("IssueDate")
    private String IssueDate;
    @JsonProperty("CarPass")
    private String CarPass;
    @JsonProperty("EMUseMaxCount")
    private Integer EMUseMaxCount;
    @JsonProperty("FAntiPassback")
    private Integer FAntiPassback;
    @JsonProperty("FGroupNo")
    private Integer FGroupNo;
    @JsonProperty("IcStartTime1")
    private String IcStartTime1;
    @JsonProperty("IcStartTime2")
    private String IcStartTime2;
    @JsonProperty("IcStartTime3")
    private String IcStartTime3;
    @JsonProperty("IcStartTime4")
    private String IcStartTime4;
    private String Remark;
    @JsonProperty("OrganizationId")
    private String OrganizationId;
    @JsonProperty("OrganizationName")
    private String OrganizationName;
    @JsonProperty("Gid")
    private String Gid;
    @JsonProperty("Rid")
    private String Rid;
}
