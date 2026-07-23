package com.sagiri.dto;

import lombok.Data;

/**
 * 员工创建/更新 DTO（含社保公积金配置）
 */
@Data
public class EmployeeSaveDTO {

    // ---------- 基本信息 ----------
    private String realName;
    private Byte role;
    private String phone;
    private Long pmId;
    private Long plId;

    // ---------- 薪资结构 ----------
    private Double baseSalary;
    private Double positionSalary;
    private Double performanceBonus;
    private Double housingSubsidy;
    private Double carSubsidy;
    private Double mealSubsidy;
    private Double otherSubsidy;

    // ---------- 社保公积金配置 ----------
    private SocialInsuranceConfigDTO socialInsuranceConfig;

    @Data
    public static class SocialInsuranceConfigDTO {
        /** 社保缴费基数（为0则按基本工资） */
        private Double socialInsuranceBase;
        /** 公积金缴费基数（为0则按基本工资） */
        private Double housingFundBase;
        /** 养老保险个人比例(%) */
        private Double pensionPersonal;
        /** 养老保险公司比例(%) */
        private Double pensionCompany;
        /** 医疗保险个人比例(%) */
        private Double medicalPersonal;
        /** 医疗保险公司比例(%) */
        private Double medicalCompany;
        /** 失业保险个人比例(%) */
        private Double unemploymentPersonal;
        /** 失业保险公司比例(%) */
        private Double unemploymentCompany;
        /** 工伤保险公司比例(%) */
        private Double injuryCompany;
        /** 生育保险公司比例(%) */
        private Double maternityCompany;
        /** 住房公积金个人比例(%) */
        private Double housingFundPersonal;
        /** 住房公积金公司比例(%) */
        private Double housingFundCompany;
        /** 备注 */
        private String remark;
    }
}
