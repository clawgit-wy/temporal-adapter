package com.yutong.safety.common.vo.web.spot;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 治安巡查-巡查记录统计
 */
@Data
public class PatrolRecordVO implements Serializable {

    @ApiModelProperty("id")
    private String id;
    @ApiModelProperty("事件编号")
    private String flowNo;
    @ApiModelProperty("bpm流程实例ID")
    private String flowId;
    @ApiModelProperty("流程状态")
    private String status;
    @ApiModelProperty("工厂ID")
    private Integer factoryId;
    @ApiModelProperty("工厂")
    private String factory;
    @ApiModelProperty("计划名称")
    private String planName;
    @ApiModelProperty("任务ID")
    private Integer taskId;
    @ApiModelProperty("事件来源 1-周期任务 2-事件上报")
    private Integer eventSource;
    @ApiModelProperty("类型ID")
    private Integer eventId;
    @ApiModelProperty("事件类型")
    private String eventType;
    @ApiModelProperty("事项ID")
    private Integer teamId;
    @ApiModelProperty("事项")
    private String teamName;
    @ApiModelProperty("是否存在异常 0-否 1-是")
    private Integer isException;
    @ApiModelProperty("事件等级")
    private String eventLevel;
    @ApiModelProperty("责任部门")
    private String dutyDept;
    @ApiModelProperty("创建时间")
    private String createTime;
    @ApiModelProperty("事件发生时间")
    private String occurTime;
    @ApiModelProperty("地理位置")
    private String eventAddress;
    @ApiModelProperty("是否重点部位 0-否 1-是")
    private Integer isKeynote;
    @ApiModelProperty("问题描述")
    private String problemDesc;
    @ApiModelProperty("时间现场图片")
    private String problemImage;
    @ApiModelProperty("预计处理完成日期")
    private String planDealTime;
    @ApiModelProperty("计划处理措施")
    private String planMeasure;
    @ApiModelProperty("是否当场处理 0-否 1-是")
    private Integer isScene;
    @ApiModelProperty("是否转派部门 0-否 1-是")
    private Integer isTransfer;
    @ApiModelProperty("转派部门")
    private String transferDept;
    @ApiModelProperty("处理人")
    private String dealPerson;
    @ApiModelProperty("处理人部门班组")
    private String dealPersonGroup;
    @ApiModelProperty("保卫联系人")
    private String guardPerson;
    @ApiModelProperty("处理完成时间")
    private String dealTime;
    @ApiModelProperty("处理完成图片")
    private String dealImage;
    @ApiModelProperty("事件处理结果及过程")
    private String dealRecord;
    @ApiModelProperty("事件处理结果及过程评分")
    private String resultScore;
    @ApiModelProperty("得分原因")
    private String resultDesc;
    @ApiModelProperty("评分人")
    private String resultPerson;
    @ApiModelProperty("是否延期处理 0-否 1-是")
    private Integer isDelay;
    @ApiModelProperty("事件上报人")
    private String submitName;
    @ApiModelProperty("上报人部门班组")
    private String submitDept;
    @ApiModelProperty("是否指挥中心派单 0-否 1-是")
    private Integer isAssign;
    @ApiModelProperty("指派人")
    private String assignPerson;
    @ApiModelProperty("是否抢单 0-否 1-是")
    private Integer isGrab;
    @ApiModelProperty("抢单人")
    private String grabPerson;
    @ApiModelProperty("接单时间")
    private String grabTime;
    @ApiModelProperty("是否派单 0-否 1-是")
    private Integer isDispatch;
    @ApiModelProperty("指派人")
    private String dispatchPerson;
    @ApiModelProperty("指派人员工号")
    private String dispatchPersonNo;
    @ApiModelProperty("返工次数")
    private Integer reworkNum;
    @ApiModelProperty("是否处理人转派 0-否 1-是")
    private Integer isDealTransfer;
    @ApiModelProperty("当前节点处理人，用户名逗号拼接")
    private String curDealHandler;
    @ApiModelProperty("车工号")
    private String carNumber;
    @ApiModelProperty("联系电话")
    private String telephone;
    @ApiModelProperty("司机")
    private String driverName;

    @ApiModelProperty("流程链接")
    private String flowUrl;
    @ApiModelProperty("出门证流程链接")
    private String GoodsOutFlowUrl;
}
