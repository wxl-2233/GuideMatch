package com.guide.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("users")
public class User {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String nickname;
    private String password;

    private String role;
    private String status;

    @TableField("full_name")
    private String fullName;
    
    private String email;
    
    private String phonenumber;
    
    @TableField("avatar_path")
    private String avatarPath;

    @TableField("avatar_status")
    private String avatarStatus; // pending, approved, rejected

    private Integer lv;
    private Integer exp;

    @TableField("create_time")
    private LocalDateTime createTime;
}
