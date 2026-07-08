package com.yash.watchtower.repository;

import com.yash.watchtower.model.SecurityEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SecurityEventRepository extends JpaRepository<SecurityEvent, Long> {
    List<SecurityEvent> findTop20ByOrderByTimestampDesc();
    List<SecurityEvent> findBySeverity(String severity);
}