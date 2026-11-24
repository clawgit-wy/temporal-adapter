package com.yutong.safety;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@ImportResource("classpath:spring/application.xml")
@SpringBootApplication(scanBasePackages = "com.yutong")
public class SafetyKafkaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SafetyKafkaApplication.class, args);
    }

}
