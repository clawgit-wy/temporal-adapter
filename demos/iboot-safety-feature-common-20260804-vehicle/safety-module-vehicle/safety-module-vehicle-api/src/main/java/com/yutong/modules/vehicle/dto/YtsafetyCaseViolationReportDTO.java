package com.yutong.modules.vehicle.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="YtsafetyLocationInfoDTO对象", description="位置信息表DTO")
public class YtsafetyCaseViolationReportDTO {
    @ApiModelProperty(value = "雪花主键")
    private Long id;
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "时间")
    private Date violationTime;
    @ApiModelProperty(value = "名称")
    private String violationTitle;
    @ApiModelProperty(value = "检查人工号")
    private String inspectorNo;
    @ApiModelProperty(value = "检查人名称")
    private String inspectorName;
    @ApiModelProperty(value = "当事人类型")
    private Integer personConcernedType;
    @ApiModelProperty(value = "当事人名称")
    private String personConcernedName;
    @ApiModelProperty(value = "员工号/外协号/手机号")
    private String accountNo;
    @ApiModelProperty(value = "所属单位/部门主键")
    private Integer blongUnitId;
    @ApiModelProperty(value = "所属单位/部门编码")
    private String blongUnitCode;
    @ApiModelProperty(value = "所属单位/部门名称")
    private String blongUnitName;
    @ApiModelProperty(value = "责任部门主键")
    private Integer departId;
    @ApiModelProperty(value = "责任部门编码")
    private String departCode;
    @ApiModelProperty(value = "责任部门名称")
    private String departName;
    @ApiModelProperty(value = "违规类别主键")
    private Integer violationCategoryId;
    @ApiModelProperty(value = "违规类别名称")
    private String violationCategoryName;
    @ApiModelProperty(value = "违规事项主键")
    private Integer violationMatterId;
    @ApiModelProperty(value = "违规事项名称")
    private String violationMatterName;
    @ApiModelProperty(value = "违规性质主键")
    private Integer violationNatureId;
    @ApiModelProperty(value = "违规性质名称")
    private String violationNatureName;
    @ApiModelProperty(value = "处理方式")
    private Integer handlingMethod;
    @ApiModelProperty(value = "依旧制度")
    private String oldRule;
    @ApiModelProperty(value = "处理人工号")
    private String handlerNo;
    @ApiModelProperty(value = "处理人名称")
    private String handlerName;
    @ApiModelProperty(value = "POS流水号")
    private String posSerialNo;
    @ApiModelProperty(value = "处罚金额")
    private Float penaltyAmount;
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "交款日期")
    private Date paymentDate;
    @ApiModelProperty(value = "事项经过")
    private String incidentDesc;
    @ApiModelProperty(value = "处理结果")
    private String disposalResult;
    @ApiModelProperty(value = "凭证地址")
    private List<String> voucherUrls;
    @ApiModelProperty(value = "附件地址")
    private List<String> attachmentUrls;
}
