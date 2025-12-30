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
     * 设备状态（start/close/add/dec），JSON键名：device_status
     */
    @JSONField(name = "device_status")
    private String deviceStatus;

    @JSONField(name = "work_status")
    private String workStatus;

    @JSONField(name = "second")
    private Long second;

    public DeviceControlCommand() {
    }

    public DeviceControlCommand(String deviceId, String deviceStatus) {
        this.deviceId = deviceId;
        this.deviceStatus = deviceStatus;
    }

    public DeviceControlCommand(String deviceId, String deviceStatus, String workStatus, Long second) {
        this.deviceId = deviceId;
        this.deviceStatus = deviceStatus;
        this.workStatus = workStatus;
        this.second = second;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(String deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public String getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(String workStatus) {
        this.workStatus = workStatus;
    }

    public Long getSecond() {
        return second;
    }

    public void setSecond(Long second) {
        this.second = second;
    }
}
