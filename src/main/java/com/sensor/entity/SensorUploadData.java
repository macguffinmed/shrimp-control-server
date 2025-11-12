package com.sensor.entity;

import java.util.Date;

/**
 * 设备上传数据实体（匹配规范4.1：devices/data/upload主题的JSON格式）
 */
public class SensorUploadData {
    // 规范4.1字段：设备序列号（device_id）
    private String device_id;
    // 规范4.1字段：溶解氧饱和度（saturation_percent）
    private Double saturation_percent;
    // 规范4.1字段：溶解氧浓度（氧气值，单位mg/L，concentration_mgL）
    private Double concentration_mgL;
    // 规范4.1字段：温度（单位℃，temperature_c）
    private Double temperature_c;
    // 规范4.1字段：盐度（salinity_psu）
    private Double salinity_psu;
    // 规范4.1字段：压力（pressure_kpa）
    private Double pressure_kpa;
    // 扩展字段：数据接收时间（服务端记录用）
    private Date receive_time;

    // Getter + Setter（严格对应JSON字段名，避免序列化/反序列化异常）
    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public Double getSaturation_percent() {
        return saturation_percent;
    }

    public void setSaturation_percent(Double saturation_percent) {
        this.saturation_percent = saturation_percent;
    }

    public Double getConcentration_mgL() {
        return concentration_mgL;
    }

    public void setConcentration_mgL(Double concentration_mgL) {
        this.concentration_mgL = concentration_mgL;
    }

    public Double getTemperature_c() {
        return temperature_c;
    }

    public void setTemperature_c(Double temperature_c) {
        this.temperature_c = temperature_c;
    }

    public Double getSalinity_psu() {
        return salinity_psu;
    }

    public void setSalinity_psu(Double salinity_psu) {
        this.salinity_psu = salinity_psu;
    }

    public Double getPressure_kpa() {
        return pressure_kpa;
    }

    public void setPressure_kpa(Double pressure_kpa) {
        this.pressure_kpa = pressure_kpa;
    }

    public Date getReceive_time() {
        return receive_time;
    }

    public void setReceive_time(Date receive_time) {
        this.receive_time = receive_time;
    }
}