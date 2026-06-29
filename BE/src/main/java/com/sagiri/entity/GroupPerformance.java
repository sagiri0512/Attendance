package com.sagiri.entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GroupPerformance {

  private Long id;
  private Long pmId;
  private String month;
  private Double performanceAmount;
  private Long setBy;
  private java.sql.Timestamp createdAt;
  private java.sql.Timestamp updatedAt;
}
