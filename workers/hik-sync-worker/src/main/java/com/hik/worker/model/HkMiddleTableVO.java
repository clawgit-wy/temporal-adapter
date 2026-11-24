package com.hik.worker.model;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 海康中间库数据传输对象
 */
@Data
public class HkMiddleTableVO implements Serializable {
    private String orgId;
    private String jobNo;
    private String certificateNo;
    private Integer status = 0;
    private LocalDateTime updateTimeStr = LocalDateTime.now();
    private String name;
    private Integer personType = 1;
    private Integer sex;
    private String mobile;
    private Integer certType = 111;
    private LocalDate timeStr;
    private String positionOrgId;
    private String orgName;
    private String parentOrgId;
    private String faceData;
    private String faceUrl;
}
