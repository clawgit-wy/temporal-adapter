package com.yutong.modules.common.entity.bpm.passprocess;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class PassProcessResponse {

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
}
