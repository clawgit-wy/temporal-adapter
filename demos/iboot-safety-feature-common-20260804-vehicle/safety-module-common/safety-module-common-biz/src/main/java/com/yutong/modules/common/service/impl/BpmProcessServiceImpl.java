package com.yutong.modules.common.service.impl;

import com.yutong.modules.common.api.IBpmProcessService;
import com.yutong.modules.common.entity.bpm.ApproveEvent;
import com.yutong.modules.common.entity.bpm.ProcessApprovers;
import com.yutong.modules.common.entity.bpm.SoapBody;
import com.yutong.modules.common.entity.bpm.SoapEnvelope;
import com.yutong.modules.common.entity.bpm.Validate.ValidateProcess;
import com.yutong.modules.common.entity.bpm.Validate.ValidateProcessEnvelope;
import com.yutong.modules.common.entity.bpm.Validate.ValidateProcessResponse;
import com.yutong.modules.common.entity.bpm.approve.*;
import com.yutong.modules.common.entity.bpm.approver.GetApproverList;
import com.yutong.modules.common.entity.bpm.approver.GetApproverListEnvelope;
import com.yutong.modules.common.entity.bpm.approver.GetApproverListResponse;
import com.yutong.modules.common.entity.bpm.auditoption.GetAuditOptionList;
import com.yutong.modules.common.entity.bpm.auditoption.GetAuditOptionListEnvelope;
import com.yutong.modules.common.entity.bpm.auditoption.GetAuditOptionListResponse;
import com.yutong.modules.common.entity.bpm.create.CreateProcess;
import com.yutong.modules.common.entity.bpm.create.CreateProcessResponse;
import com.yutong.modules.common.entity.bpm.create.CreateResponseEnvelope;
import com.yutong.modules.common.entity.bpm.createapprove.CreateAndApproveProcess;
import com.yutong.modules.common.entity.bpm.createapprove.CreateAndApproveProcessEnvelope;
import com.yutong.modules.common.entity.bpm.createapprove.CreateAndApproveProcessResponse;
import com.yutong.modules.common.entity.bpm.fields.GetFieldAuthInfo;
import com.yutong.modules.common.entity.bpm.fields.GetFieldAuthInfoEnvelope;
import com.yutong.modules.common.entity.bpm.fields.GetFieldAuthInfoResponse;
import com.yutong.modules.common.entity.bpm.flowauth.GetSearchFlowAuth;
import com.yutong.modules.common.entity.bpm.flowauth.GetSearchFlowAuthEnvelope;
import com.yutong.modules.common.entity.bpm.flowauth.GetSearchFlowAuthResponse;
import com.yutong.modules.common.entity.bpm.flowinfo.FlowInfoEnvelope;
import com.yutong.modules.common.entity.bpm.flowinfo.FlowInfoResponse;
import com.yutong.modules.common.entity.bpm.flowlog.GetFlowLogList;
import com.yutong.modules.common.entity.bpm.flowlog.GetFlowLogListEnvelope;
import com.yutong.modules.common.entity.bpm.flowlog.GetFlowLogListResponse;
import com.yutong.modules.common.entity.bpm.nodes.GetCurrentNodesInfo;
import com.yutong.modules.common.entity.bpm.nodes.GetCurrentNodesInfoEnvelope;
import com.yutong.modules.common.entity.bpm.nodes.GetCurrentNodesInfoResponse;
import com.yutong.modules.common.entity.bpm.operation.GetOperationList;
import com.yutong.modules.common.entity.bpm.operation.GetOperationListEnvelope;
import com.yutong.modules.common.entity.bpm.operation.GetOperationListResponse;
import com.yutong.modules.common.entity.bpm.passprocess.PassProcess;
import com.yutong.modules.common.entity.bpm.passprocess.PassProcessEnvelope;
import com.yutong.modules.common.entity.bpm.passprocess.PassProcessResponse;
import com.yutong.modules.common.entity.bpm.tasks.GetTasksInfo;
import com.yutong.modules.common.entity.bpm.tasks.GetTasksInfoEnvelope;
import com.yutong.modules.common.entity.bpm.tasks.GetTasksInfoResponse;
import com.yutong.modules.common.factory.ThirdApiFactory;
import com.yutong.modules.common.utils.JaxbXmlUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

