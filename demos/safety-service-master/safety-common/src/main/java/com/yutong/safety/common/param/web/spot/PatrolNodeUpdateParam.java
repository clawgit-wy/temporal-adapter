package com.yutong.safety.common.param.web.spot;

import lombok.Data;

import java.io.Serializable;
import java.security.SecureRandom;

@Data
public class PatrolNodeUpdateParam implements Serializable {

    private Integer id;
    private String flowId;
    private String curDealHandler;

    public PatrolNodeUpdateParam(){}

    public PatrolNodeUpdateParam(Integer id,String flowId,String curDealHandler){
        this.id = id;
        this.flowId = flowId;
        this.curDealHandler = curDealHandler;
    }
}
