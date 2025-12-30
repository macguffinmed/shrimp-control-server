package com.sensor.service;

import com.sensor.repository.SensorDataLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
public class HistoryMaintenanceService {

    @Autowired
    private SensorDataLogRepository sensorDataLogRepository;

    @Scheduled(cron = "0 0 3 * * *")
    public void pruneOldLogs() {
        ZonedDateTime cutoff = ZonedDateTime.now().minusDays(150);
        sensorDataLogRepository.deleteByReceivedAtBefore(cutoff);
    }
}