@Service
public class BpmProcessServiceImpl implements IBpmProcessService {

    @Autowired
    private ThirdApiFactory thirdApiFactory;

    @Resource
    private ApplicationEventPublisher eventPublisher;

    @Override
    public CreateProcessResponse createProcess(CreateProcess createProcess) {
        String intNs = "http://interfaces.lbpmdocking.sys.kmss.landray.com/";
        QName qName = new QName(intNs, "CreateProcess");
        JAXBElement<CreateProcess> element = new JAXBElement<>(qName, CreateProcess.class, createProcess);
        // 3. 封装报文结构
        SoapBody<CreateProcess> body = new SoapBody<>();
        body.setBizElement(element);

        SoapEnvelope<CreateProcess> envelope = new SoapEnvelope<>();
        envelope.setBody(body);

        // 4. 调用带 CustomNsPrefixMapper 的工具类序列化
        String xml =  JaxbXmlUtil.beanToXml(envelope,createProcess.getClass());
        String resultXml = thirdApiFactory.executeMethod("bpm_process",xml);
        if(StringUtils.isEmpty(resultXml)) throw new RuntimeException("BPM流程访问异常");
        CreateResponseEnvelope envelopeResult = JaxbXmlUtil.xmlToBean(resultXml, CreateResponseEnvelope.class);
        if(envelopeResult==null || envelopeResult.getBody()==null||envelopeResult.getBody().getCreateProcessResponse()==null) throw new RuntimeException("创建流程失败");

        return envelopeResult.getBody().getCreateProcessResponse();
    }

    @Override
    public GetCurrentNodesInfoResponse getCurrentNodesInfo(GetCurrentNodesInfo nodeInfo) {
        String intNs = "http://interfaces.lbpmdocking.sys.kmss.landray.com/";
        QName qName = new QName(intNs, "GetCurrentNodesInfo");
        JAXBElement<GetCurrentNodesInfo> element = new JAXBElement<>(qName, GetCurrentNodesInfo.class, nodeInfo);
        // 3. 封装报文结构
        SoapBody<GetCurrentNodesInfo> body = new SoapBody<>();
        body.setBizElement(element);

        SoapEnvelope<GetCurrentNodesInfo> envelope = new SoapEnvelope<>();
        envelope.setBody(body);

        // 4. 调用带 CustomNsPrefixMapper 的工具类序列化
        String xml =  JaxbXmlUtil.beanToXml(envelope,nodeInfo.getClass());
        String respXml = thirdApiFactory.executeMethod("bpm_process",xml);
        if(StringUtils.isEmpty(respXml)) throw new RuntimeException("BPM流程访问异常");
        GetCurrentNodesInfoEnvelope envelopeResult = JaxbXmlUtil.xmlToBean(respXml, GetCurrentNodesInfoEnvelope.class);
        if(envelopeResult==null||envelopeResult.getBody()==null||envelopeResult.getBody().getCurrentNodesInfoResponse()==null) throw new RuntimeException("当前节点获取失败");
        return envelopeResult.getBody().getCurrentNodesInfoResponse();
    }

