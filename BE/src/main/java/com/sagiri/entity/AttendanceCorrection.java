package com.sagiri.entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AttendanceCorrection {

  private Long id;
  private Long empId;
  private java.sql.Date recordDate;
  private java.sql.Timestamp oldClockIn;
  private java.sql.Timestamp oldClockOut;
  private java.sql.Timestamp newClockIn;
  private java.sql.Timestamp newClockOut;
  private String reason;
  private Long step;
  private Long plStatus;
  private String plRemark;
  private java.sql.Timestamp plApproveTime;
  private Long pmStatus;
  private String pmRemark;
  private java.sql.Timestamp pmApproveTime;
  private Long finalStatus;
  private Long executed;
  private java.sql.Timestamp executedTime;
  private java.sql.Timestamp createdAt;
  private java.sql.Timestamp updatedAt;
}
