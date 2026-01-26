package com.industrial.iot_platform.infrastructure.persistence;

import com.industrial.iot_platform.domain.model.SensorReading;
import com.industrial.iot_platform.domain.model.SensorReadingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SensorReadingRepository extends JpaRepository<SensorReading, SensorReadingId> {
}
