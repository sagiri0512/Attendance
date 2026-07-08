package com.sagiri.service;

import com.sagiri.common.Result;

public interface AttendanceRecordService {
    public Result<?> clock(String header);
}
