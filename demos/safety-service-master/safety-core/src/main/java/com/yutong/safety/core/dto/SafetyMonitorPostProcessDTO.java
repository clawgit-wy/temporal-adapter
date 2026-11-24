package com.yutong.safety.core.dto;

import com.yutong.safety.common.param.web.ProcessParam;
import com.yutong.safety.common.param.web.WebPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SafetyMonitorPostProcessDTO  extends WebPageParam implements Serializable {

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

    @ApiModelProperty("位置/岗位")
    private String positionPost;

    @ApiModelProperty("镜头名称")
    private String lensName;

    @ApiModelProperty("方式途径")
    private String waysMeans;

    @ApiModelProperty("验证时间-开始")
    private String checkTimeStart;

    @ApiModelProperty("验证时间-结束")
    private String checkTimeEnd;

    @ApiModelProperty("异常事件类型")
    private String abnormalType;

    @ApiModelProperty("异常事件描述")
    private String abnormalDesc;

    @ApiModelProperty("异常事件接收人")
    private String abnormalCreator;

    @ApiModelProperty("异常事件接收人员工号")
    private String abnormalCreatorNo;

    @ApiModelProperty("异常事件接收人登录名")
    private String abnormalCreatorEip;

    @ApiModelProperty("异常事件接收人部门班组编号")
    private String abnormalDept;

    @ApiModelProperty("验证对象姓名")
    private String checkName;

    @ApiModelProperty("验证对象员工号")
    private String checkUserno;

    @ApiModelProperty("验证对象部门班组")
    private String checkDept;

    @ApiModelProperty("是否异常")
    private String checkException;

    @ApiModelProperty("是否考核兑现")
    private String examFulfill;

    @ApiModelProperty("整改处理结果")
    private String improveResult;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("处理时间")
    private Date auditTime;

    @ApiModelProperty("登记时间开始")
    private String createTimeStart;

    @ApiModelProperty("登记时间结束")
    private String createTimeEnd;

    @ApiModelProperty("处理时间开始")
    private Date auditTimeStart;

    @ApiModelProperty("处理时间结束")
    private Date auditTimeEnd;

    @ApiModelProperty("流程参数")
    private ProcessParam processParam;

    @ApiModelProperty("bpm流程页面地址前缀")
    private String bpmUrlPre;

    @ApiModelProperty("流程链接")
    private String flowUrl;
}
