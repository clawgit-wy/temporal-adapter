package com.yutong.modules.common.service.impl;

import com.yutong.modules.common.api.eip.IEipDoneService;
import com.yutong.modules.common.entity.bpm.SoapBody;
import com.yutong.modules.common.entity.bpm.SoapEnvelope;
import com.yutong.modules.common.entity.bpm.approve.ApproveProcessEnvelope;
import com.yutong.modules.common.entity.eip.done.EipSetTodoDone;
import com.yutong.modules.common.entity.eip.done.EipSetTodoDoneArg;
import com.yutong.modules.common.entity.eip.done.SendTodoEnvelope;
import com.yutong.modules.common.entity.eip.done.SendTodoReturn;
import com.yutong.modules.common.entity.eip.todo.EipSendTodo;
import com.yutong.modules.common.entity.eip.todo.EipSendTodoArg;
import com.yutong.modules.common.entity.eip.todo.SetTodoDoneEnvelope;
import com.yutong.modules.common.entity.eip.todo.SetTodoDoneReturn;
import com.yutong.modules.common.factory.ThirdApiFactory;
import com.yutong.modules.common.utils.EipXmlUtil;
import com.yutong.modules.common.utils.JaxbXmlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

@Service
public class EipDoneServiceImpl implements IEipDoneService {

    @Autowired
    private ThirdApiFactory thirdApiFactory;

    @Override
    public SendTodoReturn createEipTodo(EipSendTodoArg arg) {
        EipSendTodo sendToDo = new EipSendTodo();
        sendToDo.setArg0(arg);
        String webNs = "http://webservice.notify.sys.kmss.landray.com/";
        QName qName = new QName(webNs, "sendTodo");
        JAXBElement<EipSendTodo> element = new JAXBElement<>(qName, EipSendTodo.class, sendToDo);

        SoapBody<EipSendTodo> body = new SoapBody<>();
        body.setBizElement(element);

        SoapEnvelope<EipSendTodo> envelope = new SoapEnvelope<>();
        envelope.setBody(body);

        String xml = EipXmlUtil.beanToXml(envelope, sendToDo.getClass(), arg.getClass());

        String respXml = thirdApiFactory.executeMethod("eip_done",xml);

        SendTodoEnvelope envelopeResult = JaxbXmlUtil.xmlToBean(respXml, SendTodoEnvelope.class);
        if(envelopeResult==null||envelopeResult.getBody()==null||envelopeResult.getBody().getSendTodoResponse()==null||envelopeResult.getBody().getSendTodoResponse().getSendTodoReturn()==null) throw new RuntimeException("发送待办失败");
        return envelopeResult.getBody().getSendTodoResponse().getSendTodoReturn();
    }

    @Override
    public SetTodoDoneReturn eipToDoDone(EipSetTodoDoneArg arg) {
        EipSetTodoDone sendToDo = new EipSetTodoDone();
        sendToDo.setArg0(arg);;
        String webNs = "http://webservice.notify.sys.kmss.landray.com/";
        QName qName = new QName(webNs, "setTodoDone");
        JAXBElement<EipSetTodoDone> element = new JAXBElement<>(qName, EipSetTodoDone.class, sendToDo);

        SoapBody<EipSetTodoDone> body = new SoapBody<>();
        body.setBizElement(element);

        SoapEnvelope<EipSetTodoDone> envelope = new SoapEnvelope<>();
        envelope.setBody(body);

        String xml = EipXmlUtil.beanToXml(envelope, sendToDo.getClass(), arg.getClass());
        String respXml = thirdApiFactory.executeMethod("eip_done",xml);
        SetTodoDoneEnvelope envelopeResult = JaxbXmlUtil.xmlToBean(respXml, SetTodoDoneEnvelope.class);
        if(envelopeResult==null||envelopeResult.getBody()==null||envelopeResult.getBody().getTodoDoneResponse()==null||envelopeResult.getBody().getTodoDoneResponse().getTodoDoneReturn()==null) throw new RuntimeException("设置待办失败");

        return envelopeResult.getBody().getTodoDoneResponse().getTodoDoneReturn();
    }
}
