package com.sensor.controller;

import com.sensor.entity.Device;
import com.sensor.entity.DeviceControlLog;
import com.sensor.entity.SensorDataLog;
import com.sensor.repository.DeviceRepository;
import com.sensor.repository.SensorDataLogRepository;
import com.sensor.repository.DeviceControlLogRepository;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 设备管理接口
 *
 * 覆盖设备列表/搜索、区域列表、未注册设备发现、增改删、设备级阈值读写与设备状态查询，
 * 用于“设备管理”和页面中的筛选/配置能力。
 */
@RestController
@RequestMapping("/api/devices")
@CrossOrigin
@Tag(name = "Devices")
public class DeviceMgmtController {

    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private SensorDataLogRepository sensorDataLogRepository;
    @Autowired
    private DeviceControlLogRepository deviceControlLogRepository;

    /**
     * 设备列表与搜索
     * @param region 区域筛选（可选）
     * @param q 关键字（按设备ID或名称模糊匹配，可选）
     */
    @GetMapping
    @Operation(summary = "设备列表与搜索")
    public Page<Device> list(@RequestParam(required = false) String region,
                             @RequestParam(required = false) String q,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "20") int size) {
        PageRequest pr = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "region", "name", "deviceId"));
        if (region != null && !region.isEmpty()) {
            return deviceRepository.findByRegion(region, pr);
        }
        if (q != null && !q.isEmpty()) {
            return deviceRepository.findByDeviceIdContainingOrNameContaining(q, q, pr);
        }
        return deviceRepository.findAll(pr);
    }

    /**
     * 返回已存在的区域列表
     */
    @GetMapping("/regions")
    @Operation(summary = "区域列表")
    public List<String> regions() {
        return deviceRepository.findDistinctRegions();
    }

    /**
     * 未注册设备发现：历史数据中出现但尚未注册到设备表的设备ID
     */
    @GetMapping("/discovered")
    @Operation(summary = "未注册设备发现")
    public List<String> discovered() {
        Set<String> registered = deviceRepository.findAll().stream().map(Device::getDeviceId).collect(Collectors.toSet());
        return sensorDataLogRepository.findDistinctDeviceIds().stream().filter(id -> !registered.contains(id)).collect(Collectors.toList());
    }

    /**
     * 设备创建请求 DTO
     */
    public static class CreateRequest {
        /** 设备ID（设备序列号） */
        public String deviceId;
        /** 设备名称 */
        public String name;
        /** 所属区域编码 */
        public String region;
    }

    /**
     * 新增设备（注册）
     */
    @PostMapping
    @Operation(summary = "新增设备")
    public Device create(@RequestBody CreateRequest req) {
        Device d = new Device();
        d.setDeviceId(req.deviceId);
        d.setName(req.name);
        d.setRegion(req.region);
        return deviceRepository.save(d);
    }

    /**
     * 设备更新请求 DTO
     */
    public static class UpdateRequest {
        /** 设备名称 */
        public String name;
        /** 所属区域编码 */
        public String region;
        /** 自动供氧开关 */
        public Boolean autoOxygenation;
        /** 阈值优先级（DEVICE/REGION/GLOBAL） */
        public String configPriority;
    }

    /**
     * 更新设备基本信息与控制优先级开关
     */
    @PutMapping("/{deviceId}")
    @Operation(summary = "更新设备信息")
    public Device update(@PathVariable String deviceId, @RequestBody UpdateRequest req) {
        Device d = deviceRepository.findById(deviceId).orElseGet(() -> { Device nd = new Device(); nd.setDeviceId(deviceId); return nd; });
        d.setName(req.name);
        d.setRegion(req.region);
        d.setAutoOxygenation(req.autoOxygenation);
        d.setConfigPriority(req.configPriority);
        return deviceRepository.save(d);
    }

    /**
     * 删除设备（同时会清理成员外键关联的风险由应用层控制）
     */
    @DeleteMapping("/{deviceId}")
    @Operation(summary = "删除设备")
    public void delete(@PathVariable String deviceId) {
        deviceRepository.deleteById(deviceId);
    }

    /**
     * 设备阈值更新请求 DTO
     */
    public static class ThresholdRequest {
        /** 温度下限（℃） */
        public Double tempMin;
        /** 温度上限（℃） */
        public Double tempMax;
        /** 氧气下限（mg/L） */
        public Double oxyMin;
        /** 氧气上限（mg/L） */
        public Double oxyMax;
        /** 阈值优先级（DEVICE/REGION/GLOBAL） */
        public String configPriority;
    }

    /**
     * 获取设备级阈值与优先级
     */
    @GetMapping("/{deviceId}/thresholds")
    @Operation(summary = "获取设备阈值与优先级")
    public Device getThresholds(@PathVariable String deviceId) {
        return deviceRepository.findById(deviceId).orElse(null);
    }

    /**
     * 更新设备级阈值与优先级
     */
    @PutMapping("/{deviceId}/thresholds")
    @Operation(summary = "更新设备阈值与优先级")
    public Device updateThresholds(@PathVariable String deviceId, @RequestBody ThresholdRequest req) {
        Device d = deviceRepository.findById(deviceId).orElseGet(() -> { Device nd = new Device(); nd.setDeviceId(deviceId); return nd; });
        d.setTempMin(req.tempMin);
        d.setTempMax(req.tempMax);
        d.setOxyMin(req.oxyMin);
        d.setOxyMax(req.oxyMax);
        d.setConfigPriority(req.configPriority);
        return deviceRepository.save(d);
    }

    /**
     * 设备状态 DTO
     */
    public static class Status {
        @Schema(description = "是否在线（最近5分钟内有上报）")
        public boolean connected;
        @Schema(description = "最近一次控制指令", allowableValues = {"start", "close", "add", "dec"})
        public String lastDeviceStatus;
        @Schema(description = "最近上报时间")
        public ZonedDateTime lastSeen;
    }

    /**
     * 查询设备状态（是否在线、最近开停状态）
     */
    @GetMapping("/{deviceId}/status")
    @Operation(summary = "查询设备状态")
    public Status status(@PathVariable String deviceId) {
        Status s = new Status();
        Device d = deviceRepository.findById(deviceId).orElse(null);
        if (d != null) {
            s.lastSeen = d.getLastSeen();
            if (s.lastSeen != null) {
                s.connected = ZonedDateTime.now().minusMinutes(5).isBefore(s.lastSeen);
            }
        }
        DeviceControlLog lastLog = deviceControlLogRepository.findTopByDeviceIdOrderBySentAtDesc(deviceId);
        if (lastLog != null) {
            s.lastDeviceStatus = lastLog.getDeviceStatus();
        }
        return s;
    }
}
