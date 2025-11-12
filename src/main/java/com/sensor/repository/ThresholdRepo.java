package com.sensor.repository;

import com.sensor.entity.Threshold;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 * 阈值存储库（存储默认阈值，可扩展为设备专属阈值）
 */
@Repository
public class ThresholdRepo {

    private static final Logger logger = LoggerFactory.getLogger(ThresholdRepo.class);

    // 存储默认阈值（可扩展为Map<String, Threshold>存储设备专属阈值）
    private Threshold defaultThreshold;

    /**
     * 保存默认阈值
     * @param threshold 默认阈值（温度、氧气值上下限）
     */
    public void saveDefaultThreshold(Threshold threshold) {
        this.defaultThreshold = threshold;
        logger.info("默认阈值初始化完成：温度=[{}℃, {}℃]，氧气浓度=[{}mg/L, {}mg/L]",
                threshold.getTempMin(), threshold.getTempMax(),
                threshold.getOxyMin(), threshold.getOxyMax());
    }

    /**
     * 获取默认阈值
     * @return 默认阈值（null表示未初始化）
     */
    public Threshold getDefaultThreshold() {
        return this.defaultThreshold;
    }
}