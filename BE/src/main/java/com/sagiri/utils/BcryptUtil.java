package com.sagiri.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BcryptUtil {
    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    /**
     * 加密明文密码
     * @param rawPassword 明文
     * @return 60 位密文，  $2a$10$...
     */
    public static String encode(String rawPassword) {
        return ENCODER.encode(rawPassword);
    }

    /**
     * 校验明文与密文是否匹配
     * @param rawPassword  用户输入的明文
     * @param encodedHash  数据库存的密文
     * @return true = 匹配
     */
    public static boolean matches(String rawPassword, String encodedHash) {
        return ENCODER.matches(rawPassword, encodedHash);
    }
}
