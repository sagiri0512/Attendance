package com.sagiri.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * 社保公积金配置实体
 *
 * <p>对应 social_insurance_config 表，记录每个员工的五险一金缴存基数和比例。
 * 各字段均为比例值（如0.08表示8%），缴费金额 = 基数 * 比例。</p>
 *
 * @author sagiri
 */
@Setter
@Getter
public class SocialInsuranceConfig {

    /** 主键ID */
    private Long id;
    /** 所属员工ID（FK → employee.id） */
    private Long empId;
    /** 社保缴费基数（月） */
    private Double socialInsuranceBase;
    /** 公积金缴费基数（月） */
    private Double housingFundBase;
    /** 养老保险 - 个人比例 */
    private Double pensionPersonal;
    /** 养老保险 - 公司比例 */
    private Double pensionCompany;
    /** 医疗保险 - 个人比例 */
    private Double medicalPersonal;
    /** 医疗保险 - 公司比例 */
    private Double medicalCompany;
    /** 失业保险 - 个人比例 */
    private Double unemploymentPersonal;
    /** 失业保险 - 公司比例 */
    private Double unemploymentCompany;
    /** 工伤保险 - 公司比例（个人不缴） */
    private Double injuryCompany;
    /** 生育保险 - 公司比例（个人不缴） */
    private Double maternityCompany;
    /** 住房公积金 - 个人比例 */
    private Double housingFundPersonal;
    /** 住房公积金 - 公司比例 */
    private Double housingFundCompany;
    /** 备注 */
    private String remark;
    /** 创建时间 */
    private java.sql.Timestamp createdAt;
}
