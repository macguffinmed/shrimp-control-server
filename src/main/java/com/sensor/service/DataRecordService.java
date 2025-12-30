package com.sensor.service;

import com.sensor.entity.SensorUploadData;
import com.sensor.repository.SensorDataRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 数据记录服务（记录设备上传的温度、氧气值等数据，匹配业务需求）
 */
@Service
public class DataRecordService {

    private static final Logger logger = LoggerFactory.getLogger(DataRecordService.class);

    // 注入数据存储库（模拟/可扩展为数据库）
    @Autowired
    private SensorDataRepo sensorDataRepo;

    /**
     * 记录传感器数据
     * @param sensorData 设备上传的传感器数据（含温度、氧气值）
     */
    public void recordSensorData(SensorUploadData sensorData) {
        try {
            // 调用存储库保存数据（默认内存存储，可扩展为MySQL/Redis）
            sensorDataRepo.save(sensorData);
            logger.debug("设备[{}]数据记录成功：接收时间={}，温度={}℃，氧气浓度={}mg/L",
                    sensorData.getDevice_id(),
                    sensorData.getReceive_time(),
                    sensorData.getTemperature_c(),
                    sensorData.getConcentration_mgL());
        } catch (Exception e) {
            logger.error("设备[{}]数据记录失败", sensorData.getDevice_id(), e);
        }
    }
}