package com.industrial.iot_platform.application.controller;

import com.industrial.iot_platform.application.dto.DeviceRegisterRequest;
import com.industrial.iot_platform.application.dto.DeviceRegisterResponse;
import com.industrial.iot_platform.application.dto.TokenRequest;
import com.industrial.iot_platform.application.dto.TokenResponse;
import com.industrial.iot_platform.domain.service.DeviceService;
import com.industrial.iot_platform.infrastructure.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final DeviceService deviceService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<DeviceRegisterResponse> registerDevice(@Valid @RequestBody DeviceRegisterRequest request) {
        DeviceRegisterResponse response = deviceService.registerDevice(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> loginDevice(@Valid @RequestBody TokenRequest request) {
        // 1. Verify Device Credentials
        String deviceId = deviceService.verifyDevice(request.getDeviceId(), request.getApiSecret());

        // 2. Generate JWT Token
        String jwtToken = jwtService.generateToken(deviceId);

        return ResponseEntity.ok(new TokenResponse(jwtToken));
    }
}
