package com.yutong.safety.common.param.app;

import lombok.Data;

/**
 * @author gwcuiyx
 * @date 2023-10-17 15:15
 */
@Data
public class VisitorParam {
    /**
     * 访客姓名
     */
    private String visitorName;
    /**
     * 访客手机号
     */
    private String visitorPhone;
    /**
     * 访客身份证号
     */
    private String visitorIdCard;
    /**
     * 照片
     */
    private String photo;

    /**
     * 身份证照片
     */
    private String idCardPhoto;

    /**
     * 身份证照片是否保存，0： 否，1：是
     */
    private Integer isSaveCardPhoto;
}
