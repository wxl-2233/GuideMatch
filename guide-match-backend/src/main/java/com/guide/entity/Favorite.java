package com.guide.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("favorites")
public class Favorite {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    @TableField("tourist_id")
    private Integer touristId;
    
    @TableField("guide_id")
    private Integer guideId;
    
    @TableField("create_time")
    private LocalDateTime createTime;
}
