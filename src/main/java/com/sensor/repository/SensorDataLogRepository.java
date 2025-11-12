package com.sensor.repository;

import com.sensor.entity.SensorDataLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorDataLogRepository extends JpaRepository<SensorDataLog, Long> {
}