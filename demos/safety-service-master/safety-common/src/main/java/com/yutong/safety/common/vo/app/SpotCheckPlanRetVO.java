package com.yutong.safety.common.vo.app;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class SpotCheckPlanRetVO implements Serializable {
    @ApiModelProperty("返回类型 0-正常 1-无权限 2-已点检流程未完成 3-已点检流程已完成")
    private Integer retType;
    @ApiModelProperty("返回值: retType=0 recordID拼接串 retType=2 bpm审批url retType=3 recordId")
    private String data;
}
