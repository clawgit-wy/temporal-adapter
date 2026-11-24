package com.yutong.modules.common.entity.bpm.flowinfo;

import lombok.Data;
import org.jeecg.common.util.JsonUtils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class FlowInfoResponse {

    @XmlElement(name="return")
    private String returnMsg;


    /**
     * 获取流程信息
     *
     * @return
     */
    public FlowApprover getProcessApprover(){
        return JsonUtils.parseObject(returnMsg, FlowApprover.class);
    }


    /**
     * 获取流程信息
     *
     * @return
     */
    public List<FlowInfoApprover> getFlowInfoApprover(){
        FlowApprover flowApprover=getProcessApprover();
        if(flowApprover!=null) {
            return JsonUtils.parseArray(flowApprover.getData(), FlowInfoApprover.class);
        }
        return null;
    }
}
