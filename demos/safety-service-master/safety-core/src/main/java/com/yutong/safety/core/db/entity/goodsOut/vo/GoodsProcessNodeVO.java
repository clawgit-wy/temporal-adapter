package com.yutong.safety.core.db.entity.goodsOut.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class GoodsProcessNodeVO implements Serializable {

    @ApiModelProperty("主键ID")
    private String id;
    @ApiModelProperty("流程ID")
    private String flowId;
    @ApiModelProperty("当前节点ID")
    private String curNodeId;
    @ApiModelProperty("当前节点名称")
    private String curNodeName;
    @ApiModelProperty("节点处理人用户名")
    private String curNodeHandler;
    @ApiModelProperty("节点处理人姓名")
    private String curHandlerName;
}
