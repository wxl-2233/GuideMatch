package com.guide.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("post_likes")
public class PostLike {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    @TableField("post_id")
    private Integer postId;
    
    @TableField("user_id")
    private Integer userId;
    
    @TableField("create_time")
    private LocalDateTime createTime;
}

