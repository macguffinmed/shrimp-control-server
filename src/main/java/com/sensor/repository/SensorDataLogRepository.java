package com.sensor.repository;

import com.sensor.entity.SensorDataLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.ZonedDateTime;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface SensorDataLogRepository extends JpaRepository<SensorDataLog, Long> {
    SensorDataLog findTopByDeviceIdOrderByReceivedAtDesc(String deviceId);
    Page<SensorDataLog> findByDeviceIdAndReceivedAtBetween(String deviceId, ZonedDateTime from, ZonedDateTime to, Pageable pageable);
    List<SensorDataLog> findByDeviceIdAndReceivedAtBetweenOrderByReceivedAtAsc(String deviceId, ZonedDateTime from, ZonedDateTime to);

    @Query("select distinct s.deviceId from SensorDataLog s")
    List<String> findDistinctDeviceIds();

    void deleteByReceivedAtBefore(ZonedDateTime time);
}
