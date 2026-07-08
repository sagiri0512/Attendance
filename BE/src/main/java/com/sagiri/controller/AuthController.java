package com.sagiri.controller;

import com.sagiri.common.Result;
import com.sagiri.dto.UserLogin;
import com.sagiri.service.EmployeeService;
import com.sagiri.service.TokenListService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<Result<?>> logout(HttpServletRequest req){
        String header = req.getHeader("Authorization");
        Result<?> result = employeeService.logout(header);
        return ResponseEntity.status(result.getCode()).body(result);
    }
}
