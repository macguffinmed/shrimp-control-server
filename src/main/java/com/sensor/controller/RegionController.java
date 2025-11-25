package com.sensor.controller;

import com.sensor.entity.RegionConfig;
import com.sensor.repository.RegionConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/regions")
@CrossOrigin
@Tag(name = "Regions")
public class RegionController {

    @Autowired
    private RegionConfigRepository regionConfigRepository;

    @GetMapping
    @Operation(summary = "区域阈值分页列表")
    public Page<RegionConfig> list(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "20") int size) {
        return regionConfigRepository.findAll(PageRequest.of(page, size));
    }

    @GetMapping("/{region}")
    @Operation(summary = "获取区域阈值")
    public RegionConfig get(@PathVariable String region) {
        return regionConfigRepository.findById(region).orElse(null);
    }

    public static class UpsertRequest {
        public String region;
        public Double tempMin;
        public Double tempMax;
        public Double oxyMin;
        public Double oxyMax;
    }

    @PostMapping
    @Operation(summary = "创建区域阈值")
    public RegionConfig create(@RequestBody UpsertRequest req) {
        RegionConfig rc = new RegionConfig();
        rc.setRegion(req.region);
        rc.setTempMin(req.tempMin);
        rc.setTempMax(req.tempMax);
        rc.setOxyMin(req.oxyMin);
        rc.setOxyMax(req.oxyMax);
        return regionConfigRepository.save(rc);
    }

    @PutMapping("/{region}")
    @Operation(summary = "更新区域阈值")
    public RegionConfig update(@PathVariable String region, @RequestBody UpsertRequest req) {
        RegionConfig rc = regionConfigRepository.findById(region).orElseGet(() -> { RegionConfig n = new RegionConfig(); n.setRegion(region); return n; });
        rc.setTempMin(req.tempMin);
        rc.setTempMax(req.tempMax);
        rc.setOxyMin(req.oxyMin);
        rc.setOxyMax(req.oxyMax);
        return regionConfigRepository.save(rc);
    }

    @DeleteMapping("/{region}")
    @Operation(summary = "删除区域阈值")
    public void delete(@PathVariable String region) {
        regionConfigRepository.deleteById(region);
    }
}
