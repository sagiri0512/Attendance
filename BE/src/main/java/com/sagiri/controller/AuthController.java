package com.sagiri.controller;

import com.sagiri.common.Result;
import com.sagiri.dto.UserLogin;
import com.sagiri.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final EmployeeService employeeService;

    public AuthController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/login")
    public ResponseEntity<Result<?>> login(@RequestBody UserLogin userLogin){
        Result<?> result = employeeService.login(userLogin.getEmpNo(), userLogin.getPassword());
        return ResponseEntity.status(result.getCode()).body(result);
    }
}
