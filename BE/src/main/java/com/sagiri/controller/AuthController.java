package com.sagiri.controller;

import com.sagiri.vo.Result;
import com.sagiri.dto.UserLogin;
import com.sagiri.service.EmployeeService;
import com.sagiri.service.TokenListService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 *
 * <p>提供登录、登出、获取当前用户信息等认证相关接口。
 * 登录接口无需认证，登出和获取当前用户需要已认证。</p>
 *
 * @author sagiri
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final EmployeeService employeeService;

    /**
     * 构造器注入员工服务
     *
     * @param employeeService  员工业务服务
     * @param tokenListService 令牌管理服务（预留）
     */
    public AuthController(EmployeeService employeeService, TokenListService tokenListService) {
        this.employeeService = employeeService;
    }

    /**
     * 用户登录
     *
     * <p>根据工号和密码进行登录验证，成功后返回JWT令牌。</p>
     *
     * @param userLogin 登录请求体（含empNo和password）
     * @return 登录结果，成功时data包含JWT令牌和用户信息
     */
    @PostMapping("/login")
    public ResponseEntity<Result<?>> login(@RequestBody UserLogin userLogin) {
        Result<?> result = employeeService.login(userLogin.getEmpNo(), userLogin.getPassword());
        return ResponseEntity.status(result.getCode()).body(result);
    }

    /**
     * 用户登出
     *
     * <p>从Redis令牌白名单中移除当前JWT，使令牌失效。</p>
     *
     * @param req HTTP请求（用于提取Authorization头）
     * @return 登出结果
     */
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Result<?>> logout(HttpServletRequest req) {
        String header = req.getHeader("Authorization");
        Result<?> result = employeeService.logout(header);
        return ResponseEntity.status(result.getCode()).body(result);
    }

    /**
     * 获取当前登录用户信息
     *
     * <p>从JWT令牌中解析用户身份，返回当前用户的基本信息。</p>
     *
     * @param req HTTP请求（用于提取Authorization头）
     * @return 当前用户信息（用户名、工号、角色、所属PM/PL）
     */
    @GetMapping("/current")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Result<?>> current(HttpServletRequest req) {
        String header = req.getHeader("Authorization");
        Result<?> result = employeeService.getUserInfoByJWT(header);
        return ResponseEntity.status(result.getCode()).body(result);
    }
}
