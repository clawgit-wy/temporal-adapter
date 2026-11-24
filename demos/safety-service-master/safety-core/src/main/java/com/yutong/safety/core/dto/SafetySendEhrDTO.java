package com.yutong.safety.core.dto;

import lombok.Data;

@Data
public class SafetySendEhrDTO {
    private String creatorNo;
    private String creatorName;
    private String empOrg;
    private String remark;
    private Integer status;
}
