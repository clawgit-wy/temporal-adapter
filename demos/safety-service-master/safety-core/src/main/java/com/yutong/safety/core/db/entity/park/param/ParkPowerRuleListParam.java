package com.yutong.safety.core.db.entity.park.param;

import com.yutong.safety.common.param.web.WebPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 页面检索入参对象-加分页参数
 */
@Data
public class ParkPowerRuleListParam extends WebPageParam {

    @ApiModelProperty("组织名称")
    private String orgName;

    @ApiModelProperty("组织编码")
    private String orgCode;

    @ApiModelProperty("组织名称路径")
    private String orgNamePath;

    @ApiModelProperty("岗位名称")
    private String postName;

    @ApiModelProperty("授权厂区编码")
    private String factoryCode;

    @ApiModelProperty("授权厂区")
    private String factoryName;

    @ApiModelProperty("分页标记-后端使用")
    private Boolean pageFlag;

    @ApiModelProperty("删除标记-后端使用")
    private Boolean deleteFlag;
}
