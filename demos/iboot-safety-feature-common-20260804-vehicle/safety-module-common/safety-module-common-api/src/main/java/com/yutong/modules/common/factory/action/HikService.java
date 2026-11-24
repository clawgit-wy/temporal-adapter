package com.yutong.modules.common.factory.action;

import cn.hutool.http.ContentType;
import com.hikvision.artemis.sdk.ArtemisHttpUtil;
import com.hikvision.artemis.sdk.config.ArtemisConfig;
import com.yutong.modules.common.constant.ProcessConstant;
import com.yutong.modules.common.entity.YtsafetyApiRouteConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component("hik")
public class HikService extends ActionService {

    @Value("${hik.ArtemisConfig.host}")
    private String host;

    @Value("${hik.ArtemisConfig.appKey}")
    private String appKey;

    @Value("${hik.ArtemisConfig.appSecret}")
    private String appSecret;

    @PostConstruct
    public void init() {
        ArtemisConfig artemisConfig =new ArtemisConfig();
        artemisConfig.setHost(host);
        artemisConfig.setAppKey(appKey);
        artemisConfig.setAppSecret(appSecret);
    }

    /**
     * 海康接口
     */
    @Override
    public String run(YtsafetyApiRouteConfig config, Object input) throws Exception {
        Map<String, String> path = new HashMap<String, String>(2) {
            { put(ProcessConstant.https, config.getIntefacePath()); }
        };
        String result = ArtemisHttpUtil.doPostStringArtemis(path, input.toString(), null, null, ContentType.JSON.getValue());
        if (StringUtils.isBlank(result)) {
            throw new Exception("海康接口异常！");
        }
        return result;
    }
}
