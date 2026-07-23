package com.sagiri.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 考勤记录视图对象
 *
 * <p>对应 attendance_record LEFT JOIN leave_request 的查询结果。
 * attendance_record字段以a_前缀，leave_request字段以l_前缀，
 * 请假相关字段可能为null（当天无请假时）。
 * 用于前端分页展示我的考勤列表。</p>
 *
 * @author sagiri
 */
@Setter
@Getter
public class AttendanceVO {

    // ========== attendance_record 字段 (a_ 前缀) ==========

    /** 考勤记录ID */
    private Long aId;
    /** 考勤日期 */
    private LocalDate aDate;
    /** 上班打卡时间 */
    private LocalDateTime aClockIn;
    /** 下班打卡时间 */
    private LocalDateTime aClockOut;
    /** 考勤状态：0-正常 1-迟到 2-早退 3-缺勤 */
    private Integer aStatus;
    /** 缺勤小时数 */
    private BigDecimal aAbsentHours;
    /** 关联请假单ID */
    private Long aLeaveId;

    // ========== leave_request 字段 (l_ 前缀，LEFT JOIN 可能为null) ==========

    /** 请假类型：1-年假 2-事假 3-病假 4-婚假 5-产假 6-丧假 7-调休 */
    private Integer lType;
    /** 请假开始时间 */
    private LocalDateTime lStart;
    /** 请假结束时间 */
    private LocalDateTime lEnd;
    /** 请假天数 */
    private BigDecimal lDays;
    /** 请假原因 */
    private String lReason;
    /** 最终审批结果 */
    private Integer lFinal;
}
