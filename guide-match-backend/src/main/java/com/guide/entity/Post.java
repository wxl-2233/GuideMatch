package com.guide.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("posts")
public class Post {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    @TableField("user_id")
    private Integer userId;
    
    @TableField("user_role")
    private String userRole; // tourist, guide
    
    private String title;
    private String content;
    private String images; // JSON格式
    private String tags; // JSON格式
    
    @TableField("likes_count")
    private Integer likesCount;
    
    @TableField("views_count")
    private Integer viewsCount;
    
    private String status; // pending, approved, rejected, deleted
    
    @TableField("rejection_reason")
    private String rejectionReason;
    
    @TableField("exp_awarded")
    private Integer expAwarded;
    
    @TableField("create_time")
    private LocalDateTime createTime;
    
    @TableField("update_time")
    private LocalDateTime updateTime;
}
