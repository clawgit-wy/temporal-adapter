package com.yutong.safety.common.param.web;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class ProcessParam  implements Check {
    private String subject;
    private String flowId;
    private String modelId;
    private String formId;
    private String fdParameterJson;
    private String type; // add / audit

    @Override
    public void check() {
        PreconditonsEx.checkArgument(StringUtils.isNotEmpty(type), ResponseEnum.PARAM_ERROR.getCode(), "type不能为空！");
        PreconditonsEx.checkArgument(StringUtils.isNotEmpty(formId), ResponseEnum.PARAM_ERROR.getCode(), "formId不能为空！");
        if("add".equals(type)){
            PreconditonsEx.checkArgument(StringUtils.isNotEmpty(subject), ResponseEnum.PARAM_ERROR.getCode(), "流程主题不能为空！");
        }else{
            PreconditonsEx.checkArgument(StringUtils.isNotEmpty(fdParameterJson), ResponseEnum.PARAM_ERROR.getCode(), "审批参数不能为空！");
            PreconditonsEx.checkArgument(StringUtils.isNotEmpty(flowId), ResponseEnum.PARAM_ERROR.getCode(), "流程ID不能为空！");
            PreconditonsEx.checkArgument(StringUtils.isNotEmpty(modelId), ResponseEnum.PARAM_ERROR.getCode(), "modelId不能为空！");
        }
    }
}
