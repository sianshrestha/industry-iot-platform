package com.industrial.iot_platform.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "alerts")
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
@Builder
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "device_id", nullable = false)
    private UUID deviceId;

    @Column(name = "triggered_value", nullable = false)
    private Double triggeredValue;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AlertSeverity severity;

    @Column(name = "triggered_at")
    @CreationTimestamp
    private Instant triggeredAt;

    public enum AlertSeverity {
        CRITICAL,
        WARNING,
        INFO
    }
}
