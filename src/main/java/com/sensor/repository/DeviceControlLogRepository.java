package com.sensor.repository;

import com.sensor.entity.DeviceControlLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceControlLogRepository extends JpaRepository<DeviceControlLog, Long> {
}