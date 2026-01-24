package com.industrial.iot_platform.infrastructure.persistence;

import com.industrial.iot_platform.domain.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeviceRepository extends JpaRepository<Device, UUID> {
    Optional<Device> findByName(String name);

    boolean existsByName(String name);
}
