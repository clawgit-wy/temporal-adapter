package com.yutong.safety.core.handler.goodsOut;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.vo.BpmNodeReturnVO;
import com.yutong.safety.core.db.entity.bpm.BpmProcessParam;
import com.yutong.safety.core.db.entity.goodsOut.SafetyGoodsOutEventProcessDO;
import com.yutong.safety.core.db.entity.goodsOut.SafetyGoodsOutExceptionRecordsDO;
import com.yutong.safety.core.db.entity.goodsOut.param.GoodsOutNodeUpdateParam;
import com.yutong.safety.core.dto.goodsOut.SafetyGoodsOutEventProcessDTO;
import com.yutong.safety.core.service.SafetyBpmProcessService;
import com.yutong.safety.core.service.goodsOut.SafetyGoodsOutEventsProcessService;
import com.yutong.safety.core.service.goodsOut.SafetyGoodsOutExceptionRecordService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SafetyGoodsOutEventsProcessHandler {
    @Autowired
    SafetyGoodsOutEventsProcessService safetyGoodsOutEventsProcessService;

    @Autowired
    SafetyGoodsOutExceptionRecordService safetyGoodsOutExceptionRecordService;

    @Autowired
    private SafetyBpmProcessService safetyBpmProcessService;

    private static String GOODS_OUT_EVENTS = "goods_out_events";
    private static String GOODS_OUT_EVENTS_PROCESS = "safety_goods_out_events_process";

    public void saveAndCreateProcess(SafetyGoodsOutEventProcessDTO entity,String token) {
        String uuid = UUID.randomUUID().toString().replace("-","");
        entity.setId(uuid);

        entity.setProcessStatus("20");
        entity.setCurrentNodeId("N4");

        SafetyGoodsOutEventProcessDO process = new SafetyGoodsOutEventProcessDO();
        BeanUtils.copyProperties(entity, process);
        Integer row = safetyGoodsOutEventsProcessService.save(process);

        //更新异常明细表
        SafetyGoodsOutExceptionRecordsDO recordsDO = new SafetyGoodsOutExceptionRecordsDO();
        recordsDO.setId(entity.getExceptionId());
        recordsDO.setViolationReportId(uuid);
        safetyGoodsOutExceptionRecordService.updateExceptionInfo(recordsDO);

        if(row>0){
            //更新记录表
            safetyGoodsOutExceptionRecordService.updateProcessId(entity);

            //手动提交事务
            String returnFlowId = safetyBpmProcessService.createAndApproveProcessByUserName(entity.getProcessSubject(),
                    GOODS_OUT_EVENTS, GOODS_OUT_EVENTS_PROCESS, entity.getReporterEip(),uuid);
            if(StringUtils.isNotBlank(returnFlowId)){
                process.setProcessId(returnFlowId.split(",")[1]);
            }
            safetyGoodsOutEventsProcessService.update(process);
        }

    }

    public Object approvalProcess(SafetyGoodsOutEventProcessDTO entity,String token) {
        SafetyGoodsOutEventProcessDO process = new SafetyGoodsOutEventProcessDO();
        BeanUtils.copyProperties(entity, process);
        if (!StringUtils.isEmpty(entity.getRemediationCompletionTime())) {
            process.setRemediationCompletionTime(DateUtils.parse(entity.getRemediationCompletionTime(), 2));
        }
        safetyGoodsOutEventsProcessService.update(process);

        return safetyBpmProcessService.approveProcess(entity.getId(), entity.getProcessParam().getFlowId(),
                GOODS_OUT_EVENTS, GOODS_OUT_EVENTS_PROCESS, entity.getProcessParam().getFdParameterJson(), token);
    }

    public SafetyGoodsOutEventProcessDO getProcessDetails(String processId) {
        if(StringUtils.isBlank(processId)){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"“id”不能为空");
        }
        return safetyGoodsOutEventsProcessService.selectById(processId);
    }
}
