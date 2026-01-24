package com.industrial.iot_platform.domain.service;

import com.industrial.iot_platform.application.dto.DeviceRegisterRequest;
import com.industrial.iot_platform.application.dto.DeviceRegisterResponse;
import com.industrial.iot_platform.domain.model.Device;
import com.industrial.iot_platform.domain.model.DeviceCredentials;
import com.industrial.iot_platform.infrastructure.persistence.CredentialRepository;
import com.industrial.iot_platform.infrastructure.persistence.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final CredentialRepository credentialRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public DeviceRegisterResponse registerDevice(DeviceRegisterRequest request) {

        // 1. Validation: Check if Device with the same name already exists
        if (deviceRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Device with name "+ request.getName() + " already exists");
        }

        // 2. Create and Save the Device (Status: REGISTERED)
        Device device = Device.builder()
                .name(request.getName())
                .type(request.getType())
                .firmwareVersion(request.getFirmwareVersion())
                .status(Device.DeviceStatus.REGISTERED)
                .build();

        device = deviceRepository.save(device);

        // 3. Generate API Secret and Save Credentials
        String apiKey = generateSecureToken();

        // 4. Hash the API Secret Key
        String hashedKey = passwordEncoder.encode(apiKey);

        // 5. Save the Credentials
        DeviceCredentials credentials = DeviceCredentials.builder()
                .device(device)
                .secretHash(hashedKey)
                .build();

        credentialRepository.save(credentials);

        // 6. Return Response with Device ID and API Secret
        return DeviceRegisterResponse.builder()
                .deviceId(device.getId())
                .apiSecrect(apiKey)
                .build();
    }

    private String generateSecureToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32]; // 256 bits
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public String verifyDevice(UUID deviceId, String rawSecret) {
        // 1. Find the Credentials
        DeviceCredentials credentials = credentialRepository.findById(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("Device not found"));

        // 2. Verify the hash (Raw Secret vs DB Hash)
        boolean matches = passwordEncoder.matches(rawSecret, credentials.getSecretHash());

        if (!matches) {
            throw new SecurityException("Invalid credentials");
        }

        // 3. Return ID as the "Username" for JWT upon successful verification
        return deviceId.toString();

    }
}
