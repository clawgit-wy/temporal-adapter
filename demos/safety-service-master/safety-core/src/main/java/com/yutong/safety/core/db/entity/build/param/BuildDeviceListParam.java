package com.yutong.safety.core.db.entity.build.param;

import com.yutong.safety.common.param.web.WebPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class BuildDeviceListParam  extends WebPageParam {

    @ApiModelProperty("门名称-分控器")
    private String deviceName;

    @ApiModelProperty("厂区编码")
    private String factoryCode;

    @ApiModelProperty("楼栋")
    private String building;

    @ApiModelProperty("是否拆除 0-否 1-是")
    private String removeFlag;

    @ApiModelProperty("状态 在线 离线")
    private String deviceStatus;

    @ApiModelProperty("保密等级")
    private String securityLevel;

    @ApiModelProperty("门禁编码")
    private String extendId;

    @ApiModelProperty("是否绝密 0-否 1-是")
    private String topSecretFlag;

    @ApiModelProperty("分页标记-不需前端传值")
    private Integer pageFlag;

    @ApiModelProperty("是否公共区域 0-否 1-是")
    private String isPublic;

    @ApiModelProperty("楼栋Code")
    private String anBuildingCode;

    @ApiModelProperty("设备类型 1-门禁 2-富士")
    private String deviceType;

    @ApiModelProperty("创建时间-起")
    private String createBegin;
    @ApiModelProperty("更新间-起")
    private String updateBegin;

    private List<String> deviceCodeList;

    private List<String> factoryList;

    private String securityLevelList;

    private String deviceCode;

}
