package com.yutong.safety.core.db.entity.hot.param;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Objects;

@Data
@ApiModel(value = "SyncFireApproveListParam", description = "查询动火作业列表")
public class SyncFireApproveListParam implements Check {

    @ApiModelProperty(value = "员工号")
    private String empNo;

    @ApiModelProperty(value = "厂区")
    private String region;

    @ApiModelProperty(value = "动火级别")
    private String fireLevel;

    @ApiModelProperty(value = "公共参数：供应商、镜头信息、项目负责人")
    private String commonParam;


    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(empNo), ResponseEnum.PARAM_ERROR.getCode(), "员工号不能为空！");
    }

}
