package com.yutong.safety.core.db.entity.goodsOut.param;

import com.yutong.safety.common.param.web.WebPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class GoodsOutProcessListParam  extends WebPageParam {

    @ApiModelProperty("申请人-姓名/员工号/联系电话-精确查询")
    private String submitEmp;

    @ApiModelProperty("出门人-姓名/员工号/联系电话")
    private String goOutEmp;

    @ApiModelProperty("审核人-姓名/员工号/联系电话")
    private String examineEmp;

    @ApiModelProperty("f放行人-姓名/员工号/联系电话")
    private String releaseEmp;

    @ApiModelProperty("f放行人-EIP登录名拼接")
    private String releaseEmpEip;

    @ApiModelProperty("是否车辆运输 是 否")
    private String isVehicle;

    @ApiModelProperty("车牌号")
    private String vehicleNo;

    @ApiModelProperty("流程状态 30-发布 20-审批中 00-废弃")
    private String status;

    @ApiModelProperty("出厂厂区")
    private String outFactoryName;

    @ApiModelProperty("出厂厂区ID")
    private String outFactoryId;

    @ApiModelProperty("目的厂区")
    private String targetFactoryName;

    @ApiModelProperty("目的厂区ID")
    private String targetFactoryId;

    @ApiModelProperty("出厂类型")
    private String outTypeName;

    @ApiModelProperty("物资类别")
    private String goodsType;

    @ApiModelProperty("原因类别")
    private String reasonType;

    @ApiModelProperty("流程编号")
    private String flowNo;

    @ApiModelProperty("主题")
    private String subject;

    @ApiModelProperty("提交人部门")
    private String submitDept;

    @ApiModelProperty("出厂人员部门")
    private String gooutEmpDept;

    @ApiModelProperty("审核人部门")
    private String examineDept;

    @ApiModelProperty("放行状态：0-待放行,1-已放行,2-放行异常,3-废弃")
    private String releaseStatus;

    @ApiModelProperty("提交时间-起")
    private String createBegin;

    @ApiModelProperty("提交时间-止")
    private String createEnd;

    @ApiModelProperty("出厂厂区ID拼接-后台使用")
    private String outFactoryIds;

    @ApiModelProperty("分页标识-后台使用")
    private Integer begin;

    private List<String> idList;

    @ApiModelProperty("流程节点限制-后端使用")
    private String curNodeId;
    @ApiModelProperty("流程节点处理人-后端使用")
    private String curNodeHandler;
    @ApiModelProperty("要排除的nodeFlag")
    private String unNodeFlag;
    @ApiModelProperty("要排除的curNodeId")
    private String unCurNodeId;

    @ApiModelProperty("当前处理人-用户名")
    private String curPersonLogin;
    @ApiModelProperty("查询")
    private String isOwner;

    private String creator;
    private String submitPhone;
    private String flowId;

    @ApiModelProperty(value = "是否短驳用车")
    private String shortBargeCar;

    @ApiModelProperty(value = "短驳运单号")
    private String shortBargeNum;

    @ApiModelProperty(value = "车辆类型")
    private String vehicleType;
}
