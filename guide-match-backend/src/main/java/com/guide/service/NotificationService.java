package com.guide.service;

import com.guide.entity.Notification;
import com.guide.mapper.NotificationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    /**
     * 创建通知
     */
    public void createNotification(Integer userId, String type, String title, String content, Integer relatedId) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setRelatedId(relatedId);
        notification.setRead(false);
        notification.setCreateTime(LocalDateTime.now());
        notification.setUpdateTime(LocalDateTime.now());
        notificationMapper.insert(notification);
    }

    /**
     * 发送向导审核通知给管理员
     */
    public void notifyAdminForGuideVerification(Integer guideId, String guideName) {
        // 查找所有管理员
        // 这里简化处理，实际应该查询所有管理员用户
        // 暂时先发送给第一个管理员（ID=1通常是管理员）
        createNotification(1, "system", "向导审核通知", 
            String.format("向导 %s 提交了审核申请，请及时处理", guideName), guideId);
    }

    /**
     * 发送向导审核结果通知给向导
     */
    public void notifyGuideForVerificationResult(Integer guideUserId, boolean approved, String reason) {
        String title = approved ? "审核通过" : "审核未通过";
        String content = approved ? 
            "恭喜！您的向导资料审核已通过，现在可以正常接单了。" :
            String.format("很抱歉，您的向导资料审核未通过。原因：%s", reason != null ? reason : "未提供原因");
        
        createNotification(guideUserId, "system", title, content, null);
    }

    /**
     * 发送帖子审核通知给管理员
     */
    public void notifyAdminForPostVerification(Integer postId, String authorName) {
        // 发送给管理员
        createNotification(1, "system", "帖子审核通知", 
            String.format("用户 %s 发布了新帖子，请及时审核", authorName), postId);
    }

    /**
     * 发送帖子审核结果通知给用户
     */
    public void notifyUserForPostVerificationResult(Integer userId, boolean approved, String reason) {
        String title = approved ? "帖子审核通过" : "帖子审核未通过";
        String content = approved ? 
            "恭喜！您发布的帖子已通过审核，现已公开显示。" :
            String.format("很抱歉，您发布的帖子未通过审核。原因：%s", reason != null ? reason : "未提供原因");
        
        createNotification(userId, "system", title, content, null);
    }

    /**
     * 创建通用系统通知（便于其它模块复用）
     */
    public void createSystemNotification(Integer userId, String title, String content) {
        createNotification(userId, "system", title, content, null);
    }
}

