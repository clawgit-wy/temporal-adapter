package com.yutong.modules.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 安全责任人-人员变更消息消费者
 */
@Slf4j
@Component
public class ChangeToResponsibleKafkaConsume {

    /**
     * 接收安全责任人人员变更消息
     */
    @KafkaListener(topics = "iboot-safety-employee-change-responsible", groupId = "safety-responsible-group")
    public void receiveEmployeeChange(String message) {
        log.info("收到安全责任人人员变更消息: {}", message);
    }
}
