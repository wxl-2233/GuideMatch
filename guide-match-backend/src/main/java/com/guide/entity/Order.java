package com.guide.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("orders")
public class Order {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    @TableField("tourist_id")
    private Integer touristId;
    
    @TableField("guide_id")
    private Integer guideId;
    
    @TableField("order_type")
    private String orderType; // fixed, custom
    
    @TableField("route_name")
    private String routeName;
    
    @TableField("route_id")
    private Integer routeId;
    
    @TableField("start_date")
    private LocalDate startDate;
    
    @TableField("end_date")
    private LocalDate endDate;
    
    private Integer participants;
    
    @TableField("special_requirements")
    private String specialRequirements;
    
    @TableField("total_price")
    private BigDecimal totalPrice;
    
    @TableField("commission_rate")
    private BigDecimal commissionRate;
    
    @TableField("commission_amount")
    private BigDecimal commissionAmount;
    
    @TableField("guide_income")
    private BigDecimal guideIncome;
    
    private String status; // pending, confirmed, in_progress, completed, cancelled
    
    @TableField("cancel_reason")
    private String cancelReason;
    
    @TableField("reject_reason")
    private String rejectReason;
    
    @TableField("create_time")
    private LocalDateTime createTime;
    
    @TableField("update_time")
    private LocalDateTime updateTime;
}
