package com.industrial.iot_platform.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class TokenRequest {

    private UUID deviceId;

    @NotBlank
    private String apiSecret;
}
