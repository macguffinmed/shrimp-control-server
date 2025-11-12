package com.sensor.service;

import com.alibaba.fastjson.JSON;
import com.sensor.entity.DeviceControlCommand;
import com.sensor.entity.DeviceControlLog;
import com.sensor.entity.SensorDataLog;
import com.sensor.entity.SensorUploadData;
import com.sensor.entity.Threshold;
import com.sensor.repository.DeviceControlLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * 设备控制服务（基于阈值比对逻辑，发送开关指令，匹配规范3.2、4.2）
 */
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
public class DeviceControlService {

    private static final Logger logger = LoggerFactory.getLogger(DeviceControlService.class);

    @Autowired
    private DeviceControlLogRepository deviceControlLogRepository;

    // 从配置文件读取发布主题模板（规范3.2：devices/config/alarm）
    @Value("${mqtt.publish.topic.template}")
    private String publishTopicTemplate;

    // 注入MQTT出站通道（发送控制指令）
    @Autowired
    private MessageChannel mqttOutboundChannel;

    /**
     * 控制设备开关（核心逻辑：阈值比对）
     * @param deviceId 设备ID
     * @param sensorData 设备上传的实时数据（含温度、氧气值）
     * @param threshold 温度、氧气值阈值（用于比对）
     * @param savedSensorDataLogId 已保存的传感器数据日志ID
     */
    public void controlDevice(String deviceId, SensorUploadData sensorData, Threshold threshold, Long savedSensorDataLogId) {
        // 步骤1：阈值比对逻辑
        boolean needStartDevice = true;  // 默认启动设备
        Double currentTemp = sensorData.getTemperature_c();
        Double currentOxy = sensorData.getConcentration_mgL();

        // 温度超出阈值（低于最小值或高于最大值）→ 停止设备
        if (currentTemp < threshold.getTempMin() || currentTemp > threshold.getTempMax()) {
            needStartDevice = false;
            logger.warn("设备[{}]温度超出阈值：当前={}℃，阈值范围=[{}℃, {}℃]",
                    deviceId, currentTemp, threshold.getTempMin(), threshold.getTempMax());
        }
        // 氧气浓度超出阈值（低于最小值或高于最大值）→ 停止设备
        else if (currentOxy < threshold.getOxyMin() || currentOxy > threshold.getOxyMax()) {
            needStartDevice = false;
            logger.warn("设备[{}]氧气浓度超出阈值：当前={}mg/L，阈值范围=[{}mg/L, {}mg/L]",
                    deviceId, currentOxy, threshold.getOxyMin(), threshold.getOxyMax());
        }

        // 步骤2：生成控制指令（匹配规范4.2：device_status=1启动，0停止）
        int deviceStatus = needStartDevice ? 1 : 0;
        DeviceControlCommand controlMsg = new DeviceControlCommand(deviceId, deviceStatus);
        String controlJson = JSON.toJSONString(controlMsg);

        // 步骤3：构建发布主题（规范3.2：devices/config/alarm）
        String publishTopic = publishTopicTemplate;

        // 步骤4：发送MQTT指令
        sendControlMsg(publishTopic, controlJson, deviceStatus, deviceId, savedSensorDataLogId);
    }

    /**
     * 发送设备控制指令到MQTT服务器
     * @param topic 发布主题（规范3.2）
     * @param controlJson 控制指令JSON（规范4.2）
     * @param deviceStatus 设备状态（1=启动，0=停止）
     * @param deviceId 设备ID
     * @param triggeringDataId 触发此命令的传感器数据日志ID
     */
    private void sendControlMsg(String topic, String controlJson, int deviceStatus, String deviceId, Long triggeringDataId) {
        try {
            // 构建MQTT消息并设置发布主题（Spring Integration要求通过header传递mqtt_topic）
            Message<String> message = MessageBuilder
                    .withPayload(controlJson)
                    .setHeader("mqtt_topic", topic)
                    .build();
            // 发送消息到出站通道（超时时间5秒）
            boolean sendSuccess = mqttOutboundChannel.send(message, 5000);

            if (sendSuccess) {
                logger.info("设备控制指令发送成功：主题={}，指令={}（状态：{}）",
                        topic, controlJson, deviceStatus == 1 ? "启动" : "停止");

                // 保存到数据库
                DeviceControlLog controlLog = new DeviceControlLog();
                controlLog.setDeviceId(deviceId);
                controlLog.setDeviceStatus(deviceStatus);
                controlLog.setTriggeringDataId(triggeringDataId);
                controlLog.setRawCommand(controlJson);
                controlLog.setSentAt(ZonedDateTime.now(ZoneId.of("UTC+8")));

                deviceControlLogRepository.save(controlLog);

            } else {
                logger.error("设备控制指令发送超时：主题={}，指令={}", topic, controlJson);
            }
        } catch (Exception e) {
            logger.error("设备控制指令发送失败：主题={}，指令={}", topic, controlJson, e);
        }
    }
}