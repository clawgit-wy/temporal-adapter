package com.yutong.safety.core.db.entity.goodsOut.param;

import com.yutong.safety.common.param.Check;
import com.yutong.safety.core.db.entity.goodsOut.SafetyGoodsOutDetailGoodsDO;
import com.yutong.safety.core.db.entity.goodsOut.SafetyGoodsOutDetailReceiveDO;
import com.yutong.safety.core.db.entity.goodsOut.SafetyGoodsOutDetailVehicleDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class GoodsOutProcessUpdateParam implements Check {

    @ApiModelProperty("出门证信息ID")
    private String id;

    @ApiModelProperty("是否全部接收 1全部接收；2部分接收；0未接收")
    private String receiveStatus;

    @ApiModelProperty("接收确认照片")
    private String receiveConfirmImg;

    @ApiModelProperty("未接收原因")
    private String unReceiveReason;

    @ApiModelProperty("审核人确认是否接收")
    private String checkReceiveStatus;

    @ApiModelProperty("审核人-原因简要说明")
    private String checkReceiveReson;

    @ApiModelProperty("放行状态：0-待放行,1-已放行,2-放行异常,3-废弃")
    private String releaseStatus;

    @ApiModelProperty("放行人员工号")
    private String releaseEmpNo;

    @ApiModelProperty("放行人EIP用户名")
    private String releaseEmpEip;

    @ApiModelProperty("放行人")
    private String releaseEmp;

    @ApiModelProperty("放行时间")
    private String releaseTime;

    @ApiModelProperty("放行人部门")
    private String releaseEmpDept;

    @ApiModelProperty("放行异常原因")
    private String exceptionDesc;

    @ApiModelProperty("放行异常描述")
    private String exceptionRemark;

    @ApiModelProperty("流程状态 30-发布 20-审批中")
    private String status;

    @ApiModelProperty("放行现场照片")
    private String releaseImg;

    @ApiModelProperty("出厂原因证明材料")
    private String leaveData;

    @ApiModelProperty("是否有历史异常 是/否")
    private String isHistoryExce;

    @ApiModelProperty("当前处理人-用户名")
    private String curPersonLogin;

    @ApiModelProperty("转办给-用户名")
    private String transferTo;

    @ApiModelProperty("BPM流程ID")
    private String flowId;

    @ApiModelProperty("流程节点标识")
    private int nodeFlag;

    @ApiModelProperty("流程节点审批意见")
    private String auditNote;

    @ApiModelProperty("车辆信息")
    private List<SafetyGoodsOutDetailVehicleDO> vehicleList;

    @ApiModelProperty("物资信息")
    private List<SafetyGoodsOutDetailGoodsDO> goodsList;

    @ApiModelProperty("接收人清单")
    private List<SafetyGoodsOutDetailReceiveDO> receiveList;

    @ApiModelProperty("是否更新明细表")
    private Boolean isUpdateDetail;

    @ApiModelProperty("驳回跳转节点-默认N2")
    private String toNodeId;

    @ApiModelProperty("驳回通过后是否直接到达本节点")
    private Boolean isJumpBack;

    @ApiModelProperty("是否超时 1-质保部审批超时；2-审批人超时；3-审核人上级审批超时；4-门卫放行超时")
    private String isTimeOut;
    @ApiModelProperty("审核人上级员工号")
    private String superiorEmpNo;
    @ApiModelProperty("审核人上级EIP用户名")
    private String superiorEmpEip;
    @ApiModelProperty("审核人上级姓名")
    private String superiorEmpName;
    @ApiModelProperty("审核人上级部门")
    private String superiorDept;

    @ApiModelProperty("当前节点ID")
    private String curNodeId;
    @ApiModelProperty("当前节点名称")
    private String curNodeName;
    @ApiModelProperty("节点处理人姓名")
    private String curHandlerName;
    @ApiModelProperty("当前节点处理人-eip用户名")
    private String curNodeHandler;

    @ApiModelProperty(value = "审核人物资是否接收", example = "是")
    private String isReceiveShr;

    @ApiModelProperty(value = "审核人物资接收描述", example = "物资已成功接收")
    private String reasonShr;

    @ApiModelProperty(value = "审核人物资接收照片", example = "http://example.com/photo.jpg")
    private String photoShr;

    @Override
    public void check() {
    }
    @ApiModelProperty("更新时间")
    private Date publishTime;
    @ApiModelProperty("验证类型")
    private String businessType;
    @ApiModelProperty("异常类型")
    private String exceptionType;

    @ApiModelProperty(value = "是否短驳用车")
    private String shortBargeCar;
    @ApiModelProperty(value = "短驳运单号")
    private String shortBargeNum;
}
