package com.guide.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret:GuideMatchSecretKeyForJWTTokenGeneration2024ThisIsAVeryLongSecretKeyThatMustBeAtLeast64CharactersLongForHS512Algorithm}")
    private String secret;

    @Value("${jwt.expiration:86400000}") // 24小时
    private Long expiration;

    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        // 如果密钥长度不足，使用 SHA-256 哈希来生成固定长度的密钥
        if (keyBytes.length < 64) {
            try {
                java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(keyBytes);
                // 对于 HS512，需要 64 字节，重复哈希直到达到所需长度
                byte[] extendedKey = new byte[64];
                System.arraycopy(hash, 0, extendedKey, 0, 32);
                // 再次哈希并填充剩余部分
                byte[] secondHash = digest.digest(hash);
                System.arraycopy(secondHash, 0, extendedKey, 32, 32);
                keyBytes = extendedKey;
            } catch (java.security.NoSuchAlgorithmException e) {
                // 如果 SHA-256 不可用，使用简单填充
                byte[] extendedKey = new byte[64];
                System.arraycopy(keyBytes, 0, extendedKey, 0, Math.min(keyBytes.length, 64));
                for (int i = keyBytes.length; i < 64; i++) {
                    extendedKey[i] = (byte) (keyBytes[i % keyBytes.length] ^ (i % 256));
                }
                keyBytes = extendedKey;
            }
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成JWT Token
     */
    public String generateToken(Integer userId, String role, String nickname) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role);
        claims.put("nickname", nickname);
        return createToken(claims);
    }

    /**
     * 创建Token
     */
    private String createToken(Map<String, Object> claims) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 从Token中获取Claims
     */
    public Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从Token中获取用户ID
     */
    public Integer getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null ? (Integer) claims.get("userId") : null;
    }

    /**
     * 从Token中获取角色
     */
    public String getRoleFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null ? (String) claims.get("role") : null;
    }

    /**
     * 验证Token是否有效
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims != null && !isTokenExpired(claims);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查Token是否过期
     */
    private boolean isTokenExpired(Claims claims) {
        Date expiration = claims.getExpiration();
        return expiration.before(new Date());
    }
}
