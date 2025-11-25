package com.sensor.controller;

import com.sensor.entity.SensorDataLog;
import com.sensor.entity.Threshold;
import com.sensor.repository.SensorDataLogRepository;
import com.sensor.repository.ThresholdRepo;
import com.sensor.service.MqttDataReceiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 传感器数据接口控制器
 *
 * 提供给前端使用的查询与接入能力，覆盖：
 * 1) 设备最新数据与历史分页查询
 * 2) 图表友好的时序数据（时间、温度、氧气）
 * 3) 设备列表（基于历史数据中出现过的设备ID）
 * 4) 设备摘要（最新数据 + 与当前阈值的达标判定）
 * 5) 联调数据接入（HTTP 注入与实际 MQTT 流程一致）
 */
@RestController
@RequestMapping("/api/sensors")
@CrossOrigin
@Tag(name = "Sensors")
public class SensorController {

    @Autowired
    private SensorDataLogRepository sensorDataLogRepository;

    @Autowired
    private ThresholdRepo thresholdRepo;

    @Autowired
    private MqttDataReceiveService mqttDataReceiveService;

    /**
     * 查询设备最新一条数据
     * @param deviceId 设备ID
     * @return 最新的传感器日志记录，若无返回null
     */
    @GetMapping("/latest")
    @Operation(summary = "查询设备最新数据")
    public SensorDataLog latest(@RequestParam String deviceId) {
        return sensorDataLogRepository.findTopByDeviceIdOrderByReceivedAtDesc(deviceId);
    }

    /**
     * 查询设备区间历史数据（分页）
     * @param deviceId 设备ID
     * @param from 起始时间（ISO8601，例如 2025-11-25T00:00:00+08:00）
     * @param to 截止时间（ISO8601）
     * @param page 页码（从0开始）
     * @param size 每页条数
     * @return 历史数据分页结果，按接收时间倒序
     */
    @GetMapping("/history")
    @Operation(summary = "查询设备历史数据（分页）")
    public Page<SensorDataLog> history(
            @RequestParam String deviceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PageRequest pr = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "receivedAt"));
        return sensorDataLogRepository.findByDeviceIdAndReceivedAtBetween(deviceId, from, to, pr);
    }

    /**
     * 获取历史数据中出现过的设备ID列表
     * @return 设备ID列表
     */
    @GetMapping("/devices")
    @Operation(summary = "获取历史数据出现的设备ID列表")
    public List<String> devices() {
        return sensorDataLogRepository.findDistinctDeviceIds();
    }

    /**
     * 设备摘要：返回最新数据与阈值达标判定，用于列表卡片与详情页的概览信息
     */
    /**
     * 设备摘要 DTO：用于设备卡片与详情概览
     */
    public static class Summary {
        /** 设备ID（设备序列号） */
        public String deviceId;
        /** 最新温度（℃） */
        public Double temperature;
        /** 最新氧气浓度（mg/L） */
        public Double oxygen;
        /** 温度是否在当前阈值范围内 */
        public boolean tempInRange;
        /** 氧气是否在当前阈值范围内 */
        public boolean oxyInRange;
        /** 最新数据接收时间 */
        public ZonedDateTime receivedAt;
    }

    /**
     * 获取设备摘要信息
     * @param deviceId 设备ID
     * @return 最新数据与达标判定
     */
    @GetMapping("/summary")
    @Operation(summary = "获取设备摘要（最新数据与达标判定）")
    public Summary summary(@RequestParam String deviceId) {
        SensorDataLog latest = sensorDataLogRepository.findTopByDeviceIdOrderByReceivedAtDesc(deviceId);
        Threshold t = thresholdRepo.getDefaultThreshold();
        Summary s = new Summary();
        s.deviceId = deviceId;
        if (latest != null) {
            s.temperature = latest.getTemperature();
            s.oxygen = latest.getOxygenConcentration();
            s.receivedAt = latest.getReceivedAt();
            s.tempInRange = s.temperature != null && t != null && s.temperature >= t.getTempMin() && s.temperature <= t.getTempMax();
            s.oxyInRange = s.oxygen != null && t != null && s.oxygen >= t.getOxyMin() && s.oxygen <= t.getOxyMax();
        }
        return s;
    }

    /**
     * 时序点 DTO：用于图表绘制
     */
    public static class SeriesPoint {
        /** 时间戳（升序） */
        public ZonedDateTime time;
        /** 温度（℃） */
        public Double temperature;
        /** 氧气浓度（mg/L） */
        public Double oxygen;
    }

    /**
     * 图表时序数据（升序），便于前端绘制曲线
     * @param deviceId 设备ID
     * @param from 起始时间
     * @param to 截止时间
     * @return 时序点列表（时间、温度、氧气）
     */
    @GetMapping("/series")
    @Operation(summary = "获取图表时序数据（升序）")
    public List<SeriesPoint> series(
            @RequestParam String deviceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime to
    ) {
        return sensorDataLogRepository
                .findByDeviceIdAndReceivedAtBetweenOrderByReceivedAtAsc(deviceId, from, to)
                .stream()
                .map(d -> {
                    SeriesPoint p = new SeriesPoint();
                    p.time = d.getReceivedAt();
                    p.temperature = d.getTemperature();
                    p.oxygen = d.getOxygenConcentration();
                    return p;
                })
                .collect(Collectors.toList());
    }

    /**
     * 联调数据接入入口：接收原始 JSON 并走与 MQTT 消息一致的处理链路
     * @param payload 原始JSON字符串
     */
    @PostMapping("/ingest")
    @Operation(summary = "联调数据接入（原始JSON）")
    public void ingest(@RequestBody String payload) {
        mqttDataReceiveService.processSensorData("devices/data/upload", payload);
    }
}
