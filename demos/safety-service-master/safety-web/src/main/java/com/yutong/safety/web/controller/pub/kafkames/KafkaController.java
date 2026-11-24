package com.yutong.safety.web.controller.pub.kafkames;


import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "访客")
@RestController
@RequestMapping("/app/kafka")
public class KafkaController {

    @Resource
    private KafkaMessageListener kafkaMessageListener;

    @RequestMapping("/sendMsg")
    public Object add() {
        String s = "{\"after\":{\"STAFFNO\":\"F13613866959\",\"OPENDATE\":\"2026-04-10 16:27:11\",\"ZONENAME\":\"新能源\",\"COMEGO\":\"入口\"},\"table\":\"DBO.DMSOPENRECORD\"}";
        kafkaMessageListener.listen(s);
        return null;
    }
}
