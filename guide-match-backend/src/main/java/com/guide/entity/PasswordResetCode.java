package com.guide.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("password_reset_codes")
public class PasswordResetCode {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private String email;
    private String code;
    
    @TableField("expire_time")
    private LocalDateTime expireTime;
    
    private Boolean used;
    
    @TableField("create_time")
    private LocalDateTime createTime;
}

