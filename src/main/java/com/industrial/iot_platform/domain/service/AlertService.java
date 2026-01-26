package com.industrial.iot_platform.domain.service;

import com.industrial.iot_platform.domain.model.Alert;
import com.industrial.iot_platform.domain.model.SensorReading;
import com.industrial.iot_platform.infrastructure.persistence.AlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertService {
    private final AlertRepository alertRepository;

    public void checkForAnomalies(SensorReading reading) {
        // Simple Rule: Overheating Logic
        if ("TEMPERATURE".equalsIgnoreCase(reading.getSensorType()) && reading.getValue() > 100.0) {
            log.warn("HIGH TEMPERATURE DETECTED: Device {} at {}°C", reading.getDeviceId(), reading.getValue());

            Alert alert = Alert.builder()
                    .deviceId(reading.getDeviceId())
                    .triggeredValue(reading.getValue())
                    .severity(Alert.AlertSeverity.CRITICAL)
                    .build();

            alertRepository.save(alert);
            log.info("Alert saved to alerts DB for device {}", reading.getDeviceId());

        }
    }
}
