package com.sagiri.service;

import com.sagiri.vo.Result;

public interface AttendanceRecordService {
    public Result<?> clock(String header);
}