    @Override
    public ApproveProcessResponse approveProcess(ApproveProcess approveProcess) {
        String intNs = "http://interfaces.lbpmdocking.sys.kmss.landray.com/";
        QName qName = new QName(intNs, "ApproveProcess");
        JAXBElement<ApproveProcess> element = new JAXBElement<>(qName, ApproveProcess.class, approveProcess);
        // 3. 封装报文结构
        SoapBody<ApproveProcess> body = new SoapBody<>();
        body.setBizElement(element);

        SoapEnvelope<ApproveProcess> envelope = new SoapEnvelope<>();
        envelope.setBody(body);

        // 4. 调用带 CustomNsPrefixMapper 的工具类序列化
        String xml =  JaxbXmlUtil.beanToXml(envelope,approveProcess.getClass());
        String respXml =thirdApiFactory.executeMethod("bpm_process",xml);
        if(StringUtils.isEmpty(respXml)) throw new RuntimeException("BPM流程访问异常");
        ApproveProcessEnvelope envelopeResult = JaxbXmlUtil.xmlToBean(respXml, ApproveProcessEnvelope.class);
        if(envelopeResult==null||envelopeResult.getBody()==null||envelopeResult.getBody().getApproveProcessResponse()==null) throw new RuntimeException("审批操作失败");
        ApproveEvent event = new ApproveEvent(approveProcess.getFormInstanceId(),approveProcess.getFormId(),approveProcess.getProcessId(),approveProcess.getCurNodeId(),false);
        eventPublisher.publishEvent(event);
        return envelopeResult.getBody().getApproveProcessResponse();
    }

    @Override
    public FlowInfoResponse selfFlowInfoService(FlowInfo nodeInfo)  {
        String intNs = "http://ws.self.kmss.landray.com/";
        QName qName = new QName(intNs, "getReviewInfosNew");
        JAXBElement<FlowInfo> element = new JAXBElement<>(qName, FlowInfo.class, nodeInfo);
        // 3. 封装报文结构
        SoapBody<FlowInfo> body = new SoapBody<>();
        body.setBizElement(element);

        SoapEnvelope<FlowInfo> envelope = new SoapEnvelope<>();
        envelope.setBody(body);

        // 4. 调用带 CustomNsPrefixMapper 的工具类序列化
        String xml =  JaxbXmlUtil.beanToXml(envelope,nodeInfo.getClass());
        String respXml =thirdApiFactory.executeMethod("bpm_process_flow",xml);
        if(StringUtils.isEmpty(respXml)) throw new RuntimeException("BPM流程访问异常");
        FlowInfoEnvelope envelopeResult = JaxbXmlUtil.xmlToBean(respXml, FlowInfoEnvelope.class);
        if(envelopeResult==null||envelopeResult.getBody()==null||envelopeResult.getBody().getFlowInfoResponse()==null) throw new RuntimeException("当前节点获取失败");
        return envelopeResult.getBody().getFlowInfoResponse();
    }

    @Override
    public ExistProcessResponse IsExistProcess(ExistProcess existProcess)  {
        String intNs = "http://interfaces.lbpmdocking.sys.kmss.landray.com/";
        QName qName = new QName(intNs, "IsExistProcess");
        JAXBElement<ExistProcess> element = new JAXBElement<>(qName, ExistProcess.class, existProcess);
        // 3. 封装报文结构
        SoapBody<ExistProcess> body = new SoapBody<>();
        body.setBizElement(element);

        SoapEnvelope<ExistProcess> envelope = new SoapEnvelope<>();
        envelope.setBody(body);

        // 4. 调用带 CustomNsPrefixMapper 的工具类序列化
        String xml =  JaxbXmlUtil.beanToXml(envelope,existProcess.getClass());
        String respXml =thirdApiFactory.executeMethod("bpm_process",xml);
        if(StringUtils.isEmpty(respXml)) throw new RuntimeException("BPM流程访问异常");
        ExistProcessEnvelope envelopeResult = JaxbXmlUtil.xmlToBean(respXml, ExistProcessEnvelope.class);
        return envelopeResult.getBody().getExistProcessResponse();
    }

