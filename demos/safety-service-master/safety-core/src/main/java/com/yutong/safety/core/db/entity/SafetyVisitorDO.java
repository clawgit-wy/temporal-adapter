package com.yutong.safety.core.db.entity;

import lombok.Data;

/**
 * @author caomg
 * @date 2024-11-22 16:08
 * @description SafetyVisitorDO
 */
@Data
public class SafetyVisitorDO {
    private Integer id;
    private String actInFactory;
    private String visitorPhone;
    private String visitorIdCard;
    private String idCardPhoto;
}
