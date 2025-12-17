package com.sensor.entity;

import java.util.Date;

/**
 * 设备上传数据实体（匹配规范4.1：devices/data/upload 主题的 JSON 格式）
 * 字段名称与上报 JSON 保持一致，便于反序列化。
 */
public class SensorUploadData {
    /** 设备序列号（device_id） */
    private String device_id;
    /** 溶解氧饱和度（saturation_percent，%） */
    private Double saturation_percent;
    /** 溶解氧浓度（concentration_mgL，mg/L） */
    private Double concentration_mgL;
    /** 水温（temperature_c，℃） */
    private Double temperature_c;
    /** 盐度（salinity_psu，PSU） */
    private Double salinity_psu;
    /** 压力（pressure_kpa，kPa） */
    private Double pressure_kpa;
    /** 当前工作状态；值：working/stop */
    private String work_status;
    /** 工作时长，单位秒，默认值1 */
    private Long start_time;
    /** 扩展字段：服务端接收时间 */
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

    public String getWork_status() {
        return work_status;
    }

    public void setWork_status(String work_status) {
        this.work_status = work_status;
    }

    public Long getStart_time() {
        return start_time;
    }

    public void setStart_time(Long start_time) {
        this.start_time = start_time;
    }

    public Date getReceive_time() {
        return receive_time;
    }

    public void setReceive_time(Date receive_time) {
        this.receive_time = receive_time;
    }
}
