package com.sagiri.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sagiri.common.Result;
import com.sagiri.service.TokenListService;
import com.sagiri.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoginInterceptor implements HandlerInterceptor {

    private final TokenListService tokenListService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public LoginInterceptor(TokenListService tokenListService) {
        this.tokenListService = tokenListService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        String header = request.getHeader("Authorization");

        if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
            send401(response, "未携带Token");
            return false;
        }

        String token = header.substring(7);

        if (!JwtUtil.validate(token)) {
            send401(response, "Token无效或已过期");
            return false;
        }

        if (!tokenListService.isTokenList(token)) {
            send401(response, "Token已失效，请重新登录");
            return false;
        }

        return true;
    }

    private void send401(HttpServletResponse response, String msg) throws Exception {
        response.setStatus(401);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(Result.error(401, msg)));
    }
}
