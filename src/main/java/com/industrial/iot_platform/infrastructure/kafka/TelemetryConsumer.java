package com.industrial.iot_platform.infrastructure.kafka;

import com.industrial.iot_platform.domain.model.SensorReading;
import com.industrial.iot_platform.domain.service.AlertService;
import com.industrial.iot_platform.infrastructure.persistence.SensorReadingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TelemetryConsumer {
    private final SensorReadingRepository repository;
    private final AlertService alertService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "telemetry_topic", groupId = "iot-processor-group")
    public void consume(String message) {
        log.info("Consumed message: {}", message);

        try {
            // 1. Convert JSON string to Map
            Map<String, Object> payload = objectMapper.readValue(message, new TypeReference<>() {});

            // 2. Map JSON fields to SensorReading entity fields
            SensorReading sensorReading = SensorReading.builder()
                    .deviceId(UUID.fromString((String) payload.get("deviceId")))
                    .time(Instant.parse((String) payload.get("timestamp")))
                    .sensorType((String) payload.get("sensorType"))
                    .value(((Number) payload.get("value")).doubleValue())
                    .build();

            // 3. Save to database
            repository.save(sensorReading);
            log.info("Saved sensor reading telemetry to DB for device: {}", sensorReading.getDeviceId());

            // 4. Check for alerts
            alertService.checkForAnomalies(sensorReading);

            } catch (Exception e) {
                log.error("Error processing telemetry: {}", message, e);
            }
        }

}
