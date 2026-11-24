package com.yutong.modules.common.event;

import com.yutong.modules.common.api.IBpmCallBackService;
import com.yutong.modules.common.api.IBpmProcessService;
import com.yutong.modules.common.api.bpm.IYtsafetySummaryProcessApproverService;
import com.yutong.modules.common.api.bpm.IYtsafetySummaryProcessNowapproverService;
import com.yutong.modules.common.api.bpm.IYtsafetySummaryProcessService;
import com.yutong.modules.common.constant.ProcessConstant;
import com.yutong.modules.common.constant.enums.ProcessStatusEnums;
import com.yutong.modules.common.dto.BpmMethodProcessDTO;
import com.yutong.modules.common.dto.BpmTemplateFormDTO;
import com.yutong.modules.common.entity.YtsafetySummaryProcess;
import com.yutong.modules.common.entity.bpm.ApproveEvent;
import com.yutong.modules.common.entity.bpm.FlowAuthUserEntity;
import com.yutong.modules.common.entity.bpm.NodeInfo;
import com.yutong.modules.common.entity.bpm.ProcessApprovers;
import com.yutong.modules.common.entity.bpm.flowauth.GetSearchFlowAuth;
import com.yutong.modules.common.entity.bpm.flowauth.GetSearchFlowAuthResponse;
import com.yutong.modules.common.entity.bpm.nodes.GetCurrentNodesInfo;
import com.yutong.modules.common.entity.bpm.nodes.GetCurrentNodesInfoResponse;
import com.yutong.modules.common.utils.JaxbXmlUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class ProcessCallBackListener {

    @Autowired
    private IBpmCallBackService bpmCallBackService;

    @Autowired
    private IBpmProcessService bpmProcessService;

    @Autowired
    private IYtsafetySummaryProcessApproverService summaryProcessApproverService;

    @Autowired
    private IYtsafetySummaryProcessNowapproverService summaryProcessNowapproverService;

    @Autowired
    private IYtsafetySummaryProcessService safetySummaryProcessService;

    @EventListener(BpmMethodProcessDTO.class)
    public void approveEvent(BpmMethodProcessDTO event) {
        bpmCallBackService.processCallBack(event);
    }
}
