package com.yutong.modules.kafka.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

/**
 * 人员模块Kafka生产者：基于Spring Cloud Stream发送人员变更等消息
 */
@Slf4j
@Component
public class EmployeeKafkaProducer {

    @Autowired
    private StreamBridge streamBridge;

    /**
     * 发送人员变更（离职/岗位变更）消息：分别发送给安全责任人与车辆人员变更两个下游
     *
     * @param message 人员变更消息（包含变更类型和人员台账信息）
     */
    public void sendEmployeeChange(EmployeeChangeMessage message) {
        if (message == null || message.getEmployeeMessage() == null) {
            return;
        }
        // 通知安全责任人
        send(EmployeeKafkaConstant.SEND_EMPLOYEE_CHANGE_RESPONSIBLE_BINDING, message);
        // 通知车辆人员变更
        send(EmployeeKafkaConstant.SEND_EMPLOYEE_CHANGE_VEHICLE_BINDING, message);
    }

    /**
     * 发送消息到指定binding：异常仅记录日志，不影响另一条消息的发送
     *
     * @param bindingName 发送binding名称
     * @param message     人员变更消息
     */
    private void send(String bindingName, EmployeeChangeMessage message) {
        try {
            streamBridge.send(bindingName, message);
        } catch (Exception e) {
            log.error("人员变更消息发送kafka错误, binding:{}, empNo:{}", bindingName,
                    message.getEmployeeMessage() == null ? null : message.getEmployeeMessage().getEmpNo(), e);
        }
    }
}
