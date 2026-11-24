package com.yutong.safety.common.param.web.spot;

import com.yutong.safety.common.param.web.WebPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class PatrolRecordListParam extends WebPageParam implements Serializable {
    @ApiModelProperty("ID")
    private Integer id;
    @ApiModelProperty("事件来源 1-周期巡查 2-事件上报")
    private Integer eventSource;
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
    @ApiModelProperty("点检状态")
    private Integer status;
    @ApiModelProperty("事件编号")
    private String flowNo;
    @ApiModelProperty("上报人姓名")
    private String submitName;
    @ApiModelProperty("上报人员工号")
    private String submitNo;
    @ApiModelProperty("上报人部门班组")
    private String submitDept;
    @ApiModelProperty("厂区")
    private String factory;
    @ApiModelProperty("厂区ID")
    private Integer factoryId;
    @ApiModelProperty("任务名称")
    private String planName;
    @ApiModelProperty("事项ID")
    private Integer eventId;
    @ApiModelProperty("事项")
    private String eventType;
    @ApiModelProperty("巡检项目ID")
    private Integer teamId;
    @ApiModelProperty("巡检项目")
    private String teamName;
    @ApiModelProperty("事件等级")
    private String eventLevel;
    @ApiModelProperty("事件等级")
    private String dutyDept;
    @ApiModelProperty("地理位置")
    private String eventAddress;
    @ApiModelProperty("是否现场处理 0-否 1-是")
    private Integer isScene;
    @ApiModelProperty("是否转派 0-否 1-是")
    private Integer isTransfer;
    @ApiModelProperty("转派部门")
    private String transferDept;
    @ApiModelProperty("是否重点部位 0-否 1-是")
    private Integer isKeynote;
    @ApiModelProperty("事件发生开始")
    private String occurBegin;
    @ApiModelProperty("事件发生截止")
    private String occurEnd;
    @ApiModelProperty("预计处理日期开始")
    private String planDealBegin;
    @ApiModelProperty("预计处理日期截止")
    private String planDealEnd;
    @ApiModelProperty("事件上报日期开始")
    private String createBegin;
    @ApiModelProperty("事件上报日期截止")
    private String createEnd;
    @ApiModelProperty("处理完成日期开始")
    private String dealFinishBegin;
    @ApiModelProperty("处理完成日期截止")
    private String dealFinishEnd;
    @ApiModelProperty("处理人部门班组")
    private String dealPersonGroup;
    @ApiModelProperty("处理人")
    private String dealPerson;
    @ApiModelProperty("是否抢单 0-否 1-是")
    private Integer isGrab;
    @ApiModelProperty("是否指挥中心派单 0-否 1-是")
    private Integer isAssign;
    @ApiModelProperty("工厂ID拼接-后台使用")
    private String factoryIdStr;
    @ApiModelProperty("分页标记-后台使用")
    private Integer begin;
    @ApiModelProperty("任务ID拼接-后台使用")
    private String taskIdStr;

    @ApiModelProperty("保卫联系人")
    private String guardPerson;

    @ApiModelProperty("bpm流程页面地址前缀")
    private String bpmUrlPre;
    @ApiModelProperty("出门证异常上报-bpm流程页面地址前缀")
    private String goodsExceptionBpmUrlPre;
    @ApiModelProperty("出门证-bpm流程页面地址前缀")
    private String goodsOutBpmUrlPre;
    @ApiModelProperty("类型 1-治安巡查 2-业务验证")
    private Integer planType;

    private String nodeIdIsNull;

}
