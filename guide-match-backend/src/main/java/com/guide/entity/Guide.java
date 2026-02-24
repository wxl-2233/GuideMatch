package com.guide.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("guides")
public class Guide {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    @TableField("user_id")
    private Integer userId;
    
    private String languages; // JSON格式
    private String cities; // JSON格式
    private String tags; // JSON格式
    private String bio;
    
    @TableField("hourly_rate")
    private BigDecimal hourlyRate;
    
    @TableField("daily_rate")
    private BigDecimal dailyRate;
    
    private String certificates; // JSON格式
    
    @TableField("verification_status")
    private String verificationStatus;
    
    @TableField("rejection_reason")
    private String rejectionReason;
    
    private BigDecimal rating;
    
    @TableField("total_reviews")
    private Integer totalReviews;
    
    @TableField("total_orders")
    private Integer totalOrders;
    
    @TableField("total_income")
    private BigDecimal totalIncome;
    
    @TableField("create_time")
    private LocalDateTime createTime;
    
    @TableField("update_time")
    private LocalDateTime updateTime;
}
