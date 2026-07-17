package com.sagiri.service.impl;

import com.sagiri.vo.Result;
import com.sagiri.mapper.AttendanceRecordMapper;
import com.sagiri.service.AttendanceRecordService;
import com.sagiri.utils.JwtUtil;
import org.springframework.stereotype.Service;

@Service
public class AttendanceRecordServiceImpl implements AttendanceRecordService {
    private final AttendanceRecordMapper attendanceRecordMapper;

    public AttendanceRecordServiceImpl(AttendanceRecordMapper attendanceRecordMapper) {
        this.attendanceRecordMapper = attendanceRecordMapper;
    }

    @Override
    public Result<?> clock(String header) {
        String token = header.substring(7);
        Long empId = Long.valueOf(JwtUtil.getUserId(token));
        // 第1次打卡 → 上班
        Long id = attendanceRecordMapper.findTodayRecordId(empId);
        if(id == null){
            attendanceRecordMapper.insert(empId);
            return Result.success("打卡成功！");
        }
        // 第N次打卡 → 更新下班时间
        attendanceRecordMapper.updateClockOut(empId);
        return Result.success("打卡成功！");
    }
}
