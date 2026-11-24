package com.yutong.safety.core.dto;

import lombok.Data;

import java.util.List;

/**
 * @author caomg
 * @date 2024-10-22 9:06
 * @description SafetyHairpinDTO
 */
@Data
public class SafetyHairpinDTO {
    private String eipFlow;
    private String visitedName;
    private String visitedPhone;
    private String startTime;
    private String endTime;
    private String vendorUnit;
    private String vendorMatte;
    private List<SafetyHairpinVisitorDTO> visitorList;
}
