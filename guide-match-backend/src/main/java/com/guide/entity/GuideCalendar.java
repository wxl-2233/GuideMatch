package com.guide.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("guide_calendar")
public class GuideCalendar {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    @TableField("guide_id")
    private Integer guideId;
    
    private LocalDate date;
    
    private String status; // available, booked, unavailable
    
    @TableField("create_time")
    private LocalDateTime createTime;
}
