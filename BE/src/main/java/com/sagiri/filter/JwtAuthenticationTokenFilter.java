package com.sagiri.filter;

import com.alibaba.fastjson2.JSON;
import com.sagiri.service.TokenListService;
import com.sagiri.utils.JwtUtil;
import com.sagiri.utils.WebUtil;
import com.sagiri.vo.Result;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT 认证令牌过滤器
 *
 * <p>每个请求只执行一次（继承 OncePerRequestFilter），在 Security 认证链中
 * 位于 UsernamePasswordAuthenticationFilter 之前。
 *
 * <p>认证流程：
 * <ol>
 *   <li>从 Authorization 头提取 Bearer Token</li>
 *   <li>校验 Token 有效性（JWT合法性 + Redis白名单）</li>
 *   <li>解析 Token 中的 userId 和 role</li>
 *   <li>构造认证对象存入 SecurityContextHolder</li>
 * </ol></p>
 *
 * @author sagiri
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private final TokenListService tokenListService;

    /**
     * 构造器注入令牌白名单服务
     *
     * @param tokenListService 令牌白名单服务
     */
    public JwtAuthenticationTokenFilter(TokenListService tokenListService) {
        this.tokenListService = tokenListService;
    }

    /**
     * 执行JWT认证过滤
     *
     * @param request     HTTP请求
     * @param response    HTTP响应
     * @param filterChain 过滤器链
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {
        // 1. 获取 Authorization 请求头
        String header = request.getHeader("Authorization");

        // 2. 无Token直接放行（由后续 Security 拦截处理）
        if (!StringUtils.hasText(header)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. 校验 Bearer 格式
        if (!header.startsWith("Bearer ")) {
            String json = JSON.toJSONString(Result.error(401, "Token无效或已过期，即将返回登陆！"));
            WebUtil.renderJson(response, 401, json);
            return;
        }
        String token = header.substring(7);

        // 4. 校验 Token 有效性（JWT未过期 + Redis白名单存在）
        if (!JwtUtil.validate(token) || !tokenListService.isTokenList(token)) {
            String json = JSON.toJSONString(Result.error(401, "Token无效或已过期，即将返回登陆！"));
            WebUtil.renderJson(response, 401, json);
            return;
        }

        // 5. 解析 Token，提取用户信息
        String userId = JwtUtil.getUserId(token);
        String role = JwtUtil.getRole(token);

        // 6. 将认证信息存入 SecurityContextHolder
        List<SimpleGrantedAuthority> roles = List.of(new SimpleGrantedAuthority(role));
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userId, null, roles);

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        // 7. 放行，进入后续过滤器
        filterChain.doFilter(request, response);
    }
}
