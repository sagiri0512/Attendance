package com.sagiri.controller;

import com.sagiri.vo.Result;
import com.sagiri.dto.UserLogin;
import com.sagiri.service.EmployeeService;
import com.sagiri.service.TokenListService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final EmployeeService employeeService;

    public AuthController(EmployeeService employeeService, TokenListService tokenListService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/login")
    public ResponseEntity<Result<?>> login(@RequestBody UserLogin userLogin){
        Result<?> result = employeeService.login(userLogin.getEmpNo(), userLogin.getPassword());
        return ResponseEntity.status(result.getCode()).body(result);
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Result<?>> logout(HttpServletRequest req){
        String header = req.getHeader("Authorization");
        Result<?> result = employeeService.logout(header);
        return ResponseEntity.status(result.getCode()).body(result);
    }

    @GetMapping("/current")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Result<?>> current(HttpServletRequest req){
        String header = req.getHeader("Authorization");
        Result<?> result = employeeService.getUserInfoByJWT(header);
        return ResponseEntity.status(result.getCode()).body(result);
    }
}
