package com.yutong.safety.common.param.web.spot;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Objects;

/**
 * 业务验证-货车环保标准抽查
 * @author gwxiexj
 * @since 2024-06-11 10:35
 */
@Data
public class ValidateEnvironmentAddParam implements Check {

    @ApiModelProperty("事件编号")
    private String flowNo;
    @ApiModelProperty("工厂ID")
    private Integer factoryId;
    @ApiModelProperty("工厂")
    private String factory;
    @ApiModelProperty("抽查时间")
    private String checkTime;
    @ApiModelProperty("位置")
    private String location;
    @ApiModelProperty("车牌号")
    private String carNumber;
    @ApiModelProperty("驾驶员姓名")
    private String driverName;
    @ApiModelProperty("联系电话")
    private String telephone;
    @ApiModelProperty("环保标准")
    private String emissionStandard;
    @ApiModelProperty("是否加入黑名单 0-否 1-是")
    private Integer isBlacklist;
    @ApiModelProperty("抽查人")
    private String checkPerson;
    @ApiModelProperty("抽查人员工号")
    private String checkPersonNo;
    @ApiModelProperty("抽查人部门")
    private String checkDept;
    @ApiModelProperty("加入黑名单时间")
    private String blackTime;
    @ApiModelProperty("加入黑名单结果")
    private String blackResult;
    @ApiModelProperty("出入口接口返回值")
    private String interfaceStatus;
    @ApiModelProperty("更新人员工号")
    private String updatePersonNo;
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
    @ApiModelProperty("事件来源 1-周期任务 2-事件上报")
    private Integer eventSource;
    @ApiModelProperty("业务类型")
    private String businessType;
    @ApiModelProperty("业务类型code")
    private String businessTypeCode;

    private String creator;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(factoryId), ResponseEnum.PARAM_ERROR.getCode(), "“厂区”不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(location), ResponseEnum.PARAM_ERROR.getCode(), "“位置”不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(checkTime), ResponseEnum.PARAM_ERROR.getCode(), "“抽查时间”不能为空！");
        //PreconditonsEx.checkArgument(Objects.nonNull(driverName), ResponseEnum.PARAM_ERROR.getCode(), "“驾驶员姓名”不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(carNumber), ResponseEnum.PARAM_ERROR.getCode(), "“车牌号”不能为空！");
        //PreconditonsEx.checkArgument(Objects.nonNull(telephone), ResponseEnum.PARAM_ERROR.getCode(), "“联系电话”不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(planId), ResponseEnum.PARAM_ERROR.getCode(), "“planId”不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(taskBeginDate), ResponseEnum.PARAM_ERROR.getCode(), "“taskBeginDate”不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(taskEndDate), ResponseEnum.PARAM_ERROR.getCode(), "“taskEndDate”不能为空！");
    }
}
