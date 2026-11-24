package com.yutong.safety.common.param.app;


import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class EipWriteBackParam implements Check {

    @ApiModelProperty("员工号")
    private String empNo;

    @ApiModelProperty("EIP流程链接")
    private String eipUrl;

    @ApiModelProperty("流程状态， 1：待审，2：发布，3：其他")
    private String status;

    private Long id;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(StringUtils.isNotBlank(empNo), ResponseEnum.FAIL.getCode(), "员工号不能为空");
        PreconditonsEx.checkArgument(StringUtils.isNotBlank(eipUrl), ResponseEnum.FAIL.getCode(), "EIP流程链接不能为空");
        PreconditonsEx.checkArgument(StringUtils.isNotBlank(status), ResponseEnum.FAIL.getCode(), "流程状态不能为空");
    }
}
