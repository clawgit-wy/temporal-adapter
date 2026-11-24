package com.yutong.modules.common.factory;

import com.yutong.modules.common.api.IApiRouteConfigService;
import com.yutong.modules.common.api.ISafetyInterfaceLogService;
import com.yutong.modules.common.api.IThirdFactoryApi;
import com.yutong.modules.common.entity.SafetyInterfaceLog;
import com.yutong.modules.common.entity.YtsafetyApiRouteConfig;
import com.yutong.modules.common.factory.action.ActionService;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.base.service.BaseCommonService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.Date;


/**
 * 三方接口工厂类
 */
@Slf4j
public class ThirdApiFactory implements IThirdFactoryApi {


    @Autowired
    private IApiRouteConfigService apiRouteConfigService;

    @Autowired
    private IThirdApiFactory apiFactory;

    @Resource
    private BaseCommonService baseCommonService;

    @Resource
    private ISafetyInterfaceLogService safetyInterfaceLogService;

    /**
     * 执行三方调用
     * @param routeCode
     * @param input
     * @return
     */
    public String executeMethod(String routeCode,Object input) {
        String result = "";
        SafetyInterfaceLog interfaceLog = new SafetyInterfaceLog();
        interfaceLog.setRequestTime(new Date());
        try {
            YtsafetyApiRouteConfig config = apiRouteConfigService.queryApiRouteConfigByCode(routeCode);
            http://sguat.yutong.com/lbpm/selfFlowInfoService?apikey=4d22e6b1cb92477d8699fad8a671389e
//          记录接口信息
            interfaceLog.setInterfaceName(config.getInterfaceName());
            String url = config.getIntefacePath()==null?"":config.getIntefacePath();
//            InterfaceUrl 字段只有 100
            if(url.length()>100){
                // 保证截断后 URL 语法基本完整
                int lastParamIndex = url.lastIndexOf("&", 90);
                if (lastParamIndex > 0) {
                    url = url.substring(0, lastParamIndex);
                } else {
                    url = url.substring(0, 100);
                }
            }
            interfaceLog.setInterfaceUrl(url);
            interfaceLog.setRequestBody(String.valueOf(input));

//            发起请求
            String actionCode = config.getInterfaceServer();
            ActionService actionService = apiFactory.getActionService(actionCode);
            result = actionService.run(config, input);
            interfaceLog.setResponseBody(result);
        } catch (Exception e) {
            interfaceLog.setResponseBody(e.getMessage());
        } finally {
            //记录接口请求信息
            interfaceLog.setResponseTime(new Date());
            saveInterfaceLog(interfaceLog);
        }
        return result;
    }

    @Override
    public String pushYxtRebot(Object input) {
        return executeMethod("yxtnotice001",input);
    }

    @Override
    public String pushUserSyncInfo(Object input) {
        return executeMethod("employee_sync",input);
    }


    /**
     * 保存日志
     */
    private void saveInterfaceLog (SafetyInterfaceLog interfaceLog){
        try{
            interfaceLog.setCreatedTime(new Date());
            int elapsedTime = (int)(interfaceLog.getResponseTime().getTime()-interfaceLog.getRequestTime().getTime());
            interfaceLog.setElapsedTime(elapsedTime);
            safetyInterfaceLogService.saveInterfaceLog(interfaceLog);
        }catch (Exception e){
            log.error(e.getMessage());
        }
    }
}
