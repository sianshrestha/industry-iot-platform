package com.industrial.iot_platform.application.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class DeviceRegisterRequest {

    @NotBlank(message = "Device name is required")
    private String name;

    @NotBlank(message = "Device type is required")
    private String type;

    private String firmwareVersion;
}
