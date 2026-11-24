package com.yutong.modules.bpmcallback.action;


import com.yutong.modules.common.dto.BpmMethodProcessDTO;
import com.yutong.modules.common.entity.SafetyProcessAbandonConfig;
import com.yutong.modules.common.mapper.SafetyProcessAbandonConfiglMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component("process_abandon")
public class ProcessAbandon extends NodeAction {

    @Autowired
    private SafetyProcessAbandonConfiglMapper safetyProcessAbandonConfiglMapper;

    @Override
    public void execute(BpmMethodProcessDTO event) {
        log.info("event:{}",event);
        String modelId = event.getFormId().getModelId();
        if(event!=null&&event.getFormId()!=null&&StringUtils.isNotEmpty(modelId)){
            createLocation(event);
        }
    }

    private void createLocation(BpmMethodProcessDTO event) {
        SafetyProcessAbandonConfig safetyProcessAbandonConfig= safetyProcessAbandonConfiglMapper.queryProcessAbandonByModelId(event.getFormId().getModelId());
        if(safetyProcessAbandonConfig!=null&&StringUtils.isNotEmpty( event.getFormId().getFormInstanceId())){
            safetyProcessAbandonConfiglMapper.updateParamValue(safetyProcessAbandonConfig.getParameter(),safetyProcessAbandonConfig.getStatusValue(),safetyProcessAbandonConfig.getFormId(),event.getFormId().getFormInstanceId());
        }
    }


}
