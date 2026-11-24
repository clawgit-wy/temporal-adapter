package com.yutong.safety.core.db.entity.goodsOut;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SafetyGoodsOutProcessDO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private Date createTime;
    private Date publishTime;
    @ApiModelProperty("创建人")
    private String creator;
    @ApiModelProperty("提交人部门")
    private String submitDept;
    @ApiModelProperty("提交人")
    private String submitName;
    @ApiModelProperty("提交人员工号")
    private String submitNo;
    @ApiModelProperty("提交人电话")
    private String submitPhone;
    @ApiModelProperty("触发标识  1-EIP 2-掌上宇通 3-掌宇小程序 4-EIP流程")
    private String systemFlag;
    @ApiModelProperty("出厂厂区-ID")
    private Integer outFactoryId;
    @ApiModelProperty("出厂厂区")
    private String outFactoryName;
    @ApiModelProperty("出厂类型-字典ID")
    private Integer outTypeId;
    @ApiModelProperty("出厂类型")
    private String outTypeName;
    @ApiModelProperty("出门人员类型：公司内部人员;外部人员")
    private String outPersonType;
    @ApiModelProperty("审核人EIP用户名")
    private String examineEmpEip;
    @ApiModelProperty("审核人")
    private String examineEmpName;
    @ApiModelProperty("审核人员工号")
    private String examineEmpNo;
    @ApiModelProperty("审核人联系电话")
    private String examinePhone;
    @ApiModelProperty("审核人部门")
    private String examineDept;
    @ApiModelProperty("物资出厂权限")
    private String outPowerDesc;
    @ApiModelProperty("外部人出门人姓名")
    private String outsideName;
    @ApiModelProperty("外部人联系电话")
    private String outsidePhone;
    @ApiModelProperty("提货单位名称")
    private String deliveryUnitName;
    @ApiModelProperty("外出人员工号")
    private String gooutEmpNo;
    @ApiModelProperty("出门人姓名")
    private String gooutEmpName;
    @ApiModelProperty("联系电话")
    private String gooutPhone;
    @ApiModelProperty("出厂人员部门")
    private String gooutEmpDept;
    @ApiModelProperty("是否车辆运输 是 否")
    private String isVehicle;
    @ApiModelProperty("车牌号")
    private String vehicleNo;
    @ApiModelProperty("bpm流程ID")
    private String flowId;
    @ApiModelProperty("物资类别")
    private String goodsType;
    @ApiModelProperty("原因类别")
    private String reasonType;
    @ApiModelProperty("是否需要设置接收人 是 否")
    private String isSetAccept;
    @ApiModelProperty("质保部审批人")
    private String qaExamineEmp;
    @ApiModelProperty("质保部审批人员工号")
    private String qaExamineEmpNo;
    @ApiModelProperty("质保部审批人EIP")
    private String qaExamineEmpEip;
    @ApiModelProperty("原因说明")
    private String reasonDesc;
    @ApiModelProperty("是否全部接收 1-全部接收；2-部分接收；0-未接收")
    private String receiveStatus;
    @ApiModelProperty("接收确认照片")
    private String receiveConfirmImg;
    @ApiModelProperty("未接收原因")
    private String unReceiveReason;
    @ApiModelProperty("审核人确认是否接收")
    private String checkReceiveStatus;
    @ApiModelProperty("审核人-原因简要说明")
    private String checkReceiveReson;
    @ApiModelProperty("现场照片")
    private String sceneImg;
    @ApiModelProperty("")
    private String leaveData;
    @ApiModelProperty("")
    private String leaveDataImg;
    @ApiModelProperty("是否有EIP出厂原因证明材料")
    private String leaveDataFlag;
    @ApiModelProperty("放行状态：0-待放行,1-已放行,2-放行异常,3-废弃")
    private String releaseStatus;
    @ApiModelProperty("放行人员工号")
    private String releaseEmpNo;
    @ApiModelProperty("放行人EIP用户名")
    private String releaseEmpEip;
    @ApiModelProperty("放行人")
    private String releaseEmp;
    @ApiModelProperty("放行人联系电话")
    private String releasePhone;
    @ApiModelProperty("放行时间")
    private String releaseTime;
    @ApiModelProperty("放行人部门")
    private String releaseEmpDept;
    @ApiModelProperty("放行图片")
    private String releaseImg;
    @ApiModelProperty("放行异常原因")
    private String exceptionDesc;
    @ApiModelProperty("放行异常描述")
    private String exceptionRemark;
    @ApiModelProperty("当前节点处理人-bpm")
    private String curNodeHandler;
    @ApiModelProperty("当前节点处理人姓名-bpm")
    private String curHandlerName;
    @ApiModelProperty("当前流程节点-bpm")
    private String curNodeId;
    @ApiModelProperty("当前流程节点名称-bpm")
    private String curNodeName;
    @ApiModelProperty("流程状态 30-发布 20-审批中")
    private String status;
    @ApiModelProperty("车辆类别")
    private String vehicleType;
    @ApiModelProperty("是否携带物资出厂: 是 否")
    private String isGoods;
    @ApiModelProperty("是否携带备胎 是,否")
    private String isSpareTire;
    @ApiModelProperty("备胎数量")
    private Integer spareTireNum;
    @ApiModelProperty("流程编号")
    private String flowNo;
    @ApiModelProperty("主题")
    private String subject;
    @ApiModelProperty("是否有历史异常 是，否")
    private String isHistoryExce;
    @ApiModelProperty("流程节点标识")
    private Integer nodeFlag;
    @ApiModelProperty("过磅单材料")
    private String weightDataUrl;
    @ApiModelProperty("车辆清单")
    private String weightSheetUpload;
    @ApiModelProperty("是否有过磅单材料")
    private String weightDataFlag;
    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("接收人EIP拼接")
    private String receiveEipStr;
    @ApiModelProperty("是否超时0-正常")
    private String isTimeOut;

    @ApiModelProperty("审核人上级员工号")
    private String superiorEmpNo;
    @ApiModelProperty("审核人上级EIP用户名")
    private String superiorEmpEip;
    @ApiModelProperty("审核人上级姓名")
    private String superiorEmpName;
    @ApiModelProperty("审核人上级部门")
    private String superiorDept;

    @ApiModelProperty(value = "审核人物资是否接收", example = "是")
    private String isReceiveShr;

    @ApiModelProperty(value = "审核人物资接收描述", example = "物资已成功接收")
    private String reasonShr;

    @ApiModelProperty(value = "审核人物资接收照片", example = "http://example.com/photo.jpg")
    private String photoShr;

    @ApiModelProperty(value = "微机管理员员工号")
    private String microManagerNo;

    @ApiModelProperty(value = "微机管理员姓名")
    private String microManagerName;

    @ApiModelProperty(value = "微机管理员用户名")
    private String microManagerEip;

    @ApiModelProperty(value = "微机管理员手机号")
    private String microManagerPhone;

    @ApiModelProperty(value = "是否短驳用车")
    private String shortBargeCar;

    @ApiModelProperty(value = "短驳运单号")
    private String shortBargeNum;

    @ApiModelProperty("验证类型")
    private String businessType;
    @ApiModelProperty("异常类型")
    private String exceptionType;
}