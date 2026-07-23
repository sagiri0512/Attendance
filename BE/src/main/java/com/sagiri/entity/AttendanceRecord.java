package com.sagiri.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * 考勤记录实体
 *
 * <p>对应 attendance_record 表，记录每位员工每天的上下班打卡信息。
 * 包含打卡时间、考勤状态、缺勤时长及关联的请假/加班记录。</p>
 *
 * @author sagiri
 */
@Setter
@Getter
public class AttendanceRecord {

    /** 主键ID */
    private Long id;
    /** 员工ID，关联 employee.id */
    private Long empId;
    /** 考勤日期 */
    private java.sql.Date recordDate;
    /** 上班打卡时间 */
    private java.sql.Timestamp clockInTime;
    /** 下班打卡时间 */
    private java.sql.Timestamp clockOutTime;
    /** 考勤状态：0-正常 1-迟到 2-早退 3-缺勤 */
    private Long status;
    /** 缺勤小时数 */
    private Double absentHours;
    /** 关联请假单ID */
    private Long leaveRequestId;
    /** 关联加班单ID */
    private Long overtimeRecordId;
    /** 备注 */
    private String remark;
    /** 创建时间 */
    private java.sql.Timestamp createdAt;
    /** 更新时间 */
    private java.sql.Timestamp updatedAt;
}