    @Override
    public CreateAndApproveProcessResponse createAndApproveProcess(CreateAndApproveProcess createAndApproveProcess) {
        String intNs = "http://extend.interfaces.lbpmdocking.sys.kmss.landray.com/";
        QName qName = new QName(intNs, "CreateAndApproveProcess");
        JAXBElement<CreateAndApproveProcess> element = new JAXBElement<>(qName, CreateAndApproveProcess.class, createAndApproveProcess);
        // 3. 封装报文结构
        SoapBody<CreateAndApproveProcess> body = new SoapBody<>();
        body.setBizElement(element);

        SoapEnvelope<CreateAndApproveProcess> envelope = new SoapEnvelope<>();
        envelope.setBody(body);

        // 4. 调用带 CustomNsPrefixMapper 的工具类序列化
        String xml =  JaxbXmlUtil.beanToXml(envelope,createAndApproveProcess.getClass());
        String respXml = thirdApiFactory.executeMethod("bpm_process_ext",xml);
        if(StringUtils.isEmpty(respXml)) throw new RuntimeException("BPM流程访问异常");
        CreateAndApproveProcessEnvelope envelopeResult = JaxbXmlUtil.xmlToBean(respXml, CreateAndApproveProcessEnvelope.class);
        if(envelopeResult==null||envelopeResult.getBody()==null||envelopeResult.getBody().getCreateAndApproveProcessResponse()==null) throw new RuntimeException("流程创建并且审批操作失败");
        if(envelopeResult.getBody().getCreateAndApproveProcessResponse().isSuccess()) {
            ApproveEvent event = new ApproveEvent(createAndApproveProcess.getFormInstanceId(), createAndApproveProcess.getFormId(), envelopeResult.getBody().getCreateAndApproveProcessResponse().getFlowId(),null,true);
            eventPublisher.publishEvent(event);
        }
        return envelopeResult.getBody().getCreateAndApproveProcessResponse();
    }

    @Override
    public GetFieldAuthInfoResponse getAuthFields(GetFieldAuthInfo getFieldAuthInfo) {
        String intNs = "http://interfaces.lbpmdocking.sys.kmss.landray.com/";
        QName qName = new QName(intNs, "GetFieldAuthInfo");
        JAXBElement<GetFieldAuthInfo> element = new JAXBElement<>(qName, GetFieldAuthInfo.class, getFieldAuthInfo);
        // 3. 封装报文结构
        SoapBody<GetFieldAuthInfo> body = new SoapBody<>();
        body.setBizElement(element);

        SoapEnvelope<GetFieldAuthInfo> envelope = new SoapEnvelope<>();
        envelope.setBody(body);

        // 4. 调用带 CustomNsPrefixMapper 的工具类序列化
        String xml =  JaxbXmlUtil.beanToXml(envelope,getFieldAuthInfo.getClass());
        String respXml = thirdApiFactory.executeMethod("bpm_process",xml);
        if(StringUtils.isEmpty(respXml)) throw new RuntimeException("BPM流程访问异常");
        GetFieldAuthInfoEnvelope envelopeResult = JaxbXmlUtil.xmlToBean(respXml, GetFieldAuthInfoEnvelope.class);
        if(envelopeResult==null||envelopeResult.getBody()==null||envelopeResult.getBody().getFieldAuthInfoResponse()==null) throw new RuntimeException("流程创建并且审批操作失败");
        return envelopeResult.getBody().getFieldAuthInfoResponse();
    }

    @Override
    public GetApproverListResponse getApproverList(GetApproverList approverList) {
        String intNs = "http://interfaces.lbpmdocking.sys.kmss.landray.com/";
        QName qName = new QName(intNs, "GetApproverList");
        JAXBElement<GetApproverList> element = new JAXBElement<>(qName, GetApproverList.class, approverList);
        // 3. 封装报文结构
        SoapBody<GetApproverList> body = new SoapBody<>();
        body.setBizElement(element);

        SoapEnvelope<GetApproverList> envelope = new SoapEnvelope<>();
        envelope.setBody(body);

        // 4. 调用带 CustomNsPrefixMapper 的工具类序列化
        String xml =  JaxbXmlUtil.beanToXml(envelope,approverList.getClass());
        String respXml = thirdApiFactory.executeMethod("bpm_process",xml);
        if(StringUtils.isEmpty(respXml)) throw new RuntimeException("BPM流程访问异常");
        GetApproverListEnvelope envelopeResult = JaxbXmlUtil.xmlToBean(respXml, GetApproverListEnvelope.class);
        if(envelopeResult==null||envelopeResult.getBody()==null||envelopeResult.getBody().getAproverListResponse()==null) throw new RuntimeException("获取当前节点审批人操作失败");
        return envelopeResult.getBody().getAproverListResponse();
    }

