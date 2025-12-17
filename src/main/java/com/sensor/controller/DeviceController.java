package com.sensor.controller;

import com.sensor.entity.DeviceControlLog;
import com.sensor.service.DeviceControlService;
import com.sensor.repository.DeviceControlLogRepository;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
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
        @Schema(description = "设备ID（设备序列号）", example = "TEMP001-OXY001", requiredMode = Schema.RequiredMode.REQUIRED)
        public String deviceId;

        @Schema(description = "指令类型", allowableValues = {"start", "close", "add", "dec"}, example = "start", requiredMode = Schema.RequiredMode.REQUIRED)
        public String deviceStatus;

        @Schema(name = "work_status", description = "当前工作状态", allowableValues = {"working", "stop"}, example = "working")
        public String work_status;

        @Schema(description = "单位秒，默认1", example = "1")
        public Long second;
    }

    @Autowired
    private DeviceControlService deviceControlService;

    @Autowired
    private DeviceControlLogRepository deviceControlLogRepository;

    /**
     * 手动调控设备
     * @param req 请求体：deviceId、deviceStatus（start/close/add/dec）
     */
    @PostMapping("/control")
    @Operation(summary = "手动调控设备（start/close/add/dec）")
    public void control(@RequestBody(description = "手动调控指令", required = true) @org.springframework.web.bind.annotation.RequestBody ControlRequest req) {
        deviceControlService.manualControl(req.deviceId, req.deviceStatus, req.work_status, req.second);
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
