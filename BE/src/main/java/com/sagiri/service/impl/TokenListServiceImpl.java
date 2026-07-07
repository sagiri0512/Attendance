package com.sagiri.service.impl;

import com.sagiri.service.TokenListService;
import com.sagiri.utils.JwtUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TokenListServiceImpl implements TokenListService {
    private final RedisTemplate<String, Object> redisTemplate;

    public TokenListServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void setToken(String token) {
        long remaining = JwtUtil.getExpireTime(token) - System.currentTimeMillis();
        if (remaining > 0) {
            redisTemplate.opsForValue().set("tokenList:" + token, "1", remaining, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public Boolean isTokenList(String token) {
        return redisTemplate.hasKey("tokenList:" + token);
    }
}
