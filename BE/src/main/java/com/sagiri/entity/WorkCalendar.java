package com.sagiri.entity;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * 工作日历实体
 *
 * <p>对应 work_calendar 表，记录全年每天的工作/节假日安排。
 * 日期类型(dayType)：0-工作日 1-休息日(含周末) 2-节假日。
 * 用于考勤计算时的日期判定（如判断是否工作日、加班工资倍数等）。</p>
 *
 * @author sagiri
 */
@Setter
@Getter
public class WorkCalendar {

    /** 主键ID */
    private Long id;
    /** 日期 */
    private Date date;
    /** 日期类型：0-工作日 1-休息日 2-节假日 */
    private Integer dayType;
    /** 是否法定假日：0-否 1-是 */
    private Integer holiday;
    /** 节假日名称（如"春节"、"国庆节"） */
    private String holidayName;
    /** 加班工资倍数（工作日1.5/休息日2.0/节假日3.0） */
    private Integer wage;
    /** 调休来源：0-无调休 1-节前调休 2-节后调休 */
    private Integer after;
    /** 调休目标日期 */
    private String target;
    /** 是否已确认：0-未确认 1-已确认 */
    private Integer confirmed;
    /** 确认人ID */
    private Long confirmedBy;
    /** 确认时间 */
    private Timestamp confirmedAt;
    /** 数据来源（如"国务院办公厅通知"） */
    private String source;
    /** 创建时间 */
    private Timestamp createdAt;
}
