package com.yash.watchtower.service;

import com.yash.watchtower.model.Device;
import com.yash.watchtower.model.SecurityEvent;
import com.yash.watchtower.repository.DeviceRepository;
import com.yash.watchtower.repository.SecurityEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class SecurityService {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private SecurityEventRepository eventRepository;

    // Calculate overall security score (0-100)
    public Map<String, Object> getSecurityScore() {
        List<Device> devices = deviceRepository.findAll();
        List<SecurityEvent> recentEvents = eventRepository.findTop20ByOrderByTimestampDesc();

        int score = 100;

        // Deduct for device threats
        for (Device d : devices) {
            score -= d.getThreatLevel() / 20;
            if (d.getStatus().equals("WARNING")) score -= 3;
            if (d.getStatus().equals("OFFLINE")) score -= 5;
        }

        // Deduct for recent critical events
        for (SecurityEvent e : recentEvents) {
            if (e.getSeverity().equals("CRITICAL") && !e.getStatus().equals("RESOLVED")) score -= 4;
            if (e.getSeverity().equals("HIGH") && !e.getStatus().equals("RESOLVED")) score -= 2;
        }

        score = Math.max(0, Math.min(100, score));

        Map<String, Object> result = new HashMap<>();
        result.put("score", score);
        result.put("rating", getRating(score));
        result.put("timestamp", LocalDateTime.now());
        return result;
    }

    private String getRating(int score) {
        if (score >= 90) return "SECURE";
        if (score >= 70) return "GOOD";
        if (score >= 50) return "AT RISK";
        if (score >= 30) return "VULNERABLE";
        return "CRITICAL";
    }

    // Dashboard summary stats
    public Map<String, Object> getDashboardStats() {
        List<Device> devices = deviceRepository.findAll();
        List<SecurityEvent> events = eventRepository.findAll();

        long online = devices.stream().filter(d -> d.getStatus().equals("ONLINE")).count();
        long warning = devices.stream().filter(d -> d.getStatus().equals("WARNING")).count();
        long offline = devices.stream().filter(d -> d.getStatus().equals("OFFLINE")).count();

        long critical = events.stream().filter(e -> e.getSeverity().equals("CRITICAL")).count();
        long high = events.stream().filter(e -> e.getSeverity().equals("HIGH")).count();

        int totalTraffic = devices.stream().mapToInt(Device::getTrafficMbps).sum();
        double avgCpu = devices.stream().mapToInt(Device::getCpuUsage).average().orElse(0);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalDevices", devices.size());
        stats.put("devicesOnline", online);
        stats.put("devicesWarning", warning);
        stats.put("devicesOffline", offline);
        stats.put("totalEvents", events.size());
        stats.put("criticalEvents", critical);
        stats.put("highEvents", high);
        stats.put("totalTrafficMbps", totalTraffic);
        stats.put("avgCpuUsage", Math.round(avgCpu));
        stats.put("blockedThreats", events.stream().filter(e -> e.getStatus().equals("BLOCKED")).count());

        return stats;
    }
}