package com.sensor.simulation;

import com.alibaba.fastjson.JSON;
import com.sensor.entity.SensorUploadData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.boot.context.event.ApplicationReadyEvent;

/**
 * 启动后向 MQTT 发送一条模拟设备数据，用于端到端验证。
 */
@Component
public class MqttTestPublisher {

    private static final Logger logger = LoggerFactory.getLogger(MqttTestPublisher.class);

    @Autowired
    private MessageChannel mqttOutboundChannel;

    @Value("${mqtt.test.publish:false}")
    private boolean testPublish;

    @Value("${mqtt.test.device-id:TEMP001-OXY001}")
    private String deviceId;

    @EventListener(ApplicationReadyEvent.class)
    public void publishOnce() {
        if (!testPublish) {
            return;
        }

        try {
            // 构造符合规范4.1的上传数据
            SensorUploadData data = new SensorUploadData();
            data.setDevice_id(deviceId);
            data.setTemperature_c(24.3);
            data.setConcentration_mgL(5.94);
            data.setSaturation_percent(0.84);
            data.setSalinity_psu(30.0);
            data.setPressure_kpa(101.331);

            String payload = JSON.toJSONString(data);
            String topic = String.format("devices/%s/data/upload", deviceId);

            // 通过出站通道发布到指定主题（使用 header mqtt_topic）
            mqttOutboundChannel.send(
                    MessageBuilder.withPayload(payload)
                            .setHeader("mqtt_topic", topic)
                            .build()
            );

            logger.info("模拟数据已发布：topic={} payload={}", topic, payload);
        } catch (Exception e) {
            logger.error("模拟数据发布失败", e);
        }
    }
}