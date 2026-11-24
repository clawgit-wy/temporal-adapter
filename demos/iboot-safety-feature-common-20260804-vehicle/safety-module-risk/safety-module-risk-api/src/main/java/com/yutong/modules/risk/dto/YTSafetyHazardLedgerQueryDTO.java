package com.yutong.modules.risk.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
@ApiModel(value = "隐患台账查询参数", description = "隐患台账查询参数")
public class YTSafetyHazardLedgerQueryDTO {

    @ApiModelProperty("隐患编码")
    private String hazardCode;

    @ApiModelProperty("隐患类型：0-未分类，1-消防，2-治安")
    private Integer hazardCategory;

    @ApiModelProperty("问题来源：COMPANY_INSPECTION-公司级检查,DEPARTMENT_SELF_CHECK-部门自查,EMPLOYEE_SNAPSHOT-员工随手拍,PATROL_EXCEPTION-巡查异常")
    private String problemSource;

    @ApiModelProperty("隐患等级：2-一般、3-较大、4-重大")
    private Integer problemLevel;

    @ApiModelProperty("所属厂区ID")
    private Long plantId;

    @ApiModelProperty("所属区域ID")
    private Long areaId;

    @ApiModelProperty("位置ID")
    private Long locationId;

    @ApiModelProperty("位置name")
    private String locationName;

    @ApiModelProperty("问题责任组织ID")
    private Long orgId;

    @ApiModelProperty("整改责任人工号")
    private String rectifyPersonEmpNo;

    @ApiModelProperty("整改责任人姓名")
    private String rectifyPersonName;

    @ApiModelProperty("问题类别")
    private String problemCategory;

    @ApiModelProperty("问题描述（模糊搜索）")
    private String problemDescription;

    @ApiModelProperty("是否当场处理：0-否，1-是")
    private Integer isImmediateHandle;

    @ApiModelProperty("当前状态：0-草稿、1-审批中、2-整改中、3-验证中、4-部分完成、5-已完成、6-预期未完成、7-逾期完成、8-废弃")
    private Integer status;

    @ApiModelProperty("检查时间开始")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date checkTimeBegin;

    @ApiModelProperty("检查时间结束")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date checkTimeEnd;

    @ApiModelProperty("页码")
    private Integer pageNo = 1;

    @ApiModelProperty("每页条数")
    private Integer pageSize = 10;

    @ApiModelProperty("台账ID列表（导出时使用，逗号分隔）")
    private String selections;

    @ApiModelProperty(hidden = true)
    private List<String> ids;

    @ApiModelProperty(hidden = true)
    private String applyNo;
}
