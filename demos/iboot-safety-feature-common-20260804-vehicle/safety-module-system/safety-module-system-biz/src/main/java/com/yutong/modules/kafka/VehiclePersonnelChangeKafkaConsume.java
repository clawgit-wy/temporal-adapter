package com.yutong.modules.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 车辆人员变更-人员变更消息消费者
 */
@Slf4j
@Component
public class VehiclePersonnelChangeKafkaConsume {

    /**
     * 接收车辆人员变更消息
     */
    @KafkaListener(topics = "iboot-safety-employee-change-vehicle", groupId = "safety-vehicle-personnel-change-group")
    public void receiveEmployeeChange(String message) {
        log.info("收到车辆人员变更消息: {}", message);
    }
}
