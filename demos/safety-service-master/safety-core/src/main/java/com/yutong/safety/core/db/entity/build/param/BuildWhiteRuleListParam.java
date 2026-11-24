package com.yutong.safety.core.db.entity.build.param;

import com.yutong.safety.common.param.web.WebPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class BuildWhiteRuleListParam extends WebPageParam{

    @ApiModelProperty("分页标记-不需前端传值")
    private Boolean pageFlag;

    @ApiModelProperty("岗位或组织名称")
    private String searchName;

    @ApiModelProperty("检索条件-ID")
    private String idStr;

    private List<String> createReasons;
}
