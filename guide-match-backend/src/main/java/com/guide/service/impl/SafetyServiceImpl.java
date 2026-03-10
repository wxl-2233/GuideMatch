package com.guide.service.impl;

import com.guide.entity.SafetyTracking;
import com.guide.service.SafetyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SafetyServiceImpl implements SafetyService {

    // 模拟数据库存储，实际项目中应该使用数据库
    private static final List<SafetyTracking> trackingDatabase = new ArrayList<>();
    private static final List<SafetyTracking> emergencyDatabase = new ArrayList<>();

    @Override
    public void recordLocation(SafetyTracking tracking) {
        tracking.setCreatedAt(LocalDateTime.now());
        tracking.setUpdatedAt(LocalDateTime.now());
        trackingDatabase.add(tracking);
        
        // 这里可以添加推送通知给被分享的用户
        System.out.println("位置记录成功: 用户ID " + tracking.getUserId() + 
                          ", 经度: " + tracking.getLongitude() + 
                          ", 纬度: " + tracking.getLatitude());
    }

    @Override
    public List<SafetyTracking> getUserTracking(Long userId, String tripId) {
        return trackingDatabase.stream()
                .filter(t -> t.getUserId().equals(userId) && 
                              (tripId == null || tripId.equals(t.getTripId())))
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .toList();
    }

    @Override
    public void createEmergencyAlert(Long userId, String emergencyType, Double latitude, Double longitude) {
        SafetyTracking emergency = new SafetyTracking();
        emergency.setUserId(userId);
        emergency.setEmergencyType(emergencyType);
        emergency.setLatitude(latitude);
        emergency.setLongitude(longitude);
        emergency.setEmergencyStatus("PENDING");
        emergency.setEmergencyTime(LocalDateTime.now());
        emergency.setCreatedAt(LocalDateTime.now());
        emergency.setUpdatedAt(LocalDateTime.now());
        
        emergencyDatabase.add(emergency);
        
        // 这里应该触发紧急通知给客服和相关人员
        System.out.println("紧急求助触发: 用户ID " + userId + 
                          ", 类型: " + emergencyType + 
                          ", 位置: " + latitude + ", " + longitude);
    }

    @Override
    public void shareRoute(Long userId, String tripId, List<String> sharedUsers) {
        // 更新轨迹记录的分享权限
        trackingDatabase.stream()
                .filter(t -> t.getUserId().equals(userId) && tripId.equals(t.getTripId()))
                .forEach(t -> {
                    t.setSharedWith(String.join(",", sharedUsers));
                    t.setUpdatedAt(LocalDateTime.now());
                });
        
        System.out.println("行程分享成功: 用户ID " + userId + 
                          ", 行程ID " + tripId + 
                          ", 分享给 " + sharedUsers);
    }

    @Override
    public List<SafetyTracking> getEmergencyAlerts(Long userId) {
        return emergencyDatabase.stream()
                .filter(e -> e.getUserId().equals(userId))
                .sorted((a, b) -> b.getEmergencyTime().compareTo(a.getEmergencyTime()))
                .toList();
    }
}
