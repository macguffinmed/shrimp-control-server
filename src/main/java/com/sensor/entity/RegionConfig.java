package com.sensor.entity;

import javax.persistence.*;

/**
 * 区域阈值配置实体
 * 用于为区域设定氧气与温度的上下限，设备可选择优先使用区域阈值。
 */
@Entity
@Table(name = "region_config")
public class RegionConfig {
    /**
     * 区域编码（唯一）
     */
    @Id
    @Column(name = "region", nullable = false, unique = true)
    private String region;

    /**
     * 区域温度阈值下限（℃）
     */
    @Column(name = "temp_min")
    private Double tempMin;

    /**
     * 区域温度阈值上限（℃）
     */
    @Column(name = "temp_max")
    private Double tempMax;

    /**
     * 区域氧气阈值下限（mg/L）
     */
    @Column(name = "oxy_min")
    private Double oxyMin;

    /**
     * 区域氧气阈值上限（mg/L）
     */
    @Column(name = "oxy_max")
    private Double oxyMax;

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public Double getTempMin() { return tempMin; }
    public void setTempMin(Double tempMin) { this.tempMin = tempMin; }
    public Double getTempMax() { return tempMax; }
    public void setTempMax(Double tempMax) { this.tempMax = tempMax; }
    public Double getOxyMin() { return oxyMin; }
    public void setOxyMin(Double oxyMin) { this.oxyMin = oxyMin; }
    public Double getOxyMax() { return oxyMax; }
    public void setOxyMax(Double oxyMax) { this.oxyMax = oxyMax; }
}
