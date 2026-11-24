package com.yutong.modules.risk.web;

import com.yutong.modules.risk.common.YTCheck;
import com.yutong.modules.risk.constants.YTResponseEnum;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.util.PreconditonsEx;

@Data
public class ProcessParam implements YTCheck {
    private String subject;
    private String flowId;
    private String modelId;
    private String formId;
    private String fdParameterJson;
    private String type; // add / audit
    /**
     * ytsafety_summary_process表实例id
     */
    private Long formInstanceId;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(StringUtils.isNotEmpty(type), YTResponseEnum.PARAM_ERROR.getCode(), "type不能为空！");
        PreconditonsEx.checkArgument(StringUtils.isNotEmpty(formId), YTResponseEnum.PARAM_ERROR.getCode(), "formId不能为空！");
        if("add".equals(type)){
            PreconditonsEx.checkArgument(StringUtils.isNotEmpty(subject), YTResponseEnum.PARAM_ERROR.getCode(), "流程主题不能为空！");
        }else{
            PreconditonsEx.checkArgument(StringUtils.isNotEmpty(fdParameterJson), YTResponseEnum.PARAM_ERROR.getCode(), "审批参数不能为空！");
            PreconditonsEx.checkArgument(StringUtils.isNotEmpty(flowId), YTResponseEnum.PARAM_ERROR.getCode(), "流程ID不能为空！");
            PreconditonsEx.checkArgument(StringUtils.isNotEmpty(modelId), YTResponseEnum.PARAM_ERROR.getCode(), "modelId不能为空！");
        }
    }
}
