package com.sagiri.config;

import com.sagiri.filter.JwtAuthenticationTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 安全配置类
 *
 * <p>配置JWT无状态认证模式：
 * <ul>
 *   <li>关闭 CSRF、表单登录、HTTP Basic</li>
 *   <li>会话策略设为 STATELESS（不创建Session）</li>
 *   <li>所有请求（除 /auth/login、/auth/register）需要认证</li>
 *   <li>在 UsernamePasswordAuthenticationFilter 之前插入JWT过滤器</li>
 * </ul></p>
 *
 * @author sagiri
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    /**
     * 构造器注入JWT过滤器
     *
     * @param jwtAuthenticationTokenFilter JWT认证过滤器
     */
    public SecurityConfig(JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter) {
        this.jwtAuthenticationTokenFilter = jwtAuthenticationTokenFilter;
    }

    /**
     * 密码编码器（BCrypt）
     *
     * @return BCryptPasswordEncoder 实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 认证管理器
     *
     * @param config Spring Security 认证配置
     * @return AuthenticationManager 实例
     * @throws Exception 配置异常
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * 核心安全过滤链
     *
     * <p>配置请求拦截规则：关闭无状态会话相关的默认安全机制，
     * 将JWT过滤器插入认证链，实现基于令牌的无状态认证。</p>
     *
     * @param http HttpSecurity 配置对象
     * @return SecurityFilterChain 实例
     * @throws Exception 配置异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 关闭 CSRF 防护（前后端分离的 JWT 项目不需要 CSRF）
                .csrf(AbstractHttpConfigurer::disable)
                // 禁用默认的 /login 表单登录
                .formLogin(AbstractHttpConfigurer::disable)
                // 禁用 HTTP Basic 认证
                .httpBasic(AbstractHttpConfigurer::disable)
                // 不通过 Session 获取 SecurityContext（无状态）
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // 登录和注册接口允许匿名访问
                        .requestMatchers("/auth/login", "/auth/register").anonymous()
                        // 其余所有请求需要认证
                        .anyRequest().authenticated()
                )
                // JWT 过滤器插在 UsernamePasswordAuthenticationFilter 之前
                .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
