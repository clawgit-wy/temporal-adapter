package com.yutong.safety.core.db.entity.build.param;

import com.yutong.safety.common.param.web.WebPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BuildProcessOpenListParam extends WebPageParam {

    @ApiModelProperty("分页标记-不需前端传值")
    private Boolean pageFlag;

    @ApiModelProperty("对接部门")
    private String acrossDept;

    @ApiModelProperty("厂区编码")
    private String factoryCode;

    @ApiModelProperty("楼栋编码")
    private String buildingCode;

    @ApiModelProperty("门禁")
    private String deviceName;

    @ApiModelProperty("提交时间-起始")
    private String createTimeBegin;

    @ApiModelProperty("提交时间-截止")
    private String createTimeEnd;

    @ApiModelProperty("当前审核人登录名")
    private String curAuditLogin;

    @ApiModelProperty("主流程ID")
    private String processId;

    @ApiModelProperty("申请状态 1-通过 2-不通过")
    private String applyStatus;
}
