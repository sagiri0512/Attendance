package com.sagiri.entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LeaveRequest {

  private Long id;
  private Long empId;
  private Long leaveType;
  private java.sql.Timestamp startTime;
  private java.sql.Timestamp endTime;
  private Double days;
  private String reason;
  private Long step;
  private Long plStatus;
  private String plRemark;
  private java.sql.Timestamp plApproveTime;
  private Long pmStatus;
  private String pmRemark;
  private java.sql.Timestamp pmApproveTime;
  private Long finalStatus;
  private java.sql.Timestamp createdAt;
  private java.sql.Timestamp updatedAt;
}