    @Override
    public GetAuditOptionListResponse getAuditOptionList(GetAuditOptionList auditOptionList) {
        String intNs = "http://interfaces.lbpmdocking.sys.kmss.landray.com/";
        QName qName = new QName(intNs, "GetAuditOptionList");
        JAXBElement<GetAuditOptionList> element = new JAXBElement<>(qName, GetAuditOptionList.class, auditOptionList);
        // 3. 封装报文结构
        SoapBody<GetAuditOptionList> body = new SoapBody<>();
        body.setBizElement(element);

        SoapEnvelope<GetAuditOptionList> envelope = new SoapEnvelope<>();
        envelope.setBody(body);

        // 4. 调用带 CustomNsPrefixMapper 的工具类序列化
        String xml =  JaxbXmlUtil.beanToXml(envelope,auditOptionList.getClass());
        String respXml = thirdApiFactory.executeMethod("bpm_process",xml);
        if(StringUtils.isEmpty(respXml)) throw new RuntimeException("BPM流程访问异常");
        GetAuditOptionListEnvelope envelopeResult = JaxbXmlUtil.xmlToBean(respXml, GetAuditOptionListEnvelope.class);
        if(envelopeResult==null||envelopeResult.getBody()==null||envelopeResult.getBody().getAuditOptionListResponse()==null) throw new RuntimeException("获取流程审批记录集合操作失败");
        return envelopeResult.getBody().getAuditOptionListResponse();
    }

    @Override
    public GetOperationListResponse getOperationList(GetOperationList operationList) {
        String intNs = "http://interfaces.lbpmdocking.sys.kmss.landray.com/";
        QName qName = new QName(intNs, "GetOperationList");
        JAXBElement<GetOperationList> element = new JAXBElement<>(qName, GetOperationList.class, operationList);
        // 3. 封装报文结构
        SoapBody<GetOperationList> body = new SoapBody<>();
        body.setBizElement(element);

        SoapEnvelope<GetOperationList> envelope = new SoapEnvelope<>();
        envelope.setBody(body);

        // 4. 调用带 CustomNsPrefixMapper 的工具类序列化
        String xml =  JaxbXmlUtil.beanToXml(envelope,operationList.getClass());
        String respXml = thirdApiFactory.executeMethod("bpm_process",xml);
        if(StringUtils.isEmpty(respXml)) throw new RuntimeException("BPM流程访问异常");
        GetOperationListEnvelope envelopeResult = JaxbXmlUtil.xmlToBean(respXml, GetOperationListEnvelope.class);
        if(envelopeResult==null||envelopeResult.getBody()==null||envelopeResult.getBody().getOperationListResponse()==null) throw new RuntimeException("获取流程操作清单操作失败");
        return envelopeResult.getBody().getOperationListResponse();
    }

    @Override
    public GetTasksInfoResponse getTasksInfo(GetTasksInfo tasksInfo) {
        String intNs = "http://interfaces.lbpmdocking.sys.kmss.landray.com/";
        QName qName = new QName(intNs, "GetTasksInfo");
        JAXBElement<GetTasksInfo> element = new JAXBElement<>(qName, GetTasksInfo.class, tasksInfo);
        // 3. 封装报文结构
        SoapBody<GetTasksInfo> body = new SoapBody<>();
        body.setBizElement(element);

        SoapEnvelope<GetTasksInfo> envelope = new SoapEnvelope<>();
        envelope.setBody(body);

        // 4. 调用带 CustomNsPrefixMapper 的工具类序列化
        String xml =  JaxbXmlUtil.beanToXml(envelope,tasksInfo.getClass());
        String respXml = thirdApiFactory.executeMethod("bpm_process",xml);
        if(StringUtils.isEmpty(respXml)) throw new RuntimeException("BPM流程访问异常");
        GetTasksInfoEnvelope envelopeResult = JaxbXmlUtil.xmlToBean(respXml, GetTasksInfoEnvelope.class);
        if(envelopeResult==null||envelopeResult.getBody()==null||envelopeResult.getBody().getTasksInfoResponse()==null) throw new RuntimeException("获取当前用户可处理任务操作失败");

        return envelopeResult.getBody().getTasksInfoResponse();
    }

