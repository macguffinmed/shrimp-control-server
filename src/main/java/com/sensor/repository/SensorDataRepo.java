package com.sensor.repository;

import com.sensor.entity.SensorUploadData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 传感器数据存储库（模拟内存存储，可扩展为持久化存储）
 */
@Repository
public class SensorDataRepo {

    private static final Logger logger = LoggerFactory.getLogger(SensorDataRepo.class);

    // 内存存储：key=设备ID，value=该设备的历史数据列表（线程安全）
    private final ConcurrentHashMap<String, List<SensorUploadData>> dataStore = new ConcurrentHashMap<>();

    /**
     * 保存传感器数据
     * @param sensorData 设备上传的数据
     */
    public void save(SensorUploadData sensorData) {
        // 按设备ID分组存储，无则创建新列表
        dataStore.computeIfAbsent(sensorData.getDevice_id(), k -> new ArrayList<>()).add(sensorData);
        // 可选：限制单设备历史数据数量（避免内存溢出）
        limitDataSize(sensorData.getDevice_id(), 1000);
    }

    /**
     * 获取设备历史数据
     * @param deviceId 设备ID
     * @return 该设备的历史数据列表
     */
    public List<SensorUploadData> getHistoryData(String deviceId) {
        return dataStore.getOrDefault(deviceId, new ArrayList<>());
    }

    /**
     * 限制单设备历史数据数量（保留最新N条）
     * @param deviceId 设备ID
     * @param maxSize 最大数据量
     */
    private void limitDataSize(String deviceId, int maxSize) {
        List<SensorUploadData> dataList = dataStore.get(deviceId);
        if (dataList != null && dataList.size() > maxSize) {
            // 删除最早的数据，保留最新maxSize条
            List<SensorUploadData> newDataList = dataList.subList(dataList.size() - maxSize, dataList.size());
            dataStore.put(deviceId, new ArrayList<>(newDataList));
            logger.debug("设备[{}]历史数据已截断，保留最新{}条", deviceId, maxSize);
        }
    }
}