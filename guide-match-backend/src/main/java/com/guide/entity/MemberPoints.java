package com.guide.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MemberPoints {
    private Long id;
    private Long userId;
    private Integer points; // 当前积分
    private Integer totalPoints; // 历史总积分
    private String memberLevel; // NORMAL, V1_GOLD, V2_DIAMOND, V3_BLACK_DIAMOND
    private LocalDateTime levelUpdatedAt;
    private LocalDateTime lastActiveTime;
    private Boolean hasGoodReputation; // 信誉记录是否良好
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