    @Override
    public ValidateProcessResponse validateProcess(ValidateProcess validateProcess) {
        String intNs = "http://interfaces.lbpmdocking.sys.kmss.landray.com/";
        QName qName = new QName(intNs, "ValidateProcess");
        JAXBElement<ValidateProcess> element = new JAXBElement<>(qName, ValidateProcess.class, validateProcess);
        // 3. 封装报文结构
        SoapBody<ValidateProcess> body = new SoapBody<>();
        body.setBizElement(element);

        SoapEnvelope<ValidateProcess> envelope = new SoapEnvelope<>();
        envelope.setBody(body);

        // 4. 调用带 CustomNsPrefixMapper 的工具类序列化
        String xml =  JaxbXmlUtil.beanToXml(envelope,validateProcess.getClass());
        String respXml =thirdApiFactory.executeMethod("bpm_process",xml);
        if(StringUtils.isEmpty(respXml)) throw new RuntimeException("BPM流程访问异常");
        ValidateProcessEnvelope envelopeResult = JaxbXmlUtil.xmlToBean(respXml, ValidateProcessEnvelope.class);
        if(envelopeResult==null||envelopeResult.getBody()==null||envelopeResult.getBody().getValidateProcessResponse()==null) throw new RuntimeException("获取流程合法性校验失败操作失败");

        return envelopeResult.getBody().getValidateProcessResponse();
    }

    @Override
    public GetFlowLogListResponse getFlowLogInfo(GetFlowLogList flowLogList) {
        String intNs = "http://interfaces.lbpmdocking.sys.kmss.landray.com/";
        QName qName = new QName(intNs, "GetFlowLogList");
        JAXBElement<GetFlowLogList> element = new JAXBElement<>(qName, GetFlowLogList.class, flowLogList);
        // 3. 封装报文结构
        SoapBody<GetFlowLogList> body = new SoapBody<>();
        body.setBizElement(element);

        SoapEnvelope<GetFlowLogList> envelope = new SoapEnvelope<>();
        envelope.setBody(body);

        // 4. 调用带 CustomNsPrefixMapper 的工具类序列化
        String xml =  JaxbXmlUtil.beanToXml(envelope,flowLogList.getClass());
        String respXml =thirdApiFactory.executeMethod("bpm_process",xml);
        if(StringUtils.isEmpty(respXml)) throw new RuntimeException("BPM流程访问异常");
        GetFlowLogListEnvelope envelopeResult = JaxbXmlUtil.xmlToBean(respXml, GetFlowLogListEnvelope.class);
        if(envelopeResult==null||envelopeResult.getBody()==null||envelopeResult.getBody().getFlowLogListResponse()==null) return null;
        return envelopeResult.getBody().getFlowLogListResponse();
    }

    @Override
    public ProcessApprovers getPorcessApprovers(Long formInstanceId, String formId, String processId) {
        ProcessApprovers processApprovers = new ProcessApprovers();
        GetApproverList approverList = new GetApproverList();
        approverList.setFormId(formId);
        approverList.setProcessId(processId);
        GetApproverListResponse approvers =  getApproverList(approverList);
        processApprovers.setCurApprover(approvers.getProcessApprover());
        GetFlowLogList flowLogList = new GetFlowLogList();
        flowLogList.setFormId(formId);
        flowLogList.setProcessId(processId);
        GetFlowLogListResponse flowLog =  getFlowLogInfo(flowLogList);
        processApprovers.setApproverRecord(flowLog.getApproverLogPage());
        return processApprovers;
    }

    @Override
    public PassProcessResponse setProcessCirculate(PassProcess passProcess) {
        String intNs = "http://extend.interfaces.lbpmdocking.sys.kmss.landray.com/";
        QName qName = new QName(intNs, "passProcess");
        JAXBElement<PassProcess> element = new JAXBElement<>(qName, PassProcess.class, passProcess);
        // 3. 封装报文结构
        SoapBody<PassProcess> body = new SoapBody<>();
        body.setBizElement(element);

        SoapEnvelope<PassProcess> envelope = new SoapEnvelope<>();
        envelope.setBody(body);

        // 4. 调用带 CustomNsPrefixMapper 的工具类序列化
        String xml =  JaxbXmlUtil.beanToXml(envelope,passProcess.getClass());
        String respXml =thirdApiFactory.executeMethod("bpm_process_circulate",xml);
        if(StringUtils.isEmpty(respXml)) throw new RuntimeException("BPM流程访问异常");
        PassProcessEnvelope envelopeResult = JaxbXmlUtil.xmlToBean(respXml, PassProcessEnvelope.class);
        if(envelopeResult==null||envelopeResult.getBody()==null||envelopeResult.getBody().getPassProcessResponse()==null) return null;

        return envelopeResult.getBody().getPassProcessResponse();
    }

    @Override
    public CanApprovalProcessResponse queryCanApproveProcess(CanApprovalProcess canApprovalProcess)  {
        String intNs = "http://interfaces.lbpmdocking.sys.kmss.landray.com/";
        QName qName = new QName(intNs, "CanApprovalProcess");
        JAXBElement<CanApprovalProcess> element = new JAXBElement<>(qName, CanApprovalProcess.class, canApprovalProcess);
        // 3. 封装报文结构
        SoapBody<CanApprovalProcess> body = new SoapBody<>();
        body.setBizElement(element);

        SoapEnvelope<CanApprovalProcess> envelope = new SoapEnvelope<>();
        envelope.setBody(body);

        // 4. 调用带 CustomNsPrefixMapper 的工具类序列化
        String xml =  JaxbXmlUtil.beanToXml(envelope,canApprovalProcess.getClass());
        String respXml =thirdApiFactory.executeMethod("bpm_process",xml);
        if(StringUtils.isEmpty(respXml)) throw new RuntimeException("BPM流程访问异常");
        CanApprovalProcessEnvelope envelopeResult = JaxbXmlUtil.xmlToBean(respXml, CanApprovalProcessEnvelope.class);
        if(envelopeResult==null||envelopeResult.getBody()==null||envelopeResult.getBody().getApproveProcessResponse()==null) return null;

        return envelopeResult.getBody().getApproveProcessResponse();
    }

    @Override
    public GetSearchFlowAuthResponse querySearchFlowAuth(GetSearchFlowAuth searchFlowAuth) {
        String intNs = "http://ws.self.kmss.landray.com/";
        QName qName = new QName(intNs, "getSearchFlowAuth");
        JAXBElement<GetSearchFlowAuth> element = new JAXBElement<>(qName, GetSearchFlowAuth.class, searchFlowAuth);
        // 3. 封装报文结构
        SoapBody<GetSearchFlowAuth> body = new SoapBody<>();
        body.setBizElement(element);

        SoapEnvelope<GetSearchFlowAuth> envelope = new SoapEnvelope<>();
        envelope.setBody(body);

        // 4. 调用带 CustomNsPrefixMapper 的工具类序列化
        String xml = JaxbXmlUtil.beanToXml(envelope,searchFlowAuth.getClass());
        String respXml =thirdApiFactory.executeMethod("bpm_process_permit",xml);
        if(StringUtils.isEmpty(respXml)) throw new RuntimeException("BPM流程访问异常");
        GetSearchFlowAuthEnvelope envelopeResult = JaxbXmlUtil.xmlToBean(respXml, GetSearchFlowAuthEnvelope.class);
        if(envelopeResult==null||envelopeResult.getBody()==null||envelopeResult.getBody().getSearchFlowAuthResponse()==null) return null;

        return envelopeResult.getBody().getSearchFlowAuthResponse();
    }


}
