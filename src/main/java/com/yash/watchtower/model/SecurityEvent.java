package com.yash.watchtower.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "security_events")
public class SecurityEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventType;    // LOGIN_ATTEMPT, PORT_SCAN, MALWARE, DDOS, INTRUSION, FIREWALL_BLOCK
    private String severity;     // LOW, MEDIUM, HIGH, CRITICAL
    private String sourceIp;
    private String targetDevice;
    private String description;
    private String status;       // DETECTED, BLOCKED, INVESTIGATING, RESOLVED
    private LocalDateTime timestamp;
}