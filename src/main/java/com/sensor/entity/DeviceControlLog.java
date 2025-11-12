package com.sensor.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * 设备控制日志实体（匹配规范4.2）
 */
@Entity
@Table(name = "device_control_log")
public class DeviceControlLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID'")
    private Long id;

    @Column(name = "device_id", nullable = false, columnDefinition = "VARCHAR(255) NOT NULL COMMENT '设备ID'")
    private String deviceId;

    @Column(name = "device_status", nullable = false, columnDefinition = "INT NOT NULL COMMENT '设备状态（1=启动，0=停止）'")
    private Integer deviceStatus;

    @Column(name = "triggering_data_id", nullable = false, columnDefinition = "BIGINT NOT NULL COMMENT '触发此指令的传感器数据日志ID'")
    private Long triggeringDataId;

    @Lob
    @Column(name = "raw_command", columnDefinition = "TEXT COMMENT '原始指令'")
    private String rawCommand;

    @Column(name = "sent_at", nullable = false, columnDefinition = "DATETIME NOT NULL COMMENT '发送时间'")
    private ZonedDateTime sentAt; // 指令发送时间

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

    public Integer getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(Integer deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public Long getTriggeringDataId() {
        return triggeringDataId;
    }

    public void setTriggeringDataId(Long triggeringDataId) {
        this.triggeringDataId = triggeringDataId;
    }

    public String getRawCommand() {
        return rawCommand;
    }

    public void setRawCommand(String rawCommand) {
        this.rawCommand = rawCommand;
    }

    public ZonedDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(ZonedDateTime sentAt) {
        this.sentAt = sentAt;
    }
}