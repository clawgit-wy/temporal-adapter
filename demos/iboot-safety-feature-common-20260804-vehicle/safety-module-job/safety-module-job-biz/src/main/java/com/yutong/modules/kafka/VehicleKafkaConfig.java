package com.yutong.modules.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * 车辆模块Kafka收发配置
 * broker地址与消费组在Nacos对应应用yaml中配置（spring.kafka.bootstrap-servers、spring.kafka.consumer.group-id），
 * 未配置时使用默认值，不阻塞应用启动
 */
//@Configuration
//@EnableKafka
public class VehicleKafkaConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id:" + VehicleKafkaConstant.DEFAULT_GROUP_ID + "}")
    private String groupId;

    /**
     * 生产者工厂
     */
    @Bean
    public ProducerFactory<String, String> vehicleProducerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        //尽量保证消息不丢失：全部副本确认 + 失败重试
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        return new DefaultKafkaProducerFactory<>(props);
    }

    /**
     * Kafka发送模板
     */
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(vehicleProducerFactory());
    }

    /**
     * 消费者工厂（手动ack，关闭自动提交）
     */
    @Bean
    public ConsumerFactory<String, String> vehicleConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    /**
     * @KafkaListener监听容器工厂
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(vehicleConsumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }

    /**
     * 发送topic（broker不允许自动建topic时仅告警，不影响启动）
     */
    @Bean
    public NewTopic releaseRecordSendTopic() {
        return TopicBuilder.name(VehicleKafkaConstant.RELEASE_RECORD_SEND_TOPIC).partitions(3).replicas(1).build();
    }

    /**
     * 接收topic（broker不允许自动建topic时仅告警，不影响启动）
     */
    @Bean
    public NewTopic releaseRecordReceiveTopic() {
        return TopicBuilder.name(VehicleKafkaConstant.RELEASE_RECORD_RECEIVE_TOPIC).partitions(3).replicas(1).build();
    }
}
