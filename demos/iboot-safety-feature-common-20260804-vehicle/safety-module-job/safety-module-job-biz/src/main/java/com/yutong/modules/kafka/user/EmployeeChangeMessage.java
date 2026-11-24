package com.yutong.modules.kafka.user;

import com.yutong.modules.entity.SafetyEmployeeMessage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 人员变更 kafka 消息体：包含变更类型和人员台账信息
 */
@Data
@ApiModel(value = "人员变更消息", description = "人员离职/岗位变更 kafka 消息体")
public class EmployeeChangeMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 变更类型：离职
     */
    public static final String CHANGE_TYPE_DISABLED = "DISABLED";

    /**
     * 变更类型：岗位变更
     */
    public static final String CHANGE_TYPE_POSITION_CHANGE = "POSITION_CHANGE";

    @ApiModelProperty(value = "变更类型，DISABLED-离职，POSITION_CHANGE-岗位变更")
    private String changeType;

    @ApiModelProperty(value = "人员台账信息")
    private SafetyEmployeeMessage employeeMessage;
}
