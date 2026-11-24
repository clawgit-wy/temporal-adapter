package com.yutong.safety.web.controller.pub.kafkames;

import com.alibaba.fastjson.JSONObject;
import com.yutong.safety.common.utils.DateUtils;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.errors.TimeoutException;
import org.apache.kafka.common.errors.TopicExistsException;

import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.yutong.safety.common.vo.KafkaVO._P10;
import static com.yutong.safety.common.vo.KafkaVO._P20;

/**
 * Kafka 消息发送测试工具
 * 特性：
 * 1. 自动创建 Topic（需 broker 配置 auto.create.topics.enable=true 或生产者配置 allow.auto.create.topics=true）
 * 2. 支持自定义消息内容
 * 3. 支持同步/异步发送
 */
public class KafkaMessageSender {

    private final Producer<String, String> producer;
    private final String topic;

    /**
     * 构造生产者
     * @param bootstrapServers Kafka 集群地址，如 "localhost:9092"
     * @param topic            Topic 名称（若不存在且允许自动创建则会自动创建）
     */
    public KafkaMessageSender(String bootstrapServers, String topic) {
        this.topic = topic;
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        // 允许自动创建 Topic（Kafka 2.x+ 默认 true，显式设置更安全）
        props.put("allow.auto.create.topics", "true");
        // 可选：提高发送可靠性（生产环境按需调整）
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 3);

        this.producer = new KafkaProducer<>(props);
    }

    /**
     * 同步发送消息（会等待 broker 确认）
     * @param message 消息内容
     * @return 元数据（包含 partition、offset 等）
     * @throws InterruptedException 线程中断异常
     * @throws ExecutionException   Kafka 执行异常（如 Topic 自动创建失败）
     */
    public RecordMetadata sendSync(String message) throws InterruptedException, ExecutionException {
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, message);
        Future<RecordMetadata> future = producer.send(record);
        return future.get(); // 同步等待结果
    }

    /**
     * 异步发送消息（不阻塞，通过回调处理结果）
     * @param message 消息内容
     * @param callback 发送完成后的回调（可为 null）
     */
    public void sendAsync(String message, Callback callback) {
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, message);
        producer.send(record, callback);
    }

    /**
     * 关闭生产者
     */
    public void close() {
        if (producer != null) {
            producer.flush(); // 确保所有消息已发送
            producer.close();
        }
    }

    // ---------- 测试入口 ----------
    public static void main(String[] args) throws Exception {
        // 1. 配置 Kafka 地址和 Topic 名称
        String bootstrapServers = "10.66.229.167:9092,10.66.229.168:9092,10.66.229.169:9092";  // 请修改为你的 Kafka 地址
        String topic = "clock-door-info-topic";            // 若 Topic 不存在，会自动创建

        KafkaMessageSender sender = new KafkaMessageSender(bootstrapServers, topic);

        // 2. 自定义消息内容（可以通过命令行参数传入）
        JSONObject after = new JSONObject();
        after.put("STAFFNO", "F13613866959");
        after.put("ZONENAME", "新能源");
        after.put("COMEGO", _P10);
        after.put("OPENDATE", DateUtils.format(new Date(), 2));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("table", "DBO.DMSOPENRECORD");
        jsonObject.put("after", after);
//        String customMessage = "Hello Kafka at " + System.currentTimeMillis();
        String customMessage = jsonObject.toJSONString();
        if (args.length > 0) {
            customMessage = String.join(" ", args); // 支持多参数拼接
        }
        System.out.println("准备发送消息: " + customMessage);

        try {
            // 3. 同步发送并打印结果
            RecordMetadata metadata = sender.sendSync(customMessage);
            System.out.printf("消息发送成功！ Topic: %s, Partition: %d, Offset: %d%n",
                    metadata.topic(), metadata.partition(), metadata.offset());
//
//            // 4. 异步发送示例（带回调）
//            sender.sendAsync(customMessage, (metadataAsync, exception) -> {
//                if (exception == null) {
//                    System.out.printf("异步发送成功 -> Offset: %d%n", metadataAsync.offset());
//                } else {
//                    System.err.println("异步发送失败: " + exception.getMessage());
//                }
//            });

            // 等待异步消息完成（生产环境建议使用 CountDownLatch）
            Thread.sleep(2000);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof TimeoutException) {
                System.err.println("发送超时，请检查 Kafka 是否可达或 Topic 自动创建是否启用");
            } else if (cause instanceof TopicExistsException) {
                System.err.println("Topic 已存在但配置冲突（极少发生）");
            } else {
                e.printStackTrace();
            }
        } finally {
            sender.close();
        }
    }
}
