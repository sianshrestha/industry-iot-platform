package com.industrial.iot_platform.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "sensor_readings")
@IdClass(SensorReadingId.class)
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
@Builder
public class SensorReading {

    @Id
    @Column(name = "device_id", nullable = false)
    private UUID deviceId;

    @Id
    @Column(nullable = false)
    private Instant time;

    @Id
    @Column(name = "sensor_type", nullable = false)
    private String sensorType;

    @Column(nullable = false)
    private Double value;


}
