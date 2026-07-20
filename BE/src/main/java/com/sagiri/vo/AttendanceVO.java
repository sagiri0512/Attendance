package com.sagiri.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
public class AttendanceVO {

    // attendance_record 字段 (a_ 前缀)
    private Long aId;
    private LocalDate aDate;
    private LocalDateTime aClockIn;
    private LocalDateTime aClockOut;
    private Integer aStatus;
    private BigDecimal aAbsentHours;
    private Long aLeaveId;

    // leave_request 字段 (l_ 前缀，LEFT JOIN 可能为null)
    private Integer lType;
    private LocalDateTime lStart;
    private LocalDateTime lEnd;
    private BigDecimal lDays;
    private String lReason;
    private Integer lFinal;
}
