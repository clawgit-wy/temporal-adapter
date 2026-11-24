package com.yutong.safety.core.service.wechat;

import com.alibaba.fastjson.JSONObject;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.dto.wechat.WxRequestDTO;
import com.yutong.safety.core.handler.wechat.WxMobileHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * @author caomg
 * @date 2024-8-13 15:37
 * @description PushWxMesService
 */
@Component
@Slf4j
public class PushWxMesService {
    @Autowired
    WxMobileHandler wxMobileHandler;

    @Value("${template.id}")
    private String template_id;

    @Value("${goods.template.id}")
    private String goodsTemplateId;

    @Value("${page}")
    private String page;

    @Value("${pageCmz}")
    private String pageCmz;

    @Value("${miniprogram.state}")
    private String miniprogram_state;

    @Value("${entry_config.template.id}")
    private String entryConfigTemplate;
    @Value("${entry_config.page}")
    private String entryConfigPage;

    /**
     * 物流车备案信息表发微信消息
     * @param mobile 手机号
     * @return
     */
    public VO pushWxMes(String mobile,String carNo,String warn){
        WxRequestDTO en = new WxRequestDTO();
        en.setMobile(mobile);
        en.setTemplate_id(template_id);
        en.setMiniprogram_state(miniprogram_state);
        en.setPage(page);

        HashMap<String, Object> map = new HashMap<>();
        HashMap<String, Object> map1 = new HashMap<>();
        map1.put("value", carNo);
        HashMap<String, Object> map2 = new HashMap<>();
        map2.put("value", warn);
        map.put("car_number1", map1);
        map.put("thing4", map2);

        en.setData(new JSONObject(map));

        return wxMobileHandler.pushMessageToWx(en);
    }

    /**
     * 物资出门证发微信消息
     * @param mobile 申请人手机号
     * @param formInstanceId formInstanceId
     * @param nodeFlag 节点标志
     * @param applicant 申请人
     * @param cutOffTime 截止时间（yyyy-MM-dd HH:mm）
     * @return
     */
    public VO goodsPushWxMes(String mobile, String formInstanceId, String nodeFlag, String applicant,
                             String cutOffTime,String flowNo,String processId) {
        LogUtils.info("回调发送消息goodsPushWxMes-入参：mobile="+mobile+",formInstanceId="+formInstanceId
                +",nodeFlag="+nodeFlag+",applicant="+applicant+",cutOffTime="+cutOffTime+",flowNo="+flowNo+",processId="+processId);
        WxRequestDTO en = new WxRequestDTO();
        en.setMobile(mobile);
        en.setTemplate_id(goodsTemplateId);
        en.setMiniprogram_state(miniprogram_state);
        en.setPage(pageCmz + "?id=" + formInstanceId + "&nodeFlag=" + nodeFlag+"&time="+cutOffTime+"&flowNo="+flowNo+"&processId="+processId);

        HashMap<String, Object> map = new HashMap<>();
        HashMap<String, Object> map1 = new HashMap<>();
        map1.put("value", applicant);
        HashMap<String, Object> map2 = new HashMap<>();
        map2.put("value", mobile);
        HashMap<String, Object> map3 = new HashMap<>();
        map3.put("value", cutOffTime);
        map.put("phone_number2", map2);
        map.put("thing1", map1);
        map.put("time3", map3);

        en.setData(new JSONObject(map));

        //获取openId
        String openId = wxMobileHandler.getOpenId(mobile);
        if(StringUtils.isBlank(openId)){
            LogUtils.info(applicant + "-" + mobile + "对应的openId为空,所以不再发送微信消息！！！");
            return null;
        }
        //获取订阅次数
        String subscribe = wxMobileHandler.getSubscribe(openId);
        LogUtils.info(applicant + "-" + mobile + "对应的openId="+openId+",剩余订阅次数subscribe="+subscribe);
        if (StringUtils.isBlank(subscribe) || Integer.parseInt(subscribe) < 1) {
            LogUtils.info(applicant + "-" + mobile + "的订阅次数为" + subscribe + ",所以不再发送微信消息！！！");
            return null;
        }

        //发送微信消息
        wxMobileHandler.pushMessageToWx(en);

        return null;
    }

    /**
     * 外来人员备案信息表发微信消息
     * @param mobile 手机号
     * @return
     */
    public VO pushWxEntryConfig(String mobile,String name,String entryBeginTime,
                                String entryEndTime,String enterFactory){
        WxRequestDTO en = new WxRequestDTO();
        en.setMobile(mobile);
        en.setTemplate_id(entryConfigTemplate);
        en.setMiniprogram_state(miniprogram_state);
        en.setPage(entryConfigPage);

        HashMap<String, Object> map = new HashMap<>();
        HashMap<String, Object> map1 = new HashMap<>();
        map1.put("value", name);
        HashMap<String, Object> map2 = new HashMap<>();
        map2.put("value", mobile);
        HashMap<String, Object> map3 = new HashMap<>();
        map3.put("value", entryBeginTime.substring(0,16));
        HashMap<String, Object> map4 = new HashMap<>();
        map4.put("value", enterFactory);

        //给模板对应参数赋值
        map.put("name2", map1);
        map.put("phone_number3", map2);
        map.put("time1", map3);
        map.put("thing5", map4);

        en.setData(new JSONObject(map));

        //获取openId
        String openId = wxMobileHandler.getOpenId(mobile);
        if(StringUtils.isBlank(openId)){
            LogUtils.info(mobile + "对应的openId为空,所以不再发送微信消息！！！");
            return null;
        }
        //获取订阅次数
        String subscribe = wxMobileHandler.getSubscribe(openId);
        LogUtils.info(mobile + "对应的openId="+openId+",剩余订阅次数openId="+openId);
        if (StringUtils.isBlank(subscribe) || Integer.parseInt(subscribe) < 1) {
            LogUtils.info(mobile + "的订阅次数为" + subscribe + ",所以不再发送微信消息！！！");
            return null;
        }

        return wxMobileHandler.pushMessageToWx(en);
    }
}
