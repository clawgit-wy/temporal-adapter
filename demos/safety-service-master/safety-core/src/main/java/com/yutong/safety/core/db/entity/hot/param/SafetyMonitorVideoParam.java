package com.yutong.safety.core.db.entity.hot.param;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Objects;

@Data
@ApiModel(value = "SafetyMonitorVideoParam", description = "查询监控设备视频信息")
public class SafetyMonitorVideoParam implements Check {

    @ApiModelProperty("动火流程id")
    private Long id;

    @ApiModelProperty("监控设备编号")
    private String lensNum;

    @ApiModelProperty("获取视频类型：preview直播，back回放")
    private String videoType;

    @ApiModelProperty("获取视频播放类型：ws、wss")
    private String urlType;

    @ApiModelProperty("码流类型：0主流、1子流 -直播使用")
    private Integer  streamType;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(id), ResponseEnum.PARAM_ERROR.getCode(), "动火流程不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(lensNum), ResponseEnum.PARAM_ERROR.getCode(), "监控设备编号不能为空！");
    }
}
