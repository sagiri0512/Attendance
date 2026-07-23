package com.sagiri.service.impl;

import com.sagiri.service.TokenListService;
import com.sagiri.utils.JwtUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * JWT令牌白名单服务实现（基于Redis）
 *
 * <p>Redis Key格式：tokenList:{jwt字符串}
 * Value固定为"1"，TTL与JWT剩余有效时间对齐。
 * 登出时删除Key即可使令牌即时失效。</p>
 *
 * @author sagiri
 */
@Service
public class TokenListServiceImpl implements TokenListService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 构造器注入RedisTemplate
     *
     * @param redisTemplate Redis操作模板
     */
    public TokenListServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 将JWT令牌存入Redis白名单
     *
     * <p>TTL设置为JWT剩余有效时间，与令牌过期同步。</p>
     *
     * @param token JWT令牌字符串
     */
    @Override
    public void setToken(String token) {
        long remaining = JwtUtil.getExpireTime(token) - System.currentTimeMillis();
        if (remaining > 0) {
            redisTemplate.opsForValue().set("tokenList:" + token, "1", remaining, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * 检查令牌是否在白名单中（即是否有效）
     *
     * @param token JWT令牌字符串
     * @return true-令牌有效 false-已登出或过期
     */
    @Override
    public Boolean isTokenList(String token) {
        return redisTemplate.hasKey("tokenList:" + token);
    }

    /**
     * 登出：从Redis白名单中移除令牌
     *
     * @param token JWT令牌字符串
     */
    @Override
    public void removeToken(String token) {
        redisTemplate.delete("tokenList:" + token);
    }
}
