package com.sensor.controller;

import com.sensor.entity.DeviceControlLog;
import com.sensor.service.DeviceControlService;
import com.sensor.repository.DeviceControlLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 设备控制接口
 *
 * 提供手动控制开/停与控制日志查询接口，用于前端设备卡片与详情页的交互。
 */
@RestController
@RequestMapping("/api/device")
@CrossOrigin
@Tag(name = "Device Control")
public class DeviceController {

    /**
     * 设备控制请求 DTO
     */
    public static class ControlRequest {
        /** 设备ID（设备序列号） */
        public String deviceId;
        /** 设备状态（1=启动；0=停止） */
        public Integer deviceStatus;
    }

    @Autowired
    private DeviceControlService deviceControlService;

    @Autowired
    private DeviceControlLogRepository deviceControlLogRepository;

    /**
     * 手动控制设备开/停
     * @param req 请求体：deviceId、deviceStatus（1=启动，0=停止）
     */
    @PostMapping("/control")
    @Operation(summary = "手动控制设备开/停")
    public void control(@RequestBody ControlRequest req) {
        deviceControlService.manualControl(req.deviceId, req.deviceStatus);
    }

    /**
     * 查询设备控制日志（分页）
     * @param deviceId 设备ID
     * @param page 页码
     * @param size 每页条数
     */
    @GetMapping("/control/logs")
    @Operation(summary = "查询设备控制日志（分页）")
    public Page<DeviceControlLog> logs(
            @RequestParam String deviceId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return deviceControlLogRepository.findByDeviceId(deviceId, PageRequest.of(page, size));
    }
}
