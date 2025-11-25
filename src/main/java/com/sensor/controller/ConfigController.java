package com.sensor.controller;

import com.sensor.entity.Threshold;
import com.sensor.repository.ThresholdRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 配置接口
 *
 * 提供全局阈值的读取与更新，以及滑块允许范围的查询，用于页面的阈值设置与校验提示。
 */
@RestController
@RequestMapping("/api/config")
@CrossOrigin
@Tag(name = "Config")
public class ConfigController {

    /**
     * 全局阈值更新请求 DTO
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
    }

    @Autowired
    private ThresholdRepo thresholdRepo;

    /**
     * 获取当前全局阈值
     */
    @GetMapping("/thresholds")
    @Operation(summary = "获取全局阈值")
    public Threshold get() {
        return thresholdRepo.getDefaultThreshold();
    }

    /**
     * 更新全局阈值
     * @param req 温度上下限、氧气上下限
     */
    @PutMapping("/thresholds")
    @Operation(summary = "更新全局阈值")
    public Threshold update(@RequestBody ThresholdRequest req) {
        Threshold t = new Threshold(req.tempMin, req.tempMax, req.oxyMin, req.oxyMax);
        thresholdRepo.updateDefaultThreshold(t);
        return t;
    }

    /**
     * 滑块允许范围 DTO
     */
    public static class RangeResponse {
        /** 温度允许下限（℃） */
        public Double tempMinAllowed;
        /** 温度允许上限（℃） */
        public Double tempMaxAllowed;
        /** 氧气允许下限（mg/L） */
        public Double oxyMinAllowed;
        /** 氧气允许上限（mg/L） */
        public Double oxyMaxAllowed;
    }

    /**
     * 返回设置滑块的允许范围（用于前端校验与提示）
     */
    @GetMapping("/ranges")
    @Operation(summary = "获取滑块允许范围")
    public RangeResponse ranges() {
        RangeResponse r = new RangeResponse();
        r.tempMinAllowed = 20.0;
        r.tempMaxAllowed = 32.0;
        r.oxyMinAllowed = 5.0;
        r.oxyMaxAllowed = 10.0;
        return r;
    }
}
