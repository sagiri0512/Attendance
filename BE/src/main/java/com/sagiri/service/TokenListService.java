package com.sagiri.service;

/**
 * JWT令牌白名单服务接口
 *
 * <p>基于Redis管理登录令牌白名单，实现登出后令牌即时失效。
 * 令牌以 tokenList: 前缀存入Redis，TTL与JWT过期时间对齐。</p>
 *
 * @author sagiri
 */
public interface TokenListService {

    /**
     * 将令牌加入白名单
     *
     * @param token JWT令牌字符串
     */
    void setToken(String token);

    /**
     * 检查令牌是否在白名单中
     *
     * @param token JWT令牌字符串
     * @return true-有效 false-已失效
     */
    Boolean isTokenList(String token);

    /**
     * 从白名单中移除令牌（登出时调用）
     *
     * @param token JWT令牌字符串
     */
    void removeToken(String token);
}
