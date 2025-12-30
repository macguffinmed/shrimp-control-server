package com.sensor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 应用启动类（启动Spring Boot服务，加载所有配置和Bean）
 */
@SpringBootApplication
@EnableScheduling
public class MqttSensorApplication {

    public static void main(String[] args) {
        SpringApplication.run(MqttSensorApplication.class, args);
        System.out.println("传感器服务启动成功！");
    }
}
