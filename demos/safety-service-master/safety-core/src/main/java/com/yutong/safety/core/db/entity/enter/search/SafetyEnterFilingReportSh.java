package com.yutong.safety.core.db.entity.enter.search;

import com.yutong.safety.common.param.Page;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SafetyEnterFilingReportSh extends Page implements Serializable {

    @ApiModelProperty("车牌号")
    private String fdCarNo;

    @ApiModelProperty("备案厂区")
    private String fdFilingFactory;

    @ApiModelProperty("备案厂区")
    private String fdFilingFactoryCode;

    @ApiModelProperty("准入时间-始")
    private String fdFilingBegin;

    @ApiModelProperty("准入时间-终")
    private String fdFilingEnd;

    @ApiModelProperty("接口人员工号")
    private String approveCode;

    @ApiModelProperty("接口人")
    private String approveName;

    @ApiModelProperty("接口人部门")
    private String approveDepart;

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
    private String inTimeBegin;
    private String inTimeEnd;

    @ApiModelProperty("入厂大门")
    private String inOperatorName;

    @ApiModelProperty("出厂时间")
    private String outTime;

    @ApiModelProperty("出厂大门")
    private String outOperatorName;

    @ApiModelProperty("停车时长")
    private String stayLasts;

    @ApiModelProperty("入厂厂区")
    private String inFactory;

    @ApiModelProperty("车辆进厂状态")
    private String inStatus;

    private String isAdmin;

    @ApiModelProperty("优化SQL专用")
    private List<Integer> idList;

    private String pageFlag;
}
