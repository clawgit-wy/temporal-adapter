package com.yutong.safety.common.param.web.spot;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import lombok.Data;

import java.util.Objects;

@Data
public class SpotCheckPlanSaveParam implements Check {

    private Integer id;
    private String planName;
    private String factoryId;
    private String factoryName;
    private Integer cycle;
    private Integer teamId;
    private String teamName;
    private String beginDate;
    private String endDate;
    private Integer status;
    private String dormancyBegin;
    private String dormancyEnd;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(teamName), ResponseEnum.PARAM_ERROR.getCode(), "“应用项目”不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(planName), ResponseEnum.PARAM_ERROR.getCode(), "“任务名称”不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(factoryName), ResponseEnum.PARAM_ERROR.getCode(), "“应用工厂”不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(cycle), ResponseEnum.PARAM_ERROR.getCode(), "“周期”不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(beginDate), ResponseEnum.PARAM_ERROR.getCode(), "“任务开始日期”不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(endDate), ResponseEnum.PARAM_ERROR.getCode(), "“任务结束日期”不能为空！");
    }
}
