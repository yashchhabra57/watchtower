package com.yash.watchtower.controller;

import com.yash.watchtower.model.Device;
import com.yash.watchtower.model.SecurityEvent;
import com.yash.watchtower.repository.DeviceRepository;
import com.yash.watchtower.repository.SecurityEventRepository;
import com.yash.watchtower.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private SecurityEventRepository eventRepository;

    @Autowired
    private SecurityService securityService;

    // Get all network devices
    @GetMapping("/devices")
    public List<Device> getDevices() {
        return deviceRepository.findAll();
    }

    // Get devices by status
    @GetMapping("/devices/status/{status}")
    public List<Device> getDevicesByStatus(@PathVariable String status) {
        return deviceRepository.findByStatus(status.toUpperCase());
    }

    // Get recent security events (latest 20)
    @GetMapping("/events")
    public List<SecurityEvent> getRecentEvents() {
        return eventRepository.findTop20ByOrderByTimestampDesc();
    }

    // Get events by severity
    @GetMapping("/events/severity/{severity}")
    public List<SecurityEvent> getEventsBySeverity(@PathVariable String severity) {
        return eventRepository.findBySeverity(severity.toUpperCase());
    }

    // Get overall security score
    @GetMapping("/security-score")
    public Map<String, Object> getSecurityScore() {
        return securityService.getSecurityScore();
    }

    // Get dashboard summary stats
    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        return securityService.getDashboardStats();
    }
}