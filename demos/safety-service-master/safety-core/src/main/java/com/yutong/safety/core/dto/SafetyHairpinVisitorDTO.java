package com.yutong.safety.core.dto;

import lombok.Data;

/**
 * @author caomg
 * @date 2024-10-22 9:06
 * @description SafetyHairpinVisitorDTO
 */
@Data
public class SafetyHairpinVisitorDTO {
    private String visitorName;
    private String visitorPhone;
    private String visitorIdCard;
    private String photo;
    private String eipFlow;
    private String idCardPhoto;
    /**
     * 身份证照片是否保存，0： 否，1：是
     */
    private Integer isSaveCardPhoto;
}
