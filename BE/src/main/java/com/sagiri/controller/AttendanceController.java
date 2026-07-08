package com.sagiri.controller;

import com.sagiri.common.Result;
import com.sagiri.service.AttendanceRecordService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {
    private final AttendanceRecordService attendanceRecordService;

    public AttendanceController(AttendanceRecordService attendanceRecordService) {
        this.attendanceRecordService = attendanceRecordService;
    }

    @PostMapping("/clock")
    public ResponseEntity<Result<?>> clock(HttpServletRequest req){
        String header = req.getHeader("Authorization");
        Result<?> result = attendanceRecordService.clock(header);
        return ResponseEntity.status(result.getCode()).body(result);
    }
}
