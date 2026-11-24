package com.yutong.safety.core.db.entity.hot.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SyncFireApproveListVO implements Serializable {

    @ApiModelProperty("动火作业id")
    private Long id;

    @ApiModelProperty("施工单位")
    private String workCompany;

    @ApiModelProperty("厂区")
    private String region;

    @ApiModelProperty("动火级别")
    private String fireLevel;

    @ApiModelProperty("项目负责人姓名")
    private String principalName;

    @ApiModelProperty("项目负责人员工号")
    private String principalNum;

    @ApiModelProperty("动火开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date fireStartTime;

    @ApiModelProperty("动火结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date fireEndTime;

    @ApiModelProperty("镜头id")
    private Integer lensId;

    @ApiModelProperty("镜头编号")
    private String lensNum;

    @ApiModelProperty("镜头名称")
    private String lensName;

    @ApiModelProperty("镜头编号")
    private String key;

    @ApiModelProperty("镜头名称")
    private String title;
}
