package com.sagiri.entity;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SocialInsuranceConfig {

  private Long id;
  private Long configYear;
  private Double pensionPersonal;
  private Double pensionCompany;
  private Double medicalPersonal;
  private Double medicalCompany;
  private Double unemploymentPersonal;
  private Double unemploymentCompany;
  private Double injuryCompany;
  private Double maternityCompany;
  private Double housingFundPersonal;
  private Double housingFundCompany;
  private Double minBase;
  private Double maxBase;
  private String remark;
  private java.sql.Timestamp createdAt;
}
