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

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    private final TokenListService tokenListService;

    public JwtAuthenticationTokenFilter(TokenListService tokenListService) {
        this.tokenListService = tokenListService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //获取token
        String header = request.getHeader("Authorization");

        if (!StringUtils.hasText(header)) {
            filterChain.doFilter(request, response);
            return;
        }
        //判断token是否有效
        if(!header.startsWith("Bearer ")){
            String json = JSON.toJSONString(Result.error(401, "Token无效或已过期，即将返回登陆！"));
            WebUtil.renderJson(response, 401, json);
            return;
        }
        String token = header.substring(7);

        //token是否在有效期内和token是否登出
        if (!JwtUtil.validate(token) || !tokenListService.isTokenList(token)) {
            String json = JSON.toJSONString(Result.error(401, "Token无效或已过期，即将返回登陆！"));
            WebUtil.renderJson(response, 401, json);
            return;
        }

        //解析token
        String userId = JwtUtil.getUserId(token);
        String role = JwtUtil.getRole(token);

        //存入SecurityContextHolder
        List<SimpleGrantedAuthority> roles = List.of(new SimpleGrantedAuthority(role));
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userId, null, roles);

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        //放行
        filterChain.doFilter(request, response);
    }
}
