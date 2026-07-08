package com.yash.watchtower.repository;

import com.yash.watchtower.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    List<Device> findByStatus(String status);
    List<Device> findByDeviceType(String deviceType);
}