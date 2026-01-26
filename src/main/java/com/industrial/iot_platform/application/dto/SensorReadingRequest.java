package com.industrial.iot_platform.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SensorReadingRequest {
    @NotNull(message = "Sensor type is required")
    private String sensorType; // e.g., "TEMPERATURE", "RPM", "PRESSURE"

    @NotNull(message = "Value is required")
    private Double value; // e.g., 23.5, 1500.0

    private String unit; // e.g., "Celsius", "PSI" (Optional)
}
