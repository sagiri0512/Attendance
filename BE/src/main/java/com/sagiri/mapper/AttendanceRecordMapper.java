package com.sagiri.mapper;

import com.sagiri.vo.AttendanceVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AttendanceRecordMapper {
    public Long findTodayRecordId(@Param("empId") Long id);

    public Integer insert(@Param("empId") Long id);

    public Integer updateClockOut(@Param("id") Long id);

    public List<AttendanceVO> selectAttendanceWithLeave(@Param("eid") Long eid);
}
