package com.sagiri.controller;

import com.sagiri.vo.Result;
import com.sagiri.service.AttendanceRecordService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/attendance")
@PreAuthorize("hasAnyAuthority('0','1','2','3')")
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

    @GetMapping("/my/{eid}")
    public ResponseEntity<Result<?>> getMyAttendanceRecords(@PathVariable Long eid, @RequestParam("start") Integer start, @RequestParam("size") Integer size){
        Result<?> result = attendanceRecordService.getMyAttendanceRecords(eid, start, size);
        return ResponseEntity.status(result.getCode()).body(result);
    }
}
