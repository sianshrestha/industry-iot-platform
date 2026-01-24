package com.industrial.iot_platform.domain.model;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "device_credentials")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class DeviceCredentials {

    @Id
    @Column(name = "device_id")
    private UUID deviceId;

    @Column(name = "secret_hash", nullable = false)
    private String secretHash;

    @UpdateTimestamp
    @Column(name = "last_rotated_at")
    private Instant lastRotatedAt;

    @OneToOne
    @MapsId
    @JoinColumn(name = "device_id")
    private Device device;
}
