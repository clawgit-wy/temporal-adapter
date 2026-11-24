package com.yutong.safety.core.db.entity.fireInspection.param;

import com.yutong.safety.common.param.web.WebPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class SafetyFireInspectionWorkStepQueryParams extends WebPageParam {

    @ApiModelProperty("作业环节名称")
    private String workStepName;

    @ApiModelProperty("作业环节编码")
    private String workStepCode;

    @ApiModelProperty("修改人")
    private String updateBy;

    @ApiModelProperty("作业部门编码-（单选-当前组织的Code）")
    private String workDeptCode;
}
