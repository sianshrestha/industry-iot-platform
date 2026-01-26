package com.industrial.iot_platform.application.controller;

import com.industrial.iot_platform.application.dto.SensorReadingRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/telemetry")
@RequiredArgsConstructor
public class IngestionController {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "telemetry_topic";

    @PostMapping
    public ResponseEntity<String> ingestTelemetry(
            @RequestBody SensorReadingRequest request,
            @AuthenticationPrincipal String deviceId // <-- Extracted from JWT automatically!
            ) {

        // 1. Construct the internal message
        Map<String, Object> message = new HashMap<>();
        message.put("deviceId", deviceId);
        message.put("sensorType", request.getSensorType());
        message.put("value", request.getValue());
        message.put("unit", request.getUnit());
        message.put("timestamp", Instant.now().toString());

        // 2. Push to Kafka (Async)
        kafkaTemplate.send(TOPIC, deviceId, message);

        // 3. Respond immediately (Low Latency)
        return ResponseEntity.ok("Telemetry data ingested successfully.");
    }
}
