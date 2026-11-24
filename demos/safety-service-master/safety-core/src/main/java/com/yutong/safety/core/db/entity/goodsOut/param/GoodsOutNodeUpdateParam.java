package com.yutong.safety.core.db.entity.goodsOut.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 物资出门节点更新参数
 */
@Data
public class GoodsOutNodeUpdateParam implements Serializable {

    private String id;
    private String flowId;
    private String curDealHandler;
    private String curHandlerName;
    private String curNodeId;
    private String curNodeName;
    private String fdStatus;
    private String nodeFlag;
    @ApiModelProperty("放行状态：0-待放行,1-已放行,2-放行异常,3-废弃")
    private String releaseStatus;

    public GoodsOutNodeUpdateParam(){}

    public GoodsOutNodeUpdateParam(String id,String flowId,String curDealHandler,String curNodeId,String curNodeName){
        this.id = id;
        this.flowId = flowId;
        this.curDealHandler = curDealHandler;
        this.curNodeId = curNodeId;
        this.curNodeName = curNodeName;
    }
}
