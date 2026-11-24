package com.hik.worker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

/**
 * 人员信息模型（对应富士接口中的 Staff 对象）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffInfo implements Serializable {
    private int ID;
    private String StaffNo;
    private String StaffName;
    private int Sex;
    private String Birthday;
    private String IdNo;
    private String WxNo;
    private String QQNo;
    private String Email;
    private String TelphoneNo;
    private String MobileNo;
    private String Address;
    private String Plate;
    private String BuildingNo;
    private String HouseNo;
    private String RoomNo;
    private String DrivingLisenseNo;
    private String EducatingStatue;
    private String School;
    private String Major;
    private String NativePlace;
    private String PoliticalStatus;
    private String Skills;
    private String MaritalStatus;
    private String OrganizationId;
    private String RegDate;
    private String OperName;
    private int Status;
    private int IdentNumber;
    private String Reserve1;
    private String Reserve2;
    private String Reserve3;
    private String Remark;
    private String Rid;
}
