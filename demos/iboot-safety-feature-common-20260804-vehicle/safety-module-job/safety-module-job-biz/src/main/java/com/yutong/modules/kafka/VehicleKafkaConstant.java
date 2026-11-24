package com.yutong.modules.kafka;

/**
 * 车辆模块Kafka常量
 */
public class VehicleKafkaConstant {

    /**
     * 发送topic：放行（出入）记录出方向，供下游系统消费
     */
    public static final String RELEASE_RECORD_SEND_TOPIC = "safety_vehicle_release_record";

    /**
     * 接收topic：外部系统推送的车辆出入记录
     */
    public static final String RELEASE_RECORD_RECEIVE_TOPIC = "safety_vehicle_release_receive";

    /**
     * 默认消费组
     */
    public static final String DEFAULT_GROUP_ID = "safety-vehicle-group";

    private VehicleKafkaConstant() {
    }
}
