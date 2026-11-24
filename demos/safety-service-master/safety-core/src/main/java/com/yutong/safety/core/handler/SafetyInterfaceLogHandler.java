package com.yutong.safety.core.handler;

import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.common.vo.VoUtil;
import com.yutong.safety.core.db.entity.SafetyInterfaceLogDOWithBLOBs;
import com.yutong.safety.core.service.SafetyInterfaceLogService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

@Component
public class SafetyInterfaceLogHandler {
    @Resource
    private SafetyInterfaceLogService safetyInterfaceLogService;

    @Async
    public void recordLog(String interfaceName, String url, Date requestTime, String requestBody, String response) {
        try {
            Date responseTime = new Date();
            int elapsedTime = (int) (responseTime.getTime() - requestTime.getTime());

            // 创建日志对象
            SafetyInterfaceLogDOWithBLOBs log = new SafetyInterfaceLogDOWithBLOBs();
            log.setInterfaceName(interfaceName);
            log.setInterfaceUrl(url);
            log.setRequestBody(requestBody);
            log.setResponseBody(response);
            log.setRequestTime(requestTime);
            log.setResponseTime(responseTime);
            log.setElapsedTime(elapsedTime);
            log.setCreatedTime(new Date());

            // 保存日志到数据库
            safetyInterfaceLogService.save(log);
        } catch (Exception e) {
            LogUtils.error("请求日志记录异常");
        }
    }

    public VO backupInterfaceLog() {
        Long num = safetyInterfaceLogService.selectCount();
        if (num > 1000000) {
            safetyInterfaceLogService.backupInterfaceLog();
        }
        return VoUtil.success();
    }
}
