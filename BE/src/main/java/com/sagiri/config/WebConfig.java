package com.sagiri.config;

import com.sagiri.service.TokenListService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final TokenListService tokenListService;

    public WebConfig(TokenListService tokenListService) {
        this.tokenListService = tokenListService;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor(tokenListService))
                .addPathPatterns("/**")                       // 拦截所有
                .excludePathPatterns("/auth/login", "/auth/register"); // 放行登录注册
    }
}
