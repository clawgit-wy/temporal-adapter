package com.yutong.safety.core.service;

import com.yutong.safety.core.db.entity.SafetyCarOutRuleDO;
import com.yutong.safety.core.db.entity.SafetyCarOutRuleHistoryDO;
import com.yutong.safety.core.db.mapper.SafetyCarOutRuleHistoryDOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author gwxiexj
 * @since 2024-04-03 17:14
 */
@Service
public class SafetyCarOutRuleHistoryService {

    @Autowired
    private SafetyCarOutRuleHistoryDOMapper safetyCarOutRuleHistoryDOMapper;

    /**
     * 保存历史记录
     * @param rule
     */
    public void insertCarOutRuleHistory(SafetyCarOutRuleDO rule){

        SafetyCarOutRuleHistoryDO historyDO = new SafetyCarOutRuleHistoryDO();
        historyDO.setTargetId(rule.getId());
        historyDO.setCheckFlag(rule.getCheckFlag());
        historyDO.setOrganCode(rule.getOrganCode());
        historyDO.setOrganName(rule.getOrganName());
        historyDO.setPostCategory(rule.getPostCategory());
        historyDO.setPostName(rule.getPostName());
        historyDO.setPriority(rule.getPriority());
        historyDO.setRemark(rule.getRemark());
        historyDO.setCreateTime(rule.getCreateTime());
        historyDO.setUpdateTime(new Date());

        safetyCarOutRuleHistoryDOMapper.insert(historyDO);
    }
}
