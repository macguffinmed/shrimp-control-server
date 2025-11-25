package com.sensor.config;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import java.util.UUID;

/**
 * MQTT配置类（匹配规范2、3、5章：连接参数、主题、QoS、保留消息）
 *
 * 提供入站/出站通道与适配器配置：
 * - 入站：订阅设备上报主题，将消息转交业务处理
 * - 出站：发布设备控制指令，支持QoS与Retain设置
 */
@Configuration
public class MqttConfig {

    // 从配置文件读取协议参数
    @Value("${mqtt.broker}")
    private String broker;
    @Value("${mqtt.client-id}")
    private String clientId;
    @Value("${mqtt.username}")
    private String username;
    @Value("${mqtt.password}")
    private String password;
    @Value("${mqtt.keep-alive}")
    private int keepAlive;
    @Value("${mqtt.clean-session}")
    private boolean cleanSession;
    @Value("${mqtt.subscribe.topic}")
    private String subscribeTopic;
    @Value("${mqtt.qos}")
    private int qos;
    @Value("${mqtt.retain.alarm}")
    private boolean alarmRetain;

    // 入站消息处理在独立组件中完成，配置类不直接依赖业务服务

    /**
     * 1. MQTT客户端工厂（匹配规范2：连接属性）
     */
    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        
        // 规范1.2：TCP/IP传输层 + 1883端口
        options.setServerURIs(new String[]{broker});
        // 规范2.1：Clean Session=false
        options.setCleanSession(cleanSession);
        // 规范2.1：心跳间隔
        options.setKeepAliveInterval(keepAlive);
        // 规范2.1：认证（可选，有则配置）
        if (username != null && !username.trim().isEmpty()) {
            options.setUserName(username);
        }
        if (password != null && !password.trim().isEmpty()) {
            options.setPassword(password.toCharArray());
        }
        
        factory.setConnectionOptions(options);
        return factory;
    }

    /**
     * 2. MQTT入站通道（接收设备上传数据）
     */
    @Bean
    public MessageChannel mqttInboundChannel() {
        return new DirectChannel();
    }

    /**
     * 3. MQTT入站适配器（订阅设备上传主题，匹配规范3.1）
     */
    @Bean
    public MessageProducer mqttInboundAdapter() {
        // 订阅规范3.1的主题：devices/data/upload
        String inboundClientId = clientId + "_inbound_" + UUID.randomUUID().toString();
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
                inboundClientId,  // 入站客户端ID（避免冲突）
                mqttClientFactory(),
                subscribeTopic
        );
        // 规范5：QoS=0
        adapter.setQos(qos);
        // 消息转换器（JSON格式）
        adapter.setConverter(new DefaultPahoMessageConverter());
        // 绑定入站通道
        adapter.setOutputChannel(mqttInboundChannel());
        return adapter;
    }



    // 入站消息处理在独立的组件中（见 com.sensor.integration.MqttInboundMessageHandler），
    // 以避免在配置类中直接依赖业务服务造成循环依赖。

    /**
     * 5. MQTT出站通道（发送设备控制指令）
     */
    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    /**
     * 6. MQTT出站处理器（发布报警配置主题，匹配规范3.2、5）
     */
    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutboundHandler() {
        String outboundClientId = clientId + "_outbound_" + UUID.randomUUID().toString();
        MqttPahoMessageHandler handler = new MqttPahoMessageHandler(
                outboundClientId,  // 出站客户端ID（避免冲突）
                mqttClientFactory()
        );
        // 规范5：报警配置QoS=0
        handler.setDefaultQos(qos);
        // 规范5：报警配置保留消息（Retain=true）
        handler.setDefaultRetained(alarmRetain);
        // 从消息头读取具体发布主题（兼容 DeviceControlService 设置的 mqtt_topic 头）
        handler.setTopicExpressionString("headers['mqtt_topic']");
        // JSON消息转换器
        handler.setConverter(new DefaultPahoMessageConverter());
        // 异步发送（避免阻塞）
        handler.setAsync(true);
        return handler;
    }
}
