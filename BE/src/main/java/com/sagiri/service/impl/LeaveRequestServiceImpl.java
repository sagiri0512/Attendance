package com.sagiri.service.impl;

import com.sagiri.mapper.LeaveRequestMapper;
import com.sagiri.service.LeaveRequestService;
import com.sagiri.utils.JwtUtil;
import com.sagiri.vo.Result;
import org.springframework.stereotype.Service;

@Service
public class LeaveRequestServiceImpl implements LeaveRequestService {
    private final LeaveRequestMapper leaveRequestMapper;

    public LeaveRequestServiceImpl(LeaveRequestMapper leaveRequestMapper) {
        this.leaveRequestMapper = leaveRequestMapper;
    }

    @Override
    public Result<?> apply(String header) {
        String token = header.substring(7);
        Long id = Long.valueOf(JwtUtil.getUserId(token));
        return null;
    }
}
