package com.yutong.safety.common.param.web.spot;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Objects;

/**
 *
 * @author gwxiexj
 * @since 2024-05-24 09:46
 */
@Data
public class PatrolPlanAddParam implements Check {
    @ApiModelProperty("主键ID")
    private Integer id;
    @ApiModelProperty("任务类型 1-治安巡查 2-业务验证")
    private Integer planType;
    @ApiModelProperty("任务名称")
    private String planName;
    @ApiModelProperty("工厂ID")
    private String factoryId;
    @ApiModelProperty("工厂名称")
    private String factoryName;
    @ApiModelProperty("周期")
    private Integer cycle;
    @ApiModelProperty("次数")
    private Integer frequency;
    @ApiModelProperty("巡查类型ID")
    private Integer typeId;
    @ApiModelProperty("巡查类型")
    private String typeName;
    @ApiModelProperty("项目ID")
    private Integer teamId;
    @ApiModelProperty("项目")
    private String teamName;
    @ApiModelProperty("任务开始日期")
    private String beginDate;
    @ApiModelProperty("任务结束日期")
    private String endDate;
    @ApiModelProperty("任务状态 0-正常 1-暂停")
    private Integer status;
    @ApiModelProperty("备注")
    private String remark;
    @ApiModelProperty("休眠开始日期")
    private String dormancyBegin;
    @ApiModelProperty("休眠结束日期")
    private String dormancyEnd;
    @ApiModelProperty("更新人")
    private String updateEmp;
    @ApiModelProperty("更新人员工号")
    private String updateEmpNo;
    @ApiModelProperty("批量操作-ID拼接串")
    private String idStr;
    @ApiModelProperty("周期任务-应用角色ID")
    private Integer cycleRoleId;
    @ApiModelProperty("周期任务-应用角色名称")
    private String cycleRoleName;
    private String gwRwlx;


    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(planType), ResponseEnum.PARAM_ERROR.getCode(), "“任务类型”不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(planName), ResponseEnum.PARAM_ERROR.getCode(), "“任务名称”不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(factoryId), ResponseEnum.PARAM_ERROR.getCode(), "“应用工厂”不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(cycle), ResponseEnum.PARAM_ERROR.getCode(), "“周期”不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(beginDate), ResponseEnum.PARAM_ERROR.getCode(), "“任务开始日期”不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(endDate), ResponseEnum.PARAM_ERROR.getCode(), "“任务截止日期”不能为空！");
    }
}
