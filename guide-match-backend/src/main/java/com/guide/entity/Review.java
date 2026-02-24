package com.guide.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("reviews")
public class Review {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    @TableField("order_id")
    private Integer orderId;
    
    @TableField("tourist_id")
    private Integer touristId;
    
    @TableField("guide_id")
    private Integer guideId;
    
    private Integer rating; // 1-5
    
    private String comment;
    
    @TableField("create_time")
    private LocalDateTime createTime;
}
