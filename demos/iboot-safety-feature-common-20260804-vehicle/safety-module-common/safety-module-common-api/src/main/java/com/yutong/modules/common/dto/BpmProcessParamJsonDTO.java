package com.yutong.modules.common.dto;

import com.yutong.modules.common.entity.bpm.Operation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@ApiModel(value="流程执行请求参数", description="流程执行请求参数")
public class BpmProcessParamJsonDTO {

    /**
     * 实例主键
     */
    @ApiModelProperty(value = "实例主键")
    private Long formInstanceId;

    @ApiModelProperty(value = "任务主键")
    private String taskId;

    /**
     * 申请员工登录账号
     */
    private String loginName;

    /**
     * 申请员工登录账号
     */
    private String nodeHandler;

    /**
     * 申请员工登录名称
     */
    private String nodeHandlerName;
    /**
     * 任务类型
     */
    @ApiModelProperty(value = "任务类型")
    private String activityType;

    /**
     * 操作类型
     */
    @ApiModelProperty(value = "操作类型")
    private String operationType;

    /**
     * 操作参数
     */
    @ApiModelProperty(value = "操作参数")
    private String paramApp;
}
