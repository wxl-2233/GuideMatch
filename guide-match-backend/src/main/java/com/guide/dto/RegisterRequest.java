package com.guide.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String nickname;
    private String email;
    private String password;
    private String role; // tourist, guide, admin
    private String fullName;
    private String phonenumber;
}
