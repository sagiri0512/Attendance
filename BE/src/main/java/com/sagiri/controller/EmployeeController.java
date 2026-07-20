package com.sagiri.controller;

import com.sagiri.entity.Employee;
import com.sagiri.service.EmployeeService;
import com.sagiri.vo.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employee")
@PreAuthorize("hasAuthority('3')")
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/add")
    public ResponseEntity<Result<?>> add(@RequestBody Employee employee){
        Result<?> result = employeeService.addNewEmployee(employee);
        return ResponseEntity.status(result.getCode()).body(result);
    }

    @GetMapping("/pm-list")
    public ResponseEntity<Result<?>> getAllPM(){
        Result<?> result = employeeService.getAllPM();
        return ResponseEntity.status(result.getCode()).body(result);
    }

    @GetMapping("/pl-list")
    public ResponseEntity<Result<?>> getPLByPMId(@RequestParam("pmId") Long pmId){
        Result<?> result = employeeService.getPLByPMId(pmId);
        return ResponseEntity.status(result.getCode()).body(result);
    }
}
