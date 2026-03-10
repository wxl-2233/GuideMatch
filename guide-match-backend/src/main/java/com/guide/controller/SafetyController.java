package com.guide.controller;

import com.guide.entity.SafetyTracking;
import com.guide.service.SafetyService;
import com.guide.util.JwtUtil;
import com.guide.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/safety")
@CrossOrigin
public class SafetyController {

    @Autowired
    private SafetyService safetyService;

    // 记录位置轨迹
    @PostMapping("/tracking")
    public Result<String> recordLocation(@RequestBody SafetyTracking tracking, HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            Long userId = JwtUtil.getUserId(token);
            tracking.setUserId(userId);
            tracking.setTimestamp(java.time.LocalDateTime.now());
            
            safetyService.recordLocation(tracking);
            return Result.success("位置记录成功");
        } catch (Exception e) {
            return Result.error("位置记录失败: " + e.getMessage());
        }
    }

    // 获取用户轨迹
    @GetMapping("/tracking/{tripId}")
    public Result<List<SafetyTracking>> getTracking(@PathVariable String tripId, HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            Long userId = JwtUtil.getUserId(token);
            List<SafetyTracking> tracking = safetyService.getUserTracking(userId, tripId);
            return Result.success(tracking);
        } catch (Exception e) {
            return Result.error("获取轨迹失败: " + e.getMessage());
        }
    }

    // 一键报警
    @PostMapping("/emergency")
    public Result<String> emergencyAlert(@RequestBody Map<String, Object> emergencyData, HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            Long userId = JwtUtil.getUserId(token);
            String emergencyType = (String) emergencyData.get("emergencyType");
            Double latitude = (Double) emergencyData.get("latitude");
            Double longitude = (Double) emergencyData.get("longitude");
            
            safetyService.createEmergencyAlert(userId, emergencyType, latitude, longitude);
            return Result.success("紧急求助已发送，平台客服将尽快联系您");
        } catch (Exception e) {
            return Result.error("紧急求助发送失败: " + e.getMessage());
        }
    }

    // 分享行程路线
    @PostMapping("/share-route")
    public Result<String> shareRoute(@RequestBody Map<String, Object> shareData, HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            Long userId = JwtUtil.getUserId(token);
            String tripId = (String) shareData.get("tripId");
            @SuppressWarnings("unchecked")
            List<String> sharedUsers = (List<String>) shareData.get("sharedUsers");
            
            safetyService.shareRoute(userId, tripId, sharedUsers);
            return Result.success("行程分享成功");
        } catch (Exception e) {
            return Result.error("行程分享失败: " + e.getMessage());
        }
    }

    // 获取紧急求助状态
    @GetMapping("/emergency-status/{userId}")
    public Result<List<SafetyTracking>> getEmergencyStatus(@PathVariable Long userId) {
        try {
            List<SafetyTracking> emergencies = safetyService.getEmergencyAlerts(userId);
            return Result.success(emergencies);
        } catch (Exception e) {
            return Result.error("获取紧急求助状态失败: " + e.getMessage());
        }
    }
}
