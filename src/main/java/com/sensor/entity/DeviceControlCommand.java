package com.sensor.entity;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 设备控制指令实体（匹配规范4.2：devices/config/alarm 主题的 JSON 格式）
 * 字段名通过 @JSONField 映射为设备端期望的键。
 */
public class DeviceControlCommand {

    /**
     * 设备ID（设备序列号），JSON键名：device_id
     */
    @JSONField(name = "device_id")
    private String deviceId;

    /**
     * 设备状态（1=启动，0=停止），JSON键名：device_status
     */
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
