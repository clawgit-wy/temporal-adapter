package com.yutong.safety.core.service.enter;

import com.yutong.safety.common.utils.JsonUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.WebserviceUtil;
import com.yutong.safety.core.db.entity.enter.EnterFactoryRecord;
import com.yutong.safety.core.handler.EnvironSyncRegisterHandler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 安保新平台登记人员同步安保一件事
 * @author gwxiexj
 * @since 2024-08-21 15:10
 */
@Service
public class EnvironSyncRegisterService implements EnvironSyncRegisterHandler {

    @Value("${environ.saveRegister.url}")
    private String environSaveUrl;

    @Autowired
    private WebserviceUtil webserviceUtil;

    /**
     * 人员登记信息同步安保一件事（environ）
     * @param factoryRecords 入厂登记信息
     * @param sessionKey 登记key
     */
    @Override
    public void syncEnvironRegister(List<EnterFactoryRecord> factoryRecords, String sessionKey){
        if(StringUtils.isBlank(environSaveUrl)){
            LogUtils.error("告警通知-同步environ人员登记信息异常，”environ.saveRegister.url“未配置！");
            return;
        }
        if(CollectionUtils.isEmpty(factoryRecords) || StringUtils.isBlank(sessionKey)){
            LogUtils.warn("同步environ人员登记信息终止，sessionKey或入参为空");
            return;
        }
        //设置数据类型0
        factoryRecords.stream().forEach(e->e.setDataType("0"));
        String bodyParams = JsonUtils.toString(factoryRecords);
        Map headerParams = new HashMap<>();
        headerParams.put("sessionKey",sessionKey);
        headerParams.put("Content-Type", "application/json");
        String returnStr = webserviceUtil.sendHttp(environSaveUrl,"2",bodyParams,headerParams,"POST");
        LogUtils.info("同步environ人员登记信息结束，返回值{}",returnStr);
    }
}
