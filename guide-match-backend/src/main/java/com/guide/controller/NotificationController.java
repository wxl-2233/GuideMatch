package com.guide.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.guide.entity.Notification;
import com.guide.mapper.NotificationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationMapper notificationMapper;

    /**
     * 获取通知列表
     */
    @GetMapping("/list")
    public ResponseEntity<?> getNotificationList(
            @RequestAttribute(required = false) Integer userId) {

        if (userId == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("list", new java.util.ArrayList<>());
            result.put("total", 0);
            return ResponseEntity.ok(result);
        }

        QueryWrapper<Notification> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.orderByDesc("create_time");
        queryWrapper.last("OFFSET 0 ROWS FETCH NEXT 20 ROWS ONLY");

        List<Notification> notifications = notificationMapper.selectList(queryWrapper);

        Map<String, Object> result = new HashMap<>();
        result.put("list", notifications);
        QueryWrapper<Notification> countWrapper = new QueryWrapper<>();
        countWrapper.eq("user_id", userId);
        countWrapper.eq("[read]", false);
        result.put("total", notificationMapper.selectCount(countWrapper));

        return ResponseEntity.ok(result);
    }

    /**
     * 标记通知为已读
     */
    @PostMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(
            @RequestAttribute(required = false) Integer userId,
            @PathVariable Integer id) {

        if (userId == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("未登录"));
        }

        Notification notification = notificationMapper.selectById(id);
        if (notification == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("通知不存在"));
        }

        if (!notification.getUserId().equals(userId)) {
            return ResponseEntity.badRequest().body(createErrorResponse("无权操作此通知"));
        }

        notification.setRead(true);
        notification.setUpdateTime(LocalDateTime.now());
        notificationMapper.updateById(notification);

        return ResponseEntity.ok(createSuccessResponse("已标记为已读"));
    }

    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }

    private Map<String, String> createSuccessResponse(String message) {
        Map<String, String> success = new HashMap<>();
        success.put("message", message);
        return success;
    }
}
