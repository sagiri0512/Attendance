package com.sagiri.controller;

import com.sagiri.vo.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/leave")
public class LeaveController {

    //提交请假申请
    @PostMapping("/apply")
    @PreAuthorize("hasAnyAuthority('0','1','2','3')")
    public ResponseEntity<Result<?>> apply(HttpServletRequest req){
        String header = req.getHeader("Authorization");
        return  null;
    }
}
