package com.sagiri.entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Employee {

  private Long id;
  private String empNo;
  private String username;
  private String password;
  private String realName;
  private Long deptId;
  private Long pmId;
  private Long plId;
  private Long role;
  private String phone;
  private Double baseSalary;
  private Double positionSalary;
  private Double performanceBonus;
  private Double housingSubsidy;
  private Double carSubsidy;
  private Double mealSubsidy;
  private Double otherSubsidy;
  private Double socialInsuranceBase;
  private Double housingFundBase;
  private Double housingFundRatioPersonal;
  private Double housingFundRatioCompany;
  private Long status;
  private java.sql.Timestamp createdAt;
  private java.sql.Timestamp updatedAt;
  private Long deleted;
}
