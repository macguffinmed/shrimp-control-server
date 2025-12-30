package com.sensor.repository;

import com.sensor.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DeviceRepository extends JpaRepository<Device, String> {
    Page<Device> findByRegion(String region, Pageable pageable);
    @Query("select distinct d.region from Device d where d.region is not null")
    List<String> findDistinctRegions();
    Page<Device> findByDeviceIdContainingOrNameContaining(String deviceId, String name, Pageable pageable);
}

