package com.sagiri.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sagiri.vo.AttendancePageVO;
import com.sagiri.vo.AttendanceVO;
import com.sagiri.vo.Result;
import com.sagiri.mapper.AttendanceRecordMapper;
import com.sagiri.service.AttendanceRecordService;
import com.sagiri.utils.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class AttendanceRecordServiceImpl implements AttendanceRecordService {
    private final AttendanceRecordMapper attendanceRecordMapper;

    public AttendanceRecordServiceImpl(AttendanceRecordMapper attendanceRecordMapper) {
        this.attendanceRecordMapper = attendanceRecordMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result<?> clock(String header) {
        String token = header.substring(7);
        Long empId = Long.valueOf(JwtUtil.getUserId(token));
        LocalDate today = LocalDate.now();

        Long todayRecordId = attendanceRecordMapper.findRecordByDate(empId, today);
        if (todayRecordId == null) {
            attendanceRecordMapper.insert(empId);
            return Result.success("打卡成功！");
        }
        attendanceRecordMapper.updateClockOut(todayRecordId);
        return Result.success("打卡成功！");
    }

    @Override
    public Result<?> getMyAttendanceRecords(Long eid, Integer start, Integer size) {
        if (size == null || size <= 0) {
            return Result.error(400, "请求错误，缺少参数 size！");
        }
        if (start == null) {
            start = 0;
        }
        // 前端传的是偏移量 start，PageHelper 需要的是页码（从1开始）
        int pageNum = (start / size) + 1;
        PageHelper.startPage(pageNum, size);
        List<AttendanceVO> attendanceVOS = attendanceRecordMapper.selectAttendanceWithLeave(eid);

        PageInfo<AttendanceVO> page = new PageInfo<>(attendanceVOS);

        return Result.success(AttendancePageVO.from(page));
    }
}
