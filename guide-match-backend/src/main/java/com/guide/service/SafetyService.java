package com.guide.service;

import com.guide.entity.SafetyTracking;
import java.util.List;
import java.util.Map;

public interface SafetyService {
    void recordLocation(SafetyTracking tracking);
    List<SafetyTracking> getUserTracking(Long userId, String tripId);
    void createEmergencyAlert(Long userId, String emergencyType, Double latitude, Double longitude);
    void shareRoute(Long userId, String tripId, List<String> sharedUsers);
    List<SafetyTracking> getEmergencyAlerts(Long userId);
}
