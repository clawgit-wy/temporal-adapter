package com.yutong.modules.common.entity.bpm.nodes;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yutong.modules.common.entity.bpm.NodeInfo;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.util.JsonUtils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class GetCurrentNodesInfoResponse {

    @XmlElement(name="return")
    private String returnMsg;

    /**
     * 是否成功
     * @return
     */
    public boolean isSuccess(){
        if(StringUtils.isEmpty(returnMsg)) return false;
        return returnMsg.toUpperCase().startsWith("T");
    }

    /**
     * 获取异常信息
     * @return
     */
    public String getErrorMsg(){
        if(StringUtils.isEmpty(returnMsg)) return "";
        if(!isSuccess()){
            return returnMsg.replace("F:","");
        }
        return "";
    }

    /**
     * 获取流程主键
     * @return
     */
    public List<NodeInfo> getCurrentNodeInfo(){
        if(isSuccess()){
            String json = returnMsg.replace("T:","").trim();
            return JsonUtils.parseArray(json, NodeInfo.class);
        }
        return null;
    }
}
