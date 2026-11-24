package com.yutong.safety.core.db.entity.visitor.param;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class SafetyVisitorAuthorizationConfigAddParam implements Check {
    @ApiModelProperty("id")
    private Integer id;

    @ApiModelProperty("类型，必传 0：组织， 1：人员")
    private Integer configType;

    @ApiModelProperty("员工号")
    private String empNo;

    @ApiModelProperty("组织路径编码")
    private String empOrgCodePath;

    @ApiModelProperty("组织路径名称")
    private String empOrgNamePath;

    @ApiModelProperty("授权人数")
    private Integer intervieweeNum;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(configType != null, ResponseEnum.FAIL.getCode(), "“访客授权类型”不能为空");
        if (configType == 1) {
            PreconditonsEx.checkArgument(StringUtils.isNotBlank(empNo), ResponseEnum.FAIL.getCode(), "“员工号”不能为空");
        } else {
            PreconditonsEx.checkArgument(StringUtils.isNotBlank(empOrgCodePath), ResponseEnum.FAIL.getCode(), "“组织路径编码”不能为空");
            PreconditonsEx.checkArgument(StringUtils.isNotBlank(empOrgNamePath), ResponseEnum.FAIL.getCode(), "“组织路径名称”不能为空");
        }
        PreconditonsEx.checkArgument(intervieweeNum != null, ResponseEnum.FAIL.getCode(), "“授权人数”不能为空");
    }
}
