package com.yash.watchtower.service;

import com.yash.watchtower.model.Device;
import com.yash.watchtower.model.SecurityEvent;
import com.yash.watchtower.repository.DeviceRepository;
import com.yash.watchtower.repository.SecurityEventRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class MonitoringService {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private SecurityEventRepository eventRepository;

    private final Random random = new Random();

    private final String[] DEVICE_TYPES = {"ROUTER", "SWITCH", "SERVER", "FIREWALL", "WORKSTATION"};
    private final String[] LOCATIONS = {"HQ - Duluth", "Data Center A", "Data Center B", "Branch - Superior", "Cloud Region"};
    private final String[] EVENT_TYPES = {"LOGIN_ATTEMPT", "PORT_SCAN", "MALWARE", "DDOS", "INTRUSION", "FIREWALL_BLOCK"};
    private final String[] SEVERITIES = {"LOW", "MEDIUM", "HIGH", "CRITICAL"};

    // Seed the network with devices on startup
    @PostConstruct
    public void initializeNetwork() {
        if (deviceRepository.count() == 0) {
            String[][] devices = {
                    {"Core-Router-01", "10.0.0.1", "ROUTER"},
                    {"Core-Router-02", "10.0.0.2", "ROUTER"},
                    {"Dist-Switch-01", "10.0.1.1", "SWITCH"},
                    {"Dist-Switch-02", "10.0.1.2", "SWITCH"},
                    {"Firewall-Main", "10.0.0.254", "FIREWALL"},
                    {"Web-Server-01", "10.0.2.10", "SERVER"},
                    {"DB-Server-01", "10.0.2.20", "SERVER"},
                    {"Mail-Server", "10.0.2.30", "SERVER"},
                    {"WS-Finance-01", "10.0.3.15", "WORKSTATION"},
                    {"WS-HR-01", "10.0.3.25", "WORKSTATION"},
            };

            for (int i = 0; i < devices.length; i++) {
                Device d = new Device();
                d.setName(devices[i][0]);
                d.setIpAddress(devices[i][1]);
                d.setDeviceType(devices[i][2]);
                d.setStatus("ONLINE");
                d.setTrafficMbps(random.nextInt(500) + 50);
                d.setCpuUsage(random.nextInt(40) + 10);
                d.setThreatLevel(random.nextInt(20));
                d.setLocation(LOCATIONS[i % LOCATIONS.length]);
                d.setLastSeen(LocalDateTime.now());
                deviceRepository.save(d);
            }
        }
    }

    // Update device metrics every 3 seconds (live simulation)
    @Scheduled(fixedRate = 3000)
    public void updateDeviceMetrics() {
        List<Device> devices = deviceRepository.findAll();
        for (Device d : devices) {
            // Fluctuate traffic
            int trafficChange = random.nextInt(100) - 50;
            d.setTrafficMbps(Math.max(10, Math.min(1000, d.getTrafficMbps() + trafficChange)));

            // Fluctuate CPU
            int cpuChange = random.nextInt(20) - 10;
            d.setCpuUsage(Math.max(5, Math.min(99, d.getCpuUsage() + cpuChange)));

            // Occasionally change threat level
            if (random.nextInt(10) < 3) {
                d.setThreatLevel(Math.max(0, Math.min(100, d.getThreatLevel() + random.nextInt(30) - 15)));
            }

            // Status based on threat/cpu
            if (d.getThreatLevel() > 70 || d.getCpuUsage() > 95) {
                d.setStatus("WARNING");
            } else if (random.nextInt(100) < 2) {
                d.setStatus("OFFLINE");
            } else {
                d.setStatus("ONLINE");
            }

            d.setLastSeen(LocalDateTime.now());
            deviceRepository.save(d);
        }
    }

    // Generate security events every 4 seconds
    @Scheduled(fixedRate = 4000)
    public void generateSecurityEvent() {
        if (random.nextInt(100) < 70) {  // 70% chance each cycle
            SecurityEvent event = new SecurityEvent();
            String eventType = EVENT_TYPES[random.nextInt(EVENT_TYPES.length)];
            event.setEventType(eventType);
            event.setSeverity(pickSeverity(eventType));
            event.setSourceIp(randomIp());
            event.setTargetDevice(randomDeviceName());
            event.setDescription(describeEvent(eventType));
            event.setStatus(pickStatus(eventType));
            event.setTimestamp(LocalDateTime.now());
            eventRepository.save(event);
        }
        // Keep only recent events (cleanup old ones)
        cleanupOldEvents();
    }

    private String pickSeverity(String eventType) {
        switch (eventType) {
            case "MALWARE":
            case "INTRUSION":
            case "DDOS":
                return SEVERITIES[2 + random.nextInt(2)]; // HIGH or CRITICAL
            case "PORT_SCAN":
                return SEVERITIES[1 + random.nextInt(2)]; // MEDIUM or HIGH
            default:
                return SEVERITIES[random.nextInt(2)];     // LOW or MEDIUM
        }
    }

    private String pickStatus(String eventType) {
        String[] statuses = {"DETECTED", "BLOCKED", "INVESTIGATING", "RESOLVED"};
        if (eventType.equals("FIREWALL_BLOCK")) return "BLOCKED";
        return statuses[random.nextInt(statuses.length)];
    }

    private String describeEvent(String eventType) {
        switch (eventType) {
            case "LOGIN_ATTEMPT": return "Failed login attempt detected";
            case "PORT_SCAN": return "Sequential port scan detected from external host";
            case "MALWARE": return "Malware signature identified in network traffic";
            case "DDOS": return "Abnormal traffic spike - possible DDoS";
            case "INTRUSION": return "Unauthorized access attempt to protected resource";
            case "FIREWALL_BLOCK": return "Firewall blocked suspicious outbound connection";
            default: return "Security event detected";
        }
    }

    private String randomIp() {
        return random.nextInt(223) + 1 + "." + random.nextInt(256) + "." +
                random.nextInt(256) + "." + (random.nextInt(254) + 1);
    }

    private String randomDeviceName() {
        List<Device> devices = deviceRepository.findAll();
        if (devices.isEmpty()) return "Unknown";
        return devices.get(random.nextInt(devices.size())).getName();
    }

    private void cleanupOldEvents() {
        List<SecurityEvent> all = eventRepository.findAll();
        if (all.size() > 100) {
            all.sort(Comparator.comparing(SecurityEvent::getTimestamp));
            for (int i = 0; i < all.size() - 100; i++) {
                eventRepository.delete(all.get(i));
            }
        }
    }
}