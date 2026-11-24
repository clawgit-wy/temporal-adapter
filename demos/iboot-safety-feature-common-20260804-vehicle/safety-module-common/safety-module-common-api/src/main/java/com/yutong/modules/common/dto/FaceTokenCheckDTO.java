package com.yutong.modules.common.dto;

import lombok.Data;

@Data
public class FaceTokenCheckDTO {
    /**
     * 0卡 1人脸
     */
    private Integer tokenType;
    /**
     * 0传入卡号 为1 Token传入staffNo
     */
    private String token;
}
