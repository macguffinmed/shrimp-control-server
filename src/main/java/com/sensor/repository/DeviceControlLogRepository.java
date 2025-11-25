package com.sensor.repository;

import com.sensor.entity.DeviceControlLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DeviceControlLogRepository extends JpaRepository<DeviceControlLog, Long> {
    Page<DeviceControlLog> findByDeviceId(String deviceId, Pageable pageable);
    DeviceControlLog findTopByDeviceIdOrderBySentAtDesc(String deviceId);
}
