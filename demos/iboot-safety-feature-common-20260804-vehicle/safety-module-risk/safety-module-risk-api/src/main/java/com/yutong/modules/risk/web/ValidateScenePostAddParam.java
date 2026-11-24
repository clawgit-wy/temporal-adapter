package com.yutong.modules.risk.web;

import com.yutong.modules.risk.common.YTCheck;
import com.yutong.modules.risk.constants.YTResponseEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.common.util.PreconditonsEx;

import java.util.Objects;

@Data
public class ValidateScenePostAddParam implements YTCheck {

    @ApiModelProperty("ID")
    private Long id;
    @ApiModelProperty("数据类型 1-现场岗位 2-模拟岗位")
    private Integer dataType;
    @ApiModelProperty("事件编号")
    private String flowNo;
    @ApiModelProperty("工厂ID")
    private Long factoryId;
    @ApiModelProperty("工厂")
    private String factory;
    @ApiModelProperty("验证项目ID")
    private String teamId;
    @ApiModelProperty("验证项目")
    private String teamName;
    @ApiModelProperty("人员分类")
    private String personCategory;
    @ApiModelProperty("岗位/位置")
    private String postName;
    @ApiModelProperty("验证对象员工号")
    private String empNo;
    @ApiModelProperty("验证对象姓名")
    private String empName;
    @ApiModelProperty("验证对象部门班组")
    private String empDept;
    @ApiModelProperty("是否直系领导 0-是 1-否")
    private Integer leaderFlag;
    @ApiModelProperty("到达现场用时")
    private Integer reachDuration;
    @ApiModelProperty("直系领导员工号")
    private String leaderEmpNo;
    @ApiModelProperty("直系领导名称")
    private String leaderEmpName;
    @ApiModelProperty("验证项目")
    private String validateTeam;
    @ApiModelProperty("验证情况描述")
    private String validateDesc;
    @ApiModelProperty("验证时间-开始")
    private String validateBeginTime;
    @ApiModelProperty("验证时间-结束")
    private String validateEndTime;
    @ApiModelProperty("验证人员工号")
    private String createEmpNo;
    @ApiModelProperty("验证人姓名")
    private String createEmp;
    @ApiModelProperty("验证人班组")
    private String createDept;
    @ApiModelProperty("是否合格 0-否 1-是")
    private Integer isPass;
    @ApiModelProperty("是否考核 0-否  1-是")
    private Integer isExamine;
    @ApiModelProperty("整改结果")
    private String reformDesc;
    @ApiModelProperty("备注")
    private String remark;
    @ApiModelProperty("是否整改 0-否 1-是")
    private Integer isForm;
    @ApiModelProperty("节点标识")
    private String nodeFlag;
    @ApiModelProperty("EIP代办的modelId")
    private String processId;
    @ApiModelProperty("任务ID")
    private Long taskId;
    @ApiModelProperty("任务名称")
    private String planName;
    @ApiModelProperty("任务ID")
    private Long planId;
    @ApiModelProperty("周期任务开始日期")
    private String taskBeginDate;
    @ApiModelProperty("周期任务截止日期")
    private String taskEndDate;
    @ApiModelProperty("事件来源 1-周期任务 2-事件上报")
    private Integer eventSource;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(dataType), YTResponseEnum.PARAM_ERROR.getCode(), "“dataType”不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(isPass), YTResponseEnum.PARAM_ERROR.getCode(), "“isPass”不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(empNo), YTResponseEnum.PARAM_ERROR.getCode(), "“empNo”不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(personCategory), YTResponseEnum.PARAM_ERROR.getCode(), "“personCategory”不能为空！");
    }
}
