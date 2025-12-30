package com.sensor.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * 传感器数据日志实体（匹配规范4.1）
 */
@Entity
@Table(name = "sensor_data_log")
public class SensorDataLog {

    /**
     * 日志ID（自增主键）
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID'")
    private Long id;

    /**
     * 设备ID（与设备序列号一致）
     */
    @Column(name = "device_id", nullable = false, columnDefinition = "VARCHAR(255) NOT NULL COMMENT '设备ID'")
    private String deviceId;

    /**
     * 温度值（℃）
     */
    @Column(name = "temperature", columnDefinition = "DOUBLE COMMENT '温度值'")
    private Double temperature;

    /**
     * 氧气浓度（mg/L）
     */
    @Column(name = "oxygen_concentration", columnDefinition = "DOUBLE COMMENT '氧气浓度'")
    private Double oxygenConcentration;

    /**
     * 原始数据（完整上报JSON，便于追溯）
     */
    @Lob
    @Column(name = "raw_data", columnDefinition = "TEXT COMMENT '原始数据'")
    private String rawData;

    /**
     * 数据接收时间（服务器时间，含时区）
     */
    @Column(name = "received_at", nullable = false, columnDefinition = "DATETIME NOT NULL COMMENT '接收时间'")
    private ZonedDateTime receivedAt; // 数据接收时间

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getOxygenConcentration() {
        return oxygenConcentration;
    }

    public void setOxygenConcentration(Double oxygenConcentration) {
        this.oxygenConcentration = oxygenConcentration;
    }

    public String getRawData() {
        return rawData;
    }

    public void setRawData(String rawData) {
        this.rawData = rawData;
    }

    public ZonedDateTime getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(ZonedDateTime receivedAt) {
        this.receivedAt = receivedAt;
    }
}
