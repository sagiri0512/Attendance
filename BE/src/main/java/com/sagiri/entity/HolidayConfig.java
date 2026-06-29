package com.sagiri.entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class HolidayConfig {

  private Long id;
  private java.sql.Date holidayDate;
  private String holidayName;
  private Double multiplier;
  private Long isWorkday;
  private java.sql.Timestamp createdAt;
}
