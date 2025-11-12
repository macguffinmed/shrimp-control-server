package com.sensor.entity;

/**
 * 阈值配置实体（记录温度、氧气值上下限，用于比对逻辑）
 */
public class Threshold {
    // 温度最小值（℃）
    private Double tempMin;
    // 温度最大值（℃）
    private Double tempMax;
    // 氧气浓度最小值（mg/L）
    private Double oxyMin;
    // 氧气浓度最大值（mg/L）
    private Double oxyMax;

    // 构造方法
    public Threshold(Double tempMin, Double tempMax, Double oxyMin, Double oxyMax) {
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.oxyMin = oxyMin;
        this.oxyMax = oxyMax;
    }

    // Getter
    public Double getTempMin() {
        return tempMin;
    }

    public Double getTempMax() {
        return tempMax;
    }

    public Double getOxyMin() {
        return oxyMin;
    }

    public Double getOxyMax() {
        return oxyMax;
    }
}