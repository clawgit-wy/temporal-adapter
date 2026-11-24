package com.yutong.modules.common.vo;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.yutong.modules.common.constant.enums.CrkStaffStateEnum;
import lombok.Data;

import java.util.List;

/**
 * 出入口 人脸发行改写
 */
@Data
public class CrkFaceModifyVO {

    private int OperType;
    private CrkStaffInfoVO Staff;
    private String BeginTime;
    private String EndTime;
    private List<Integer> AuthorizeDeviceNo;
    private int StaffState;
    private String FacePicture;


    public static void main(String[] args) {
        CrkFaceModifyVO crkFaceModifyVO = new CrkFaceModifyVO();
        crkFaceModifyVO.setOperType(CrkStaffStateEnum.FACE_MODIFY.getCode());
        crkFaceModifyVO.setBeginTime("2026-07-07 00:00:00");
        crkFaceModifyVO.setEndTime("2026-07-09 23:59:59");
        crkFaceModifyVO.setAuthorizeDeviceNo(Lists.newArrayList(11));
        crkFaceModifyVO.setStaffState(CrkStaffStateEnum.STAFF_STATE.getCode());
        crkFaceModifyVO.setFacePicture("rty73854");
        CrkStaffInfoVO staff = new CrkStaffInfoVO();
        staff.setStaffNo("90132094");
        staff.setStaffName("张博");
        staff.setOrganizationId("243EAAD0-2797-410F-9EC5-E45842D2E4C0");
        staff.setRid("90132094");
        staff.setTelphoneNo("13203713938");
        staff.setSex(1);
        crkFaceModifyVO.setStaff(staff);
        String s = JSON.toJSON(crkFaceModifyVO).toString();
        System.out.println(s);
    }
}
