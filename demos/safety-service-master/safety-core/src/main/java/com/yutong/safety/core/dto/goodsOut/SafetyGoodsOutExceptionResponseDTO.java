package com.yutong.safety.core.dto.goodsOut;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class SafetyGoodsOutExceptionResponseDTO {
    @ApiModelProperty("主键ID")
    @ExcelIgnore
    private String id;

    @ApiModelProperty("出门证流程主键ID")
    @ExcelIgnore
    private String relatedProcessId;

    @ApiModelProperty("流程编号")
    @ExcelProperty("流程编号")
    private String flowNo;

    @ApiModelProperty("主题")
    @ExcelProperty("主题")
    private String subject;

    @ApiModelProperty("提交时间")
    @ExcelProperty("提交时间")
    private Date createTime;

    @ExcelProperty("异常类型")
    @ApiModelProperty("异常类型")
    private String exceptionType;

    @ExcelProperty("详细描述")
    @ApiModelProperty("详细描述")
    private String detailedDescription;

    @ExcelProperty("操作人姓名")
    @ApiModelProperty("操作人")
    private String operatorName;

    @ExcelProperty("操作人EIP")
    @ExcelIgnore
    private String operatorEip;

    @ExcelProperty("操作人code")
    @ExcelIgnore
    private String operatorCode;

    @ExcelProperty("异常创建时间")
    @ApiModelProperty("异常创建时间")
    private Date eventCreatedTime;

    @ExcelProperty("当前流程节点ID")
    @ExcelIgnore
    private String curNodeId;

    @ExcelProperty("当前流程节点名称")
    @ApiModelProperty("流程当前节点")
    private String curNodeName;

    @ExcelProperty("申请人姓名")
    @ApiModelProperty("申请人姓名")
    private String submitName;

    @ExcelProperty("申请人员工号")
    @ApiModelProperty("申请人员工号")
    private String submitNo;

    @ExcelProperty("申请人手机号")
    @ApiModelProperty("申请人手机号")
    private String submitPhone;

    @ExcelProperty("申请人部门")
    @ApiModelProperty("申请人部门")
    private String submitDept;

    @ExcelProperty("出厂厂区")
    @ApiModelProperty("出厂厂区")
    private String outFactoryName;

    @ExcelProperty("出门类型")
    @ApiModelProperty("出门类型")
    private String outTypeName;

    @ExcelProperty("审核人")
    @ApiModelProperty("审核人")
    private String examineEmpName;

    @ExcelProperty("审核人员工号")
    @ApiModelProperty("审核人员工号")
    private String examineEmpNo;

    @ExcelProperty("审核人部门")
    @ApiModelProperty("审核人部门")
    private String examineDept;

    @ExcelProperty("出门人类型")
    @ApiModelProperty("出门人类型")
    private String outPersonType;

    @ExcelProperty("出门人姓名")
    @ApiModelProperty("出门人姓名")
    private String gooutEmpName;

    @ExcelProperty("出门人手机号")
    @ApiModelProperty("出门人手机号")
    private String gooutPhone;

    @ExcelProperty("出门人员工号")
    @ApiModelProperty("出门人员工号")
    private String gooutEmpNo;

    @ExcelProperty("出门人部门")
    @ApiModelProperty("出门人部门")
    private String gooutEmpDept;

    @ExcelProperty("是否车辆运输")
    @ApiModelProperty("是否车辆运输")
    private String isVehicle;

    @ExcelProperty("车牌号")
    @ApiModelProperty("车牌号")
    private String vehicleNo;

    @ExcelProperty("是否需要接收人确认")
    @ApiModelProperty("是否需要接收人确认")
    private String isSetAccept;

    @ExcelProperty("接收人EIP")
    @ExcelIgnore
    private String receiveEmpEip;

    @ExcelProperty("接收人")
    @ApiModelProperty("接收人")
    private String receiveEmp;

    @ExcelProperty("现场照片")
    @ApiModelProperty("现场照片")
    private String sceneImg;

    @ExcelProperty("接收照片")
    @ApiModelProperty("接收照片")
    private String releaseImg;

    @ExcelProperty("物资是否接收")
    @ApiModelProperty("物资是否接收")
    private String receiveStatus;

    @ExcelProperty("未接收/超时接收原因")
    @ApiModelProperty("未接收/超时接收原因")
    private String unReceiveReason;

    @ExcelProperty("过磅单/证明材料")
    @ApiModelProperty("过磅单/证明材料")
    private String weightSheetUpload;

    @ExcelProperty("备注")
    @ApiModelProperty("备注")
    private String remarks;

    @ExcelIgnore
    @ApiModelProperty("放行人")
    private String releaseEmp;
    @ExcelIgnore
    @ApiModelProperty("放行时间")
    private String releaseTime;
    @ExcelIgnore
    @ApiModelProperty("放行状态")
    private String releaseStatus;

    @ExcelIgnore
    private String bpmUrlPre;//流程链接前缀
    @ExcelIgnore
    private String bpmFlowUrl;//流程链接
}
