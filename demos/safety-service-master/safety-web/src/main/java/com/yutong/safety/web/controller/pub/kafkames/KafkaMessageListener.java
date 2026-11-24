package com.yutong.safety.web.controller.pub.kafkames;

import com.alibaba.fastjson.JSONObject;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.vo.KafkaVO;
import com.yutong.safety.core.handler.SafetyVisitorHairpinHandler;
import com.yutong.safety.core.handler.enter.SafetyEnterFilingHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author caomg
 * @date 2024-11-19 10:17
 * @description KafkaMessageListener
 */
@Component
public class KafkaMessageListener {

    @Autowired
    SafetyVisitorHairpinHandler safetyVisitorHairpinHandler;

    @Autowired
    SafetyEnterFilingHandler safetyEnterFilingHandler;

//    测试环境测试步骤：
//    ①登录10.66.229.167，用户名/密码：root/Work1.hard
//    ②执行 /data/servers/kafka_2.12-3.2.0/bin/kafka-console-producer.sh --bootstrap-server 10.66.229.167:9092 --topic XXX(你自己的主题名)
//    ③在输入提示符>后面输入消息，按回车发送
//    ④停止测试时，按Ctrl+C退出
    @KafkaListener(topics = "clock-door-info-topic")
    public void listen(String message) {
        LogUtils.info("listen:{}", message);
        if (StringUtils.isBlank(message)) {
            return;
        }

        JSONObject sourceData = JSONObject.parseObject(message);
        //表名
        String table = sourceData.getString("table");
        JSONObject jsonObject = sourceData.getJSONObject("after");

        if (ObjectUtils.isEmpty(jsonObject)) {
            return;
        }
        //门禁
        if (KafkaVO.DMS.equals(table)) {
            String employNo = jsonObject.getString("STAFFNO");
            String factoryName = jsonObject.getString("ZONENAME");
            //进出类型
            String comeGo = jsonObject.getString("COMEGO");
            Date opendate = DateUtils.parse(jsonObject.getString("OPENDATE"), 2);

            safetyVisitorHairpinHandler.updateStatuWithKafka(employNo, factoryName, comeGo, opendate);
        }

        /*else if (KafkaVO.INV.equals(table)){
            //物流车进厂
            String tcm = jsonObject.getString("TCMID");
            String inTime = jsonObject.getString("INTIME");
            String carNo = jsonObject.getString("TOKENNO");
            String doorName = jsonObject.getString("INLANENAME");
            if (StringUtils.isBlank(carNo) || StringUtils.isBlank(tcm) || StringUtils.isBlank(inTime) ||(!KafkaVO.CQCL.equals(tcm) && !KafkaVO.LSCL.equals(tcm))) {
                LogUtils.info("kafka进厂消息信息缺失tcm={}，inTime={},carNo={}", tcm, inTime,carNo);
                return;
            }
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("carNo", carNo);
            paramMap.put("doorName", doorName);
            paramMap.put("carTime", DateUtils.format(DateUtils.parse(inTime,2),2));
            paramMap.put("tcm", tcm);
            paramMap.put("type", "进厂");

            safetyEnterFilingHandler.sendYXTMes(paramMap);

        }else if (KafkaVO.OUTV.equals(table)){
            //物流车出厂
            String tcm = jsonObject.getString("TCMID");
            String outTime = jsonObject.getString("OUTTIME");
            String carNo = jsonObject.getString("TOKENNO");
            String doorName = jsonObject.getString("OUTLANENAME");
            if (StringUtils.isBlank(carNo) || StringUtils.isBlank(tcm) || StringUtils.isBlank(outTime) ||(!KafkaVO.CQCL.equals(tcm) && !KafkaVO.LSCL.equals(tcm))) {
                LogUtils.info("kafka出厂消息信息缺失tcm={}，inTime={},carNo={}", tcm, outTime,carNo);
                return;
            }

            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("carNo", carNo);
            paramMap.put("doorName", doorName);
            paramMap.put("carTime", DateUtils.format(DateUtils.parse(outTime,2),2));
            paramMap.put("tcm", tcm);
            paramMap.put("type", "出厂");

            safetyEnterFilingHandler.sendYXTMes(paramMap);
        }*/
    }
}
