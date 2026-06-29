package com.sagiri.entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AttendanceRecord {

  private Long id;
  private Long empId;
  private java.sql.Date recordDate;
  private java.sql.Timestamp clockInTime;
  private java.sql.Timestamp clockOutTime;
  private Long status;
  private Double absentHours;
  private String remark;
  private java.sql.Timestamp createdAt;
  private java.sql.Timestamp updatedAt;
}
