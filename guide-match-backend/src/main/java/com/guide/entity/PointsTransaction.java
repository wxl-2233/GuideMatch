package com.guide.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PointsTransaction {
    private Long id;
    private Long userId;
    private Integer points; // 积分变化（正数为获得，负数为消费）
    private String transactionType; // CONSUMPTION, REVIEW, COMMUNITY_POST, SOCIAL_SHARE, LEVEL_UPGRADE
    private String description;
    private String relatedOrderId; // 关联订单ID（如果是消费获得）
    private String relatedGuideId; // 关联导游ID（如果是评价获得）
    private LocalDateTime createdAt;
}
