package com.sagiri.mapper;

import com.sagiri.vo.AttendanceVO;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 考勤记录 Mapper
 *
 * <p>操作 attendance_record 表，提供打卡记录的增改查功能。</p>
 *
 * @author sagiri
 */
public interface AttendanceRecordMapper {

    /**
     * 查询指定日期的打卡记录ID
     *
     * <p>用于判断当天是否已有打卡记录：有则更新下班时间，无则插入新记录。</p>
     *
     * @param empId 员工ID
     * @param date  考勤日期
     * @return 记录ID，无记录返回null
     */
    Long findRecordByDate(@Param("empId") Long empId, @Param("date") LocalDate date);

    /**
     * 插入上班打卡记录
     *
     * @param empId 员工ID
     * @return 影响行数
     */
    Integer insert(@Param("empId") Long empId);

    /**
     * 更新下班打卡时间
     *
     * @param recordId 考勤记录ID
     * @return 影响行数
     */
    Integer updateClockOut(@Param("id") Long recordId);

    /**
     * 分页查询考勤记录（LEFT JOIN请假表）
     *
     * @param eid 员工ID
     * @return 考勤+请假关联数据列表
     */
    List<AttendanceVO> selectAttendanceWithLeave(@Param("eid") Long eid);
}
