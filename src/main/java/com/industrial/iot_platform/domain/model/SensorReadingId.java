package com.industrial.iot_platform.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensorReadingId implements Serializable {
    private UUID deviceId;
    private Instant time;
    private String sensorType;
}
