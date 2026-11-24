package com.yutong.modules.risk.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;
import java.util.List;

/**
 * @Description: 隐患上报表
 * @Author: jeecg-boot
 * @Date:   2026-06-26
 * @Version: V1.0
 */
@Data
public class YTSafetyHazardReportReviewDTO {

    @NotNull(message = "主键必填")
    @ApiModelProperty(value = "主键",required = true)
    private Long id;
    @ApiModelProperty(value = "申请员工号",required = true)
    @NotBlank(message = "申请员工号必填")
    private String applyNo;
    @ApiModelProperty(value = "所属部门编码")
    private String applyDepartCode;
    @NotNull(message = "隐患类别必填")
    @ApiModelProperty(value = "隐患类别：0-未分类，1-消防，2-治安",required = true)
    private Integer hazardCategory;
    @NotBlank(message = "问题来源必填")
    @Pattern(regexp = "^(COMPANY_INSPECTION|DEPARTMENT_SELF_CHECK|EMPLOYEE_SNAPSHOT|PATROL_EXCEPTION)$", message = "问题来源只能填写 COMPANY_INSPECTION、DEPARTMENT_SELF_CHECK、EMPLOYEE_SNAPSHOT、PATROL_EXCEPTION")
    @ApiModelProperty(value = "问题来源：COMPANY_INSPECTION-公司级检查,DEPARTMENT_SELF_CHECK-部门自查,EMPLOYEE_SNAPSHOT-员工随手拍,PATROL_EXCEPTION-巡查异常",required = true)
    private String problemSource;
    @ApiModelProperty(value = "检查名称")
    private String checkName;
    @NotNull(message = "检查时间必填")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "检查时间",required = true)
    private Date checkTime;
    @NotNull(message = "所属厂区必填")
    @ApiModelProperty(value = "所属厂区id",required = true)
    private Long plantId;
    @NotNull(message = "所属区域必填")
    @ApiModelProperty(value = "所属区域id",required = true)
    private Long areaId;
    @NotNull(message = "位置必填")
    @ApiModelProperty(value = "位置id",required = true)
    private Long locationId;
    @NotNull(message = "所属组织必填")
    @ApiModelProperty(value = "所属组织id",required = true)
    private Long orgId;
    @NotBlank(message = "问题类别必填")
    @ApiModelProperty(value = "问题类别")
    private String problemCategory;
    @NotNull(message = "问题等级必填")
    @ApiModelProperty(value = "问题等级：1-低，2-一般，3-较大，4-重大")
    private Integer problemLevel;
    @NotNull(message = "是否当场处理必填")
    @ApiModelProperty(value = "是否当场处理：0-否，1-是")
    private Integer isImmediateHandle;
    @ApiModelProperty(value = "整改责任人")
    private String rectifyPersonEmpNo;
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "整改要求完成时间")
    private Date rectifyDeadline;
    @ApiModelProperty(value = "抄送人")
    private String ccPersonEmpNo;

    @ApiModelProperty(value = "问题描述列表")
    private List<YTSafetyHazardDescriptionDTO> descriptionList;

    @ApiModelProperty(value = "处理结果列表")
    private List<YTSafetyHazardResultDTO> resultList;
}
