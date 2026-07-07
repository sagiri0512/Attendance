package com.sagiri.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类（JJWT 0.12.x）
 */
public class JwtUtil {

    private static final String SECRET = "YourSecretKeyAtLeast256BitsLongForHS256!";
    private static final long EXPIRE = 24 * 60 * 60 * 1000L; // 24 小时

    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    /**
     * 生成 Token
     */
    public static String generateToken(String userId, String role, String realName) {
        return Jwts.builder()
                .subject(userId)
                .claim("role", role)
                .claim("realName", realName)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRE))
                .signWith(KEY)
                .compact();
    }

    /**
     * 从 Token 中解析 Claims
     */
    public static Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 获取用户 ID
     */
    public static String getUserId(String token) {
        return parseToken(token).getSubject();
    }

    /**
     * 获取角色
     */
    public static String getRole(String token) {
        return parseToken(token).get("role", String.class);
    }

    /**
     * 获取姓名
     */
    public static String getRealName(String token) {
        return parseToken(token).get("realName", String.class);
    }

    /**
     * 获取过期时间戳（毫秒），用于 Redis 黑名单 TTL
     */
    public static long getExpireTime(String token) {
        return parseToken(token).getExpiration().getTime();
    }

    /**
     * 校验 Token 是否有效
     */
    public static boolean validate(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
