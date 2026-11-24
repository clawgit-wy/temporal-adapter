package com.yutong.modules.risk.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class YTSafetyHazardReportListVO {

    @ApiModelProperty(value = "主键")
    private String id;

    @ApiModelProperty(value = "隐患编码")
    private String hazardCode;

    @Excel(name = "隐患类别：0-未分类，1-消防，2-治安", width = 15)
    @ApiModelProperty(value = "隐患类别：0-未分类，1-消防，2-治安")
    private Integer hazardCategory;

    @Excel(name = "问题来源", width = 15)
    @ApiModelProperty(value = "问题来源：COMPANY_INSPECTION-公司级检查,DEPARTMENT_SELF_CHECK-部门自查,EMPLOYEE_SNAPSHOT-员工随手拍,PATROL_EXCEPTION-巡查异常")
    private String problemSource;

    @Excel(name = "检查名称", width = 15)
    @ApiModelProperty(value = "检查名称")
    private String checkName;

    @Excel(name = "检查时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "检查时间")
    private Date checkTime;

    @Excel(name = "所属厂区id", width = 15)
    @ApiModelProperty(value = "所属厂区id")
    private String plantId;

    @Excel(name = "所属厂区名称", width = 15)
    @ApiModelProperty(value = "所属厂区名称")
    private String plantName;

    @Excel(name = "所属区域id", width = 15)
    @ApiModelProperty(value = "所属区域id")
    private String areaId;

    @Excel(name = "所属区域名称", width = 15)
    @ApiModelProperty(value = "所属区域名称")
    private String areaName;

    @Excel(name = "责任组织名称", width = 15)
    @ApiModelProperty(value = "责任组织名称")
    private String orgName;

    @Excel(name = "位置id", width = 15)
    @ApiModelProperty(value = "位置id")
    private String locationId;

    @Excel(name = "位置名称", width = 15)
    @ApiModelProperty(value = "位置名称")
    private String locationName;

    @ApiModelProperty(value = "问题类别")
    private String problemCategoryName;

    @Excel(name = "问题等级", width = 15)
    @ApiModelProperty(value = "问题等级：2-一般，3-较大，4-重大")
    private Integer problemLevel;

    @Excel(name = "状态：0-草稿，1-审批中，2-整改中，3-验证中，4-部分完成，5-已完成，6-预期未完成，7-逾期完成，8-延期审批中，9-起草，30-废弃", width = 15)
    @ApiModelProperty(value = "状态：0-草稿，1-审批中，2-整改中，3-验证中，4-部分完成，5-已完成，6-预期未完成，7-逾期完成，9-起草，8-延期审批中，30-废弃")
    private Integer status;

    @Excel(name = "创建时间")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "问题描述")
    private String problemDescription;

}
