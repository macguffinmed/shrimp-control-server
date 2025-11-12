package com.sensor.entity;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 设备控制指令实体（匹配规范4.2：devices/config/alarm主题的JSON格式）
 */
public class DeviceControlCommand {

    @JSONField(name = "device_id")
    private String deviceId;

    @JSONField(name = "device_status")
    private int deviceStatus;

    public DeviceControlCommand() {
    }

    public DeviceControlCommand(String deviceId, int deviceStatus) {
        this.deviceId = deviceId;
        this.deviceStatus = deviceStatus;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(int deviceStatus) {
        this.deviceStatus = deviceStatus;
    }
}