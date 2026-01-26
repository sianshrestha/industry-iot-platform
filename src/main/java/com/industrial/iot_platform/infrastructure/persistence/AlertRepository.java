package com.industrial.iot_platform.infrastructure.persistence;

import com.industrial.iot_platform.domain.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AlertRepository extends JpaRepository<Alert, UUID> {
}
