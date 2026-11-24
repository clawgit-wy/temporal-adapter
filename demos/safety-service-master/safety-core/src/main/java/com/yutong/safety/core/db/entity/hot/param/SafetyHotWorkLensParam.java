package com.yutong.safety.core.db.entity.hot.param;

import com.yutong.safety.common.param.web.WebPageParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "SafetyHotWorkLensParam", description = "查询监控设备信息")
public class SafetyHotWorkLensParam extends WebPageParam {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("全匹配")
    private String lensAll;

    @ApiModelProperty("镜头编号")
    private String lensNo;

    @ApiModelProperty("镜头编码")
    private String lensNum;

    @ApiModelProperty("镜头名称")
    private String lensName;

    @ApiModelProperty("镜头sn号")
    private String lensSn;

    @ApiModelProperty("镜头ip地址")
    private String lensIp;

    @ApiModelProperty("镜头型号")
    private String lensModel;

}
