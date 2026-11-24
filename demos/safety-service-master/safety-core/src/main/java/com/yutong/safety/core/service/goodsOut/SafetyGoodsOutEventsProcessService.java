package com.yutong.safety.core.service.goodsOut;

import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.db.entity.goodsOut.SafetyGoodsOutEventProcessDO;
import com.yutong.safety.core.db.mapper.goodsOut.SafetyGoodsOutEventProcessDOExtMapper;
import com.yutong.safety.core.db.mapper.goodsOut.SafetyGoodsOutEventProcessDOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class SafetyGoodsOutEventsProcessService {
    @Autowired
    SafetyGoodsOutEventProcessDOMapper safetyGoodsOutEventProcessDOMapper;

    @Autowired
    SafetyGoodsOutEventProcessDOExtMapper safetyGoodsOutEventProcessDOExtMapper;

    public Integer save(SafetyGoodsOutEventProcessDO process) {
        return safetyGoodsOutEventProcessDOMapper.insert(process);
    }

    public void update(SafetyGoodsOutEventProcessDO process) {
        safetyGoodsOutEventProcessDOMapper.updateByPrimaryKeySelective(process);
    }

    public SafetyGoodsOutEventProcessDO selectById(String id) {
        return safetyGoodsOutEventProcessDOMapper.selectByPrimaryKey(id);
    }

    public void bpmCallBackEventProcess(String processId) {
        LogUtils.info("出门证异常上报流程回调{}", processId);
        SafetyGoodsOutEventProcessDO en = new SafetyGoodsOutEventProcessDO();
        en.setProcessId(processId);
        en.setLastUpdateTime(new Date());
        en.setProcessStatus("30");
        en.setCurrentNodeId("N3");

        safetyGoodsOutEventProcessDOExtMapper.approveByEntity(en);
    }


}
