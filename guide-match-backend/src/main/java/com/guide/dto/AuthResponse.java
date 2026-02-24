package com.guide.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AuthResponse {
    private String token;
    private Integer userId;
    private String role;
    private String nickname;
    private String avatarPath;
    private String avatarStatus;
    private Integer lv;
    private Integer exp;
    private String email;
    private LocalDateTime createTime;
}
