package com.yutong.safety.common.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 审批返回状态
 * @author gwxiexj
 * @since 2024-08-02 10:20
 */
@Data
public class BpmNodeReturnVO implements Serializable {

    @ApiModelProperty("节点审批状态")
    private String status;
    @ApiModelProperty("节点审批详细情况")
    private String message;
}
