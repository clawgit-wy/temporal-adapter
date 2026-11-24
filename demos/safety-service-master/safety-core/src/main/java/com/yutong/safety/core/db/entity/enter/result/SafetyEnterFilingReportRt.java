package com.yutong.safety.core.db.entity.enter.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class SafetyEnterFilingReportRt implements Serializable {

    @ApiModelProperty("备案ID")
    private String filingId;

    @ApiModelProperty("车牌号")
    private String fdCarNo;

    @ApiModelProperty("准入厂区")
    private String fdFilingFactory;

    @ApiModelProperty("准入时间-始")
    private String fdFilingBegin;

    @ApiModelProperty("准入时间-终")
    private String fdFilingEnd;

    @ApiModelProperty("接口人员工号")
    private String approveCode;

    @ApiModelProperty("接口人")
    private String approveName;

    @ApiModelProperty("入厂人员名称")
    private String fdPerName;

    @ApiModelProperty("入厂人员电话")
    private String fdPerPhone;

    @ApiModelProperty("供应商")
    private String fdSupplierName;

    @ApiModelProperty("货物名称")
    private String fdGoodsName;

    @ApiModelProperty("车主/运输公司")
    private String fdDriverName;

    @ApiModelProperty("车辆类型")
    private String tcmName;

    @ApiModelProperty("入厂时间")
    private String inTime;

    @ApiModelProperty("入厂地点")
    private String inLaneName;

    @ApiModelProperty("入厂大门")
    private String inOperatorName;

    @ApiModelProperty("出厂时间")
    private String outTime;

    @ApiModelProperty("出厂地点")
    private String outLaneName;

    @ApiModelProperty("出厂大门")
    private String outOperatorName;

    @ApiModelProperty("停车时长")
    private String stayLasts;

    @ApiModelProperty("入厂识别图片")
    private String inPicture;

    @ApiModelProperty("出厂识别图片")
    private String outPicture;

    @ApiModelProperty("入厂厂区")
    private String inFactory;

    @ApiModelProperty("车辆进厂状态")
    private String inStatus;

    @ApiModelProperty("备案期内进厂次数")
    private Integer enterNum;

    @ApiModelProperty("接口人部门")
    private String approveDepart;
}
