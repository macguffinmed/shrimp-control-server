package com.sensor.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.sensor.entity.SensorDataLog;
import com.sensor.entity.SensorUploadData;
import com.sensor.entity.Threshold;
import com.sensor.entity.Device;
import com.sensor.entity.RegionConfig;
import com.sensor.repository.SensorDataLogRepository;
import com.sensor.repository.ThresholdRepo;
import com.sensor.repository.DeviceRepository;
import com.sensor.repository.RegionConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * MQTT数据接收服务（处理设备上传数据：解析、转发记录、触发控制）
 */
@Service
public class MqttDataReceiveService {

    private static final Logger logger = LoggerFactory.getLogger(MqttDataReceiveService.class);

    @Autowired
    private SensorDataLogRepository sensorDataLogRepository;

    // 注入设备控制服务
    @Autowired
    private DeviceControlService deviceControlService;
    // 注入阈值存储库（获取比对用的阈值）
    @Autowired
    private ThresholdRepo thresholdRepo;
    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private RegionConfigRepository regionConfigRepository;

    /**
     * 处理传感器上传数据
     *
     * 流程：
     * 1) 解析JSON（必要时修复格式）并校验device_id
     * 2) 持久化到 sensor_data_log（保存原始数据与主要指标）
     * 3) 根据设备/区域/全局优先级选择阈值
     * 4) 调用设备控制服务进行开停判定并发送指令
     * @param topic 订阅的主题（格式：devices/data/upload）
     * @param payload 设备上传的JSON数据（匹配规范4.1）
     */
    public void processSensorData(String topic, String payload) {
        logger.info("开始处理传感器数据，主题: {}, 数据: {}", topic, payload);
        
        try {
            // 步骤1：解析JSON数据为实体（匹配规范4.1的格式）
            SensorUploadData sensorData = null;
            
            try {
                sensorData = JSON.parseObject(payload, SensorUploadData.class);
                logger.debug("标准JSON解析成功");
            } catch (JSONException e) {
                // 如果标准解析失败，尝试修复JSON格式后再解析
                logger.warn("标准JSON解析失败，尝试修复格式: {}", payload);
                String fixedPayload = fixJsonFormat(payload);
                logger.debug("修复后的JSON: {}", fixedPayload);
                sensorData = JSON.parseObject(fixedPayload, SensorUploadData.class);
            }
            
            if (sensorData == null || sensorData.getDevice_id() == null) {
                logger.error("设备上传数据格式无效，或缺少device_id字段，不匹配规范4.1：{}", payload);
                return;
            }

            // 从消息体中获取设备ID
            String deviceId = sensorData.getDevice_id();
            logger.info("成功解析传感器数据，设备ID: {}", deviceId);

            // 规范4.1：保存到数据库
            SensorDataLog dataLog = new SensorDataLog();
            dataLog.setDeviceId(sensorData.getDevice_id());
            dataLog.setTemperature(sensorData.getTemperature_c());
            dataLog.setOxygenConcentration(sensorData.getConcentration_mgL());
            dataLog.setRawData(payload);
            dataLog.setReceivedAt(ZonedDateTime.now(ZoneId.of("UTC+8")));

            SensorDataLog savedLog = sensorDataLogRepository.save(dataLog);
            logger.info("传感器数据保存成功，日志ID: {}", savedLog.getId());

            // 步骤5：触发设备控制逻辑（调用设备控制服务）
            Device device = deviceRepository.findById(deviceId).orElseGet(() -> {
                Device d = new Device();
                d.setDeviceId(deviceId);
                return deviceRepository.save(d);
            });
            device.setLastSeen(ZonedDateTime.now(ZoneId.of("UTC+8")));
            deviceRepository.save(device);

            Threshold currentThreshold;
            if (device.getConfigPriority() != null && device.getConfigPriority().equalsIgnoreCase("DEVICE")
                    && device.getTempMin() != null && device.getTempMax() != null
                    && device.getOxyMin() != null && device.getOxyMax() != null) {
                currentThreshold = new Threshold(device.getTempMin(), device.getTempMax(), device.getOxyMin(), device.getOxyMax());
            } else if (device.getConfigPriority() != null && device.getConfigPriority().equalsIgnoreCase("REGION")
                    && device.getRegion() != null) {
                RegionConfig rc = regionConfigRepository.findById(device.getRegion()).orElse(null);
                if (rc != null) {
                    currentThreshold = new Threshold(rc.getTempMin(), rc.getTempMax(), rc.getOxyMin(), rc.getOxyMax());
                } else {
                    currentThreshold = thresholdRepo.getDefaultThreshold();
                }
            } else {
                currentThreshold = thresholdRepo.getDefaultThreshold();
            }
            deviceControlService.controlDevice(deviceId, sensorData, currentThreshold, savedLog.getId());
            logger.info("设备控制逻辑处理完成，设备ID: {}", deviceId);
        } catch (Exception e) {
            logger.error("处理传感器数据时发生错误: {}", payload, e);
        }
    }

    /**
     * 修复不规范的JSON格式（例如 device_id 未加引号）
     * @param payload 原始JSON字符串
     * @return 修复后的JSON字符串
     */
    private String fixJsonFormat(String payload) {
        // 使用正则表达式匹配device_id后跟冒号和非引号开始的值
        // 匹配模式: device_id:(非引号字符)
        String regex = "(\"device_id\"\\s*:)\\s*([^\"][^,}\\s]*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(payload);
        
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            // 将找到的未加引号的值加上双引号
            matcher.appendReplacement(sb, matcher.group(1) + "\"$2\"");
        }
        matcher.appendTail(sb);
        
        return sb.toString();
    }

    // The parseDeviceIdFromTopic method is no longer needed and has been removed.
}
