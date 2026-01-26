package com.industrial.iot_platform.application.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class DeviceRegisterResponse {

    private UUID deviceId;

    private String apiSecret;
}
