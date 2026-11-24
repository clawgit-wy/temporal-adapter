package com.yutong.safety.core.handler;

import com.yutong.safety.common.param.web.CarOutRuleAddParam;
import com.yutong.safety.common.param.web.CarOutRuleListParam;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.common.vo.SafetyCarOutRuleVO;
import com.yutong.safety.core.service.SafetyCarOutRuleHistoryService;
import com.yutong.safety.core.service.SafetyCarOutRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author gwxiexj
 * @since 2024-04-02 09:37:00
 */
@Component
public class SafetyCarOutRuleHandler {

    @Autowired
    private SafetyCarOutRuleService safetyCarOutRuleService;

    @Autowired
    private SafetyCarOutRuleHistoryService safetyCarOutRuleHistoryService;

    /**
     * 分页查询
     * @param param 查询入参
     * @return 返回
     */
    public Object pageList(CarOutRuleListParam param) {
        List<SafetyCarOutRuleVO> list = safetyCarOutRuleService.pageList(param);
        long total = safetyCarOutRuleService.pageListCount(param);
        return new PageVO<SafetyCarOutRuleVO>(total, list);
    }

    /**
     * 数据添加接口
     * @param param
     * @return
     */
    public Object add(CarOutRuleAddParam param) throws Exception{
        /*更新前保留历史版本
        if(param.getRuleId() != null){
            SafetyCarOutRuleDO ruleDO = safetyCarOutRuleService.getRuleByKey(param.getRuleId());
            if(ruleDO != null){
                safetyCarOutRuleHistoryService.insertCarOutRuleHistory(ruleDO);
            }
        }*/
        safetyCarOutRuleService.saveOrUpdate(param);
        return null;
    }

    public Object deleteById(String ruleId) throws Exception{
        return safetyCarOutRuleService.deleteById(ruleId);
    }

    public Object doTask() {
        return safetyCarOutRuleService.doTask();
    }

    public Object downAll() {
        return safetyCarOutRuleService.downAll();
    }
}
