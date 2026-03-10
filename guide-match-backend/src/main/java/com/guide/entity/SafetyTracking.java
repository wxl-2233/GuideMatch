package com.guide.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SafetyTracking {
    private Long id;
    private Long userId;
    private Double latitude;
    private Double longitude;
    private String locationName;
    private LocalDateTime timestamp;
    private String tripId;
    private Boolean isEmergency;
    private String emergencyType;
    private String emergencyStatus; // PENDING, PROCESSING, RESOLVED
    private LocalDateTime emergencyTime;
    private String sharedWith; // JSON array of user IDs who can access this route
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
