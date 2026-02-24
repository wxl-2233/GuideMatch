package com.guide.controller;

import com.guide.dto.AuthResponse;
import com.guide.dto.LoginRequest;
import com.guide.dto.RegisterRequest;
import com.guide.entity.PasswordResetCode;
import com.guide.entity.User;
import com.guide.mapper.PasswordResetCodeMapper;
import com.guide.mapper.UserMapper;
import com.guide.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordResetCodeMapper passwordResetCodeMapper;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final Random random = new Random();

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        User user = userMapper.findByEmail(request.getEmail());
        if (user == null) {
            user = userMapper.findByNickname(request.getEmail());
        }

        if (user == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("用户不存在"));
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body(createErrorResponse("密码错误"));
        }

        if (!"active".equals(user.getStatus())) {
            return ResponseEntity.badRequest().body(createErrorResponse("账号已被冻结"));
        }

        // 生成JWT Token
        String token = jwtUtil.generateToken(user.getId(), user.getRole(), user.getNickname());

        // 返回响应
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setRole(user.getRole());
        response.setNickname(user.getNickname());
        response.setAvatarPath(user.getAvatarPath());
        response.setAvatarStatus(user.getAvatarStatus());
        response.setLv(user.getLv());
        response.setExp(user.getExp());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userMapper.findByEmail(request.getEmail()) != null) {
            return ResponseEntity.badRequest().body(createErrorResponse("邮箱已被注册"));
        }

        if (userMapper.findByNickname(request.getNickname()) != null) {
            return ResponseEntity.badRequest().body(createErrorResponse("昵称已被使用"));
        }
        if (!"tourist".equals(request.getRole()) && !"guide".equals(request.getRole())) {
            return ResponseEntity.badRequest().body(createErrorResponse("无效的用户角色"));
        }

        User user = new User();
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setStatus("active");
        user.setFullName(request.getFullName());
        user.setPhonenumber(request.getPhonenumber());
        user.setLv(1);
        user.setExp(0);
        user.setCreateTime(LocalDateTime.now());

        userMapper.insert(user);

        // 生成JWT Token
        String token = jwtUtil.generateToken(user.getId(), user.getRole(), user.getNickname());

        // 返回响应
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setRole(user.getRole());
        response.setNickname(user.getNickname());
        response.setAvatarPath(user.getAvatarPath());
        response.setAvatarStatus(user.getAvatarStatus());
        response.setLv(user.getLv());
        response.setExp(user.getExp());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestAttribute Integer userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("用户不存在"));
        }

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("userId", user.getId());
        userInfo.put("role", user.getRole());
        userInfo.put("nickname", user.getNickname());
        userInfo.put("avatarPath", user.getAvatarPath());
        userInfo.put("avatarStatus", user.getAvatarStatus());
        userInfo.put("lv", user.getLv());
        userInfo.put("exp", user.getExp());
        userInfo.put("email", user.getEmail());
        userInfo.put("fullName", user.getFullName());
        userInfo.put("phonenumber", user.getPhonenumber());
        userInfo.put("createTime", user.getCreateTime());

        return ResponseEntity.ok(userInfo);
    }

    @PostMapping("/update-avatar")
    public ResponseEntity<?> updateAvatar(
            @RequestAttribute Integer userId,
            @RequestBody Map<String, String> request) {
        String avatarPath = request.get("avatarPath");
        if (avatarPath == null || avatarPath.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(createErrorResponse("头像路径不能为空"));
        }

        User user = userMapper.findById(userId);
        if (user == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("用户不存在"));
        }

        user.setAvatarPath(avatarPath);
        user.setAvatarStatus("pending");
        userMapper.updateById(user);

        AuthResponse response = new AuthResponse();
        response.setUserId(user.getId());
        response.setRole(user.getRole());
        response.setNickname(user.getNickname());
        response.setAvatarPath(user.getAvatarPath());
        response.setAvatarStatus(user.getAvatarStatus());
        response.setLv(user.getLv());
        response.setExp(user.getExp());
        response.setEmail(user.getEmail());
        response.setCreateTime(user.getCreateTime());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/update-profile")
    public ResponseEntity<?> updateProfile(
            @RequestAttribute Integer userId,
            @RequestBody Map<String, String> request) {
        User user = userMapper.findById(userId);
        if (user == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("用户不存在"));
        }

        if (request.containsKey("fullName")) {
            user.setFullName(request.get("fullName"));
        }
        if (request.containsKey("phonenumber")) {
            user.setPhonenumber(request.get("phonenumber"));
        }

        userMapper.updateById(user);

        AuthResponse response = new AuthResponse();
        response.setUserId(user.getId());
        response.setRole(user.getRole());
        response.setNickname(user.getNickname());
        response.setAvatarPath(user.getAvatarPath());
        response.setAvatarStatus(user.getAvatarStatus());
        response.setLv(user.getLv());
        response.setExp(user.getExp());
        response.setEmail(user.getEmail());
        response.setCreateTime(user.getCreateTime());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String nickname = request.get("nickname");
        String newPassword = request.get("newPassword");

        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(createErrorResponse("邮箱不能为空"));
        }
        if (nickname == null || nickname.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(createErrorResponse("用户名不能为空"));
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(createErrorResponse("新密码不能为空"));
        }
        if (newPassword.length() < 6) {
            return ResponseEntity.badRequest().body(createErrorResponse("密码长度至少6位"));
        }

        User user = userMapper.findByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("邮箱或用户名不正确"));
        }

        if (!nickname.equals(user.getNickname())) {
            return ResponseEntity.badRequest().body(createErrorResponse("邮箱或用户名不正确"));
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);

        return ResponseEntity.ok(createSuccessResponse("密码重置成功"));
    }

    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }

    private Map<String, String> createSuccessResponse(String message) {
        Map<String, String> success = new HashMap<>();
        success.put("message", message);
        return success;
    }
}
