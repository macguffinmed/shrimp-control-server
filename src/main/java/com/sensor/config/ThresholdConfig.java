package com.sensor.config;

import com.sensor.entity.Threshold;
import com.sensor.repository.ThresholdRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * 阈值配置类（加载配置文件中的温度、氧气值阈值，用于比对逻辑）
 */
@Configuration
public class ThresholdConfig {

    // 从配置文件读取阈值
    @Value("${threshold.temp.min}")
    private Double tempMin;
    @Value("${threshold.temp.max}")
    private Double tempMax;
    @Value("${threshold.oxy.min}")
    private Double oxyMin;
    @Value("${threshold.oxy.max}")
    private Double oxyMax;

    // 注入阈值存储库（初始化默认阈值）
    @Autowired
    private ThresholdRepo thresholdRepo;

    /**
     * 项目启动时初始化默认阈值（PostConstruct：Bean初始化后执行）
     * 来源于 application.properties 的四个阈值配置项。
     */
    @PostConstruct
    public void initDefaultThreshold() {
        Threshold defaultThreshold = new Threshold(tempMin, tempMax, oxyMin, oxyMax);
        thresholdRepo.saveDefaultThreshold(defaultThreshold);
    }
}
