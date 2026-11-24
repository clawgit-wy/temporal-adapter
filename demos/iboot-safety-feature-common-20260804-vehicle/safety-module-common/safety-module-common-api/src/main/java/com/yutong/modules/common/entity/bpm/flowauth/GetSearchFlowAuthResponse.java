package com.yutong.modules.common.entity.bpm.flowauth;

import com.yutong.modules.common.entity.bpm.*;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.util.JsonUtils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.List;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class GetSearchFlowAuthResponse {

    @XmlElement(name="return")
    private String returnMsg;

    /**
     * 是否成功
     * @return
     */
    public boolean isSuccess(){
        return !StringUtils.isEmpty(returnMsg);
    }
    /**
     * 获取异常信息
     *
     * @return
     */
    public String getErrorMsg(){
        if(StringUtils.isEmpty(returnMsg)) return "";
        if(!isSuccess()){
            ProcessResult results = (ProcessResult) JsonUtils.parseArray(returnMsg, ProcessResult.class);
            return results==null?"":results.getMsg();
        }
        return "";
    }

    /**
     * 获取流程主键
     * @return
     */
    public List<FlowAuthUserEntity> getFlowAuthUser(){
        if(isSuccess()) {
            ProcessResult results = (ProcessResult) JsonUtils.parseObject(returnMsg, ProcessResult.class);
            if(results==null||results.getCode()==null||!results.getCode().equalsIgnoreCase("s")) return null;
            return JsonUtils.parseArray(results.getData(),FlowAuthUserEntity.class);
        }
        return null;
    }
}
