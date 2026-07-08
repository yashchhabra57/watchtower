package com.yash.watchtower.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "devices")
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String ipAddress;
    private String deviceType;   // ROUTER, SWITCH, SERVER, FIREWALL, WORKSTATION
    private String status;       // ONLINE, OFFLINE, WARNING
    private int trafficMbps;     // current traffic
    private int cpuUsage;        // percentage
    private int threatLevel;     // 0-100
    private String location;
    private LocalDateTime lastSeen;
}