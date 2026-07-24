package com.sagiri.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 考勤记录视图对象
 *
 * <p>对应 work_calendar LEFT JOIN attendance_record LEFT JOIN leave_request LEFT JOIN overtime_request 的查询结果。
 * 字段以字母前缀区分来源表：w_→工作历、a_→考勤、l_→请假、o_→加班。
 * 请假/加班相关字段可能为null（当天无请假/加班时）。
 * 用于前端分页展示我的考勤列表。</p>
 *
 * @author sagiri
 */
@Setter
@Getter
public class AttendanceVO {

    // ========== work_calendar 字段 (w_ 前缀) ==========

    /** 日期 */
    private LocalDate wDate;
    /** 日期类型：0-工作日 1-休息日 2-法定假 3-调休 */
    private Integer wDayType;
    /** 节假日名称（如"国庆节"，工作日/普通周末为null） */
    private String wHolidayName;

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

    // ========== overtime_request 字段 (o_ 前缀，LEFT JOIN 可能为null) ==========

    /** 实际加班小时数 */
    private BigDecimal oHours;
    /** 加班工资倍数：1/1.5/2/3 */
    private BigDecimal oWage;
    /** 加班审批状态：0-审批中 1-已通过 2-已驳回 */
    private Integer oStatus;
}
