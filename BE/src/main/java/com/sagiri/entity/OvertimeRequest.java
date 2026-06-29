package com.sagiri.entity;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OvertimeRequest {

  private Long id;
  private Long empId;
  private java.sql.Date overtimeDate;
  private Double applyHours;
  private Double actualHours;
  private Double salaryMultiplier;
  private String reason;
  private Long isSpecial;
  private Long isCrossDay;
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
