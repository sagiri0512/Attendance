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

import java.util.List;

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

    @Override
    public Result<?> getMyAttendanceRecords(Long eid, Integer start, Integer size) {
        if(size == null){
            return Result.error(400, "请求错误，缺少参数！");
        }
        if(start == null){
            start = 1;
        }
        PageHelper.startPage(start, size);
        List<AttendanceVO> attendanceVOS = attendanceRecordMapper.selectAttendanceWithLeave(eid);

        PageInfo<AttendanceVO> page = new PageInfo<>(attendanceVOS);

        return Result.success(AttendancePageVO.from(page));
    }
}
