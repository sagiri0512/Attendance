package com.sagiri.mapper;

import com.sagiri.vo.AttendanceVO;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRecordMapper {
    /** 查指定日期的打卡记录ID */
    public Long findRecordByDate(@Param("empId") Long empId, @Param("date") LocalDate date);

    public Integer insert(@Param("empId") Long empId);

    public Integer updateClockOut(@Param("id") Long recordId);

    public List<AttendanceVO> selectAttendanceWithLeave(@Param("eid") Long eid);
}
