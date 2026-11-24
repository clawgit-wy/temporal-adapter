package com.yutong.modules.kafka.user;

/**
 * 人员模块Kafka常量
 */
public class EmployeeKafkaConstant {

    /**
     * 发送binding：人员变更（离职/岗位变更）通知-安全责任人
     * 对应配置 spring.cloud.stream.bindings.sendEmployeeChangeToResponsible-out-0.destination
     */
    public static final String SEND_EMPLOYEE_CHANGE_RESPONSIBLE_BINDING = "sendEmployeeChangeToResponsible-out-0";

    /**
     * 发送topic：安全责任人-人员变更通知
     */
    public static final String EMPLOYEE_CHANGE_RESPONSIBLE_TOPIC = "iboot-safety-employee-change-responsible";

    /**
     * 发送binding：人员变更（离职/岗位变更）通知-车辆人员变更
     * 对应配置 spring.cloud.stream.bindings.sendEmployeeChangeToVehicle-out-0.destination
     */
    public static final String SEND_EMPLOYEE_CHANGE_VEHICLE_BINDING = "sendEmployeeChangeToVehicle-out-0";

    /**
     * 发送topic：车辆人员变更-人员变更通知
     */
    public static final String EMPLOYEE_CHANGE_VEHICLE_TOPIC = "iboot-safety-employee-change-vehicle";

    private EmployeeKafkaConstant() {
    }
}
