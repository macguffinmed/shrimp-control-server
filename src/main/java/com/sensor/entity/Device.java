package com.sensor.entity;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "device")
public class Device {
    /**
     * 设备序列号（唯一标识）
     */
    @Id
    @Column(name = "device_id", nullable = false, unique = true)
    private String deviceId;

    /**
     * 设备名称（用于页面显示）
     */
    @Column(name = "name")
    private String name;

    /**
     * 所属区域编码（用于区域筛选与区域阈值）
     */
    @Column(name = "region")
    private String region;

    /**
     * 自动供氧开关（true=自动供氧；false=手动）
     */
    @Column(name = "auto_oxygenation")
    private Boolean autoOxygenation;

    /**
     * 阈值优先级（DEVICE/REGION/GLOBAL）
     */
    @Column(name = "config_priority")
    private String configPriority;

    /**
     * 设备级温度阈值下限（℃）
     */
    @Column(name = "temp_min")
    private Double tempMin;

    /**
     * 设备级温度阈值上限（℃）
     */
    @Column(name = "temp_max")
    private Double tempMax;

    /**
     * 设备级氧气阈值下限（mg/L）
     */
    @Column(name = "oxy_min")
    private Double oxyMin;

    /**
     * 设备级氧气阈值上限（mg/L）
     */
    @Column(name = "oxy_max")
    private Double oxyMax;

    /**
     * 最近上报时间（用于在线状态判定）
     */
    @Column(name = "last_seen")
    private ZonedDateTime lastSeen;

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public Boolean getAutoOxygenation() { return autoOxygenation; }
    public void setAutoOxygenation(Boolean autoOxygenation) { this.autoOxygenation = autoOxygenation; }
    public String getConfigPriority() { return configPriority; }
    public void setConfigPriority(String configPriority) { this.configPriority = configPriority; }
    public Double getTempMin() { return tempMin; }
    public void setTempMin(Double tempMin) { this.tempMin = tempMin; }
    public Double getTempMax() { return tempMax; }
    public void setTempMax(Double tempMax) { this.tempMax = tempMax; }
    public Double getOxyMin() { return oxyMin; }
    public void setOxyMin(Double oxyMin) { this.oxyMin = oxyMin; }
    public Double getOxyMax() { return oxyMax; }
    public void setOxyMax(Double oxyMax) { this.oxyMax = oxyMax; }
    public ZonedDateTime getLastSeen() { return lastSeen; }
    public void setLastSeen(ZonedDateTime lastSeen) { this.lastSeen = lastSeen; }
}
