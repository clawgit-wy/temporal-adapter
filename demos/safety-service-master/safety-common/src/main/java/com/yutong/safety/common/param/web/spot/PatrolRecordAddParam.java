package com.yutong.safety.common.param.web.spot;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author gwxiexj
 */
@Data
public class PatrolRecordAddParam implements Serializable {

    @ApiModelProperty("ID")
    private Integer id;

    @ApiModelProperty("流程ID")
    private String flowId;

    @ApiModelProperty("流程主题")
    private String title;

    @ApiModelProperty("流程提交人员工号")
    private String creator;

    @ApiModelProperty("流程创建时间")
    private Date createTime;

    @ApiModelProperty("流程发布时间")
    private Date publishTime;

    @ApiModelProperty("流程状态")
    private String status;

    @ApiModelProperty("事件编号")
    private String flowNo;

    @ApiModelProperty("上报人姓名")
    private String submitName;

    @ApiModelProperty("上报人员工号")
    private String submitNo;

    @ApiModelProperty("上报人部门班组")
    private String submitDept;

    @ApiModelProperty("事件发生时间")
    private String occurTime;

    @ApiModelProperty("厂区")
    private String factory;

    @ApiModelProperty("厂区ID")
    private Integer factoryId;

    @ApiModelProperty("事件来源 1-周期巡查 2-事件上报")
    private Integer eventSource;

    @ApiModelProperty("周期任务ID")
    private Integer taskId;

    @ApiModelProperty("任务名称")
    private String planName;

    @ApiModelProperty("任务ID")
    private Integer planId;

    @ApiModelProperty("周期任务开始日期")
    private String taskBeginDate;

    @ApiModelProperty("周期任务截止日期")
    private String taskEndDate;

    @ApiModelProperty("事件类型")
    private String eventType;

    @ApiModelProperty("事件类型ID")
    private Integer eventId;

    @ApiModelProperty("事项ID")
    private Integer teamId;

    @ApiModelProperty("事项")
    private String teamName;

    @ApiModelProperty("任务详情")
    private String planDesc;

    @ApiModelProperty("是否完成任务 0-否 1-是")
    private Integer isFinish;

    @ApiModelProperty("是否存在异常 0-否 1-是")
    private Integer isException;

    @ApiModelProperty("巡查图片")
    private String patrolImage;

    @ApiModelProperty("事件等级")
    private String eventLevel;

    @ApiModelProperty("责任部门")
    private String dutyDept;

    @ApiModelProperty("地理位置")
    private String eventAddress;

    @ApiModelProperty("是否重点部位 0-否 1-是")
    private Integer isKeynote;

    @ApiModelProperty("问题描述")
    private String problemDesc;

    @ApiModelProperty("问题图片")
    private String problemImage;

    @ApiModelProperty("是否当场处理 0-否 1-是")
    private Integer isScene;

    @ApiModelProperty("责任人登录名")
    private String dutyPersonNo;

    @ApiModelProperty("责任人")
    private String dutyPerson;

    @ApiModelProperty("保卫联系人登录名")
    private String guardPersonNo;

    @ApiModelProperty("保卫联系人")
    private String guardPerson;

    @ApiModelProperty("保卫联系人-电话")
    private String guardPhone;

    @ApiModelProperty("预计处理完成日期")
    private String planDealTime;

    @ApiModelProperty("计划处理措施")
    private String planMeasure;

    @ApiModelProperty("处理完成日期")
    private String dealTime;

    @ApiModelProperty("事件处理过程及结果")
    private String dealRecord;

    @ApiModelProperty("处理完成图片")
    private String dealImage;

    @ApiModelProperty("处理人用户名")
    private String dealPersonNo;

    @ApiModelProperty("处理人")
    private String dealPerson;

    @ApiModelProperty("处理人班组")
    private String dealPersonGroup;

    @ApiModelProperty("是否部门转派 0-否 1-是")
    private Integer isTransfer;

    @ApiModelProperty("转派人用户名")
    private String transferDept;

    @ApiModelProperty("事件处理结构及过程评分")
    private String resultScore;

    @ApiModelProperty("得分原因")
    private String resultDesc;

    @ApiModelProperty("评分人")
    private String resultPerson;

    @ApiModelProperty("评分人员工号")
    private String resultPersonNo;

    @ApiModelProperty("评分时间")
    private String resultTime;

    @ApiModelProperty("情况说明")
    private String resultRemark;

    @ApiModelProperty("当前登录人用户名")
    private String curPersonNo;

    @ApiModelProperty("节点标识 0-上报 1-治安管理员确认节点 2-设置计划日期节点 3-事件问题处理节点 4-治安管理员验证节点")
    private Integer nodeFlag;

    @ApiModelProperty("审批意见")
    private String auditNote;

    @ApiModelProperty("是否处理人转派 0-否 1-是")
    private Integer isDealTransfer;

    @ApiModelProperty("当前节点处理人 后端使用")
    private String curDealHandler;

    @ApiModelProperty("当前节点ID")
    private String curNodeId;
}
