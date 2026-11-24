package com.yutong.safety.core.dto;

import com.yutong.safety.common.param.web.ProcessParam;
import com.yutong.safety.common.param.web.WebPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SafetySpotCheckProcessDTO extends WebPageParam implements Serializable {

    @ApiModelProperty("流程表主键ID")
    private String id;
    @ApiModelProperty("流程ID")
    private String flowId;
    @ApiModelProperty("流程主题")
    private String title;
    @ApiModelProperty("流程创建人员工号")
    private String creator;
    @ApiModelProperty("流程创建时间")
    private Date createTime;
    @ApiModelProperty("流程发布时间")
    private Date publishTime;
    @ApiModelProperty("流程状态")
    private String status;

    @ApiModelProperty("设备点检人姓名")
    private String checkName;
    @ApiModelProperty("设备点检人员工号")
    private String checkEmpNo;
    @ApiModelProperty("设备点检部门")
    private String dutyName;
    @ApiModelProperty("设备点检时间")
    private String checkTime;
    @ApiModelProperty("验证对象-项目")
    private String teamName;
    @ApiModelProperty("验证对象-设备ID")
    private String deviceId;
    @ApiModelProperty("验证对象-设备名称")
    private String deviceName;
    @ApiModelProperty("验证对象-设备编号")
    private String deviceCode;
    @ApiModelProperty("验证对象-位置")
    private String location;
    @ApiModelProperty("验证对象-整改完成时间")
    private String reformTime;
    @ApiModelProperty("整改责任人登录名")
    private String reformUser;
    @ApiModelProperty("整改责任人信息")
    private String reformUserInfo;
    @ApiModelProperty("整改责任人电话")
    private String reformUserPhone;
    @ApiModelProperty("整改责任人上级登录名")
    private String reformUserLeader;
    @ApiModelProperty("整改是否验证通过_是否返回上个节点")
    private String checkResult;

    @ApiModelProperty("点检信息描述_获取点检项是否必填")
    private String ruleDOS;
    @ApiModelProperty("点检信息用户填写信息_获取校验项名称及描述_回传及展示点检信息")
    private String recordRuleDOS;
    @ApiModelProperty("整改记录回传")
    private String resultDTOS;

    @ApiModelProperty("流程参数")
    private ProcessParam processParam;
    @ApiModelProperty("登录名")
    private String loginName;
    @ApiModelProperty("工厂")
    private String factoryId;
    @ApiModelProperty("节点标识 1治安管理员 2整改责任人 3验证节点")
    private String nodeFlag;
    @ApiModelProperty("是否转派")
    private String commissionFlag;
    @ApiModelProperty("是否需要整改")
    private Integer needReform;
    @ApiModelProperty("划验证人")
    private String planValidateEmp;
    @ApiModelProperty("计划验证人员工号")
    private String planValidateEmpNo;
    @ApiModelProperty("计划验证人登录名")
    private String planValidateEmpLogin;

    @ApiModelProperty("设备预设点检人工号")
    private String deviceCheckNo;
    @ApiModelProperty("设备预设点检人姓名")
    private String deviceCheckName;
    @ApiModelProperty("设备预设点检人登录名")
    private String deviceCheckLogin;

    @ApiModelProperty("BPM流程审批意见")
    private String auditNoteMsg;
    @ApiModelProperty("验证详情")
    private String validRemark;
}
