package com.sagiri.mapper;

import org.apache.ibatis.annotations.Param;

public interface AttendanceRecordMapper {
    public Long findTodayRecordId(@Param("empId") Long id);

    public Integer insert(@Param("empId") Long id);

    public Integer updateClockOut(@Param("id") Long id);
}
