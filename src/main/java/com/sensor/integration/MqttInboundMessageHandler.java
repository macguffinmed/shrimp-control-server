package com.sensor.integration;

import com.sensor.service.MqttDataReceiveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * 独立的MQTT入站消息处理器，避免配置类与业务服务形成循环依赖。
 */
@Component
public class MqttInboundMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(MqttInboundMessageHandler.class);

    @Autowired
    private MqttDataReceiveService mqttDataReceiveService;

    /**
     * 处理从 mqttInboundChannel 收到的消息。
     * @param message Spring Integration消息，包含 MQTT 主题与载荷
     */
    @ServiceActivator(inputChannel = "mqttInboundChannel")
    public void handleInbound(Message<?> message) {
        String topic = String.valueOf(message.getHeaders().get("mqtt_receivedTopic"));
        String payload = String.valueOf(message.getPayload());
        logger.info("收到原始MQTT消息: topic={}, payload={}", topic, payload);
        mqttDataReceiveService.processSensorData(topic, payload);
    }


}
