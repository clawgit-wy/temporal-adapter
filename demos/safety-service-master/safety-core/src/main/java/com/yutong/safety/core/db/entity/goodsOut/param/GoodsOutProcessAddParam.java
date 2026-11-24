package com.yutong.safety.core.db.entity.goodsOut.param;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import com.yutong.safety.core.db.entity.goodsOut.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
public class GoodsOutProcessAddParam implements Check {

    @ApiModelProperty("出门证信息ID")
    private String id;

    @ApiModelProperty("主题")
    private String subject;

    @ApiModelProperty("触发标识 1-EIP 2-掌上宇通 3-掌宇小程序")
    private String systemFlag;

    @ApiModelProperty("流程创建人-EIP用户名")
    private String creator;

    @ApiModelProperty("流程创建人-创建时间")
    private String createTime;

    @ApiModelProperty("提交人部门")
    private String submitDept;

    @ApiModelProperty("提交人姓名")
    private String submitName;

    @ApiModelProperty("提交人员工号")
    private String submitNo;

    @ApiModelProperty("提交人手机号-小程序")
    private String submitPhone;

    @ApiModelProperty("提交人身份证号-小程序")
    private String submitCardNo;

    @ApiModelProperty("出厂厂区ID")
    private Integer outFactoryId;

    @ApiModelProperty("出厂厂区")
    private String outFactoryName;

    @ApiModelProperty("出厂类型ID-字典")
    private Integer outTypeId;

    @ApiModelProperty("出厂类型")
    private String outTypeName;

    @ApiModelProperty("出门人员类型：1-公司内部人员; 2-外部人员")
    private String outPersonType;

    @ApiModelProperty("审核人EIP用户名")
    private String examineEmpEip;

    @ApiModelProperty("审核人")
    private String examineEmpName;

    @ApiModelProperty("审核人员工号")
    private String examineEmpNo;

    @ApiModelProperty("审核人部门")
    private String examineDept;

    @ApiModelProperty("审核人电话")
    private String examinePhone;

    @ApiModelProperty("物资出厂权限")
    private String outPowerDesc;

    @ApiModelProperty("外部人出门人姓名")
    private String outsideName;

    @ApiModelProperty("外部人联系电话")
    private String outsidePhone;

    @ApiModelProperty("提货单位名称")
    private String deliveryUnitName;

    @ApiModelProperty("出门人员工号")
    private String gooutEmpNo;

    @ApiModelProperty("出门人姓名")
    private String gooutEmpName;

    @ApiModelProperty("出门人联系电话")
    private String gooutPhone;

    @ApiModelProperty("出厂人员部门")
    private String gooutEmpDept;

    @ApiModelProperty("是否车辆运输 是 否")
    private String isVehicle;

    @ApiModelProperty("车牌号")
    private String vehicleNo;

    @ApiModelProperty("流程ID")
    private String flowId;

    @ApiModelProperty("物资类别")
    private String goodsType;

    @ApiModelProperty("原因类别")
    private String reasonType;

    @ApiModelProperty("原因说明")
    private String reasonDesc;

    @ApiModelProperty("是否需要设置接收人 是 否")
    private String isSetAccept;

    @ApiModelProperty("质保部审批人")
    private String qaExamineEmp;

    @ApiModelProperty("质保部审批人员工号")
    private String qaExamineEmpNo;

    @ApiModelProperty("质保部审批人EIP")
    private String qaExamineEmpEip;

    @ApiModelProperty("现场照片")
    private String sceneImg;

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

    @ApiModelProperty("出厂原因证明材料")
    private String leaveData;

    @ApiModelProperty("出厂原因证明材料-图片")
    private String leaveDataImg;

    @ApiModelProperty("是否有EIP出厂原因证明材料 0-否 1-是")
    private String leaveDataFlag;

    @ApiModelProperty("过磅单材料")
    private String weightDataUrl;

    @ApiModelProperty("是否有过磅单材料")
    private String weightDataFlag;

    @ApiModelProperty("车辆信息")
    private List<SafetyGoodsOutDetailVehicleDO> vehicleList;

    @ApiModelProperty("物资信息")
    private List<SafetyGoodsOutDetailGoodsDO> goodsList;

    @ApiModelProperty("接收人清单")
    private List<SafetyGoodsOutDetailReceiveDO> receiveList;

    @ApiModelProperty("流程节点标识")
    private int nodeFlag;

    @ApiModelProperty("流程状态 30-发布 20-审批中")
    private String status;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("放行人用户名-后端指定")
    private String releaseEmpEip;
    @ApiModelProperty("是否超时 1-废料销售确认超时；2-质保部审批超时；3-审批人审批超时；4-审批人上级超时；5-门卫放行超时")
    private String isTimeOut;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(subject), ResponseEnum.PARAM_ERROR.getCode(), "“主题”不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(creator), ResponseEnum.PARAM_ERROR.getCode(), "“流程创建人-EIP用户名”不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(outFactoryId), ResponseEnum.PARAM_ERROR.getCode(), "“出厂厂区ID”不能为空！");
    }

    @ApiModelProperty("审核人权限id")
    private Integer authId;

    @ApiModelProperty(value = "微机管理员员工号")
    private String microManagerNo;

    @ApiModelProperty(value = "微机管理员姓名")
    private String microManagerName;

    @ApiModelProperty(value = "微机管理员用户名")
    private String microManagerEip;

    @ApiModelProperty(value = "微机管理员手机号")
    private String microManagerPhone;

    @ApiModelProperty(value = "eip文件名")
    private String fileName;

    @ApiModelProperty(value = "是否短驳用车")
    private String shortBargeCar;
    @ApiModelProperty(value = "短驳运单号")
    private String shortBargeNum;

    @ApiModelProperty(value = "物资清单附件")
    private String goodsFileUrl;

}
