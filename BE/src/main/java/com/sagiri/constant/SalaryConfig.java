package com.sagiri.constant;

import lombok.Data;

import java.time.LocalTime;

/**
 * 全局业务常量
 * <ul>
 *   <li>工号生成规则</li>
 *   <li>五险一金默认缴费比例（百分比）</li>
 * </ul>
 */
public class SalaryConfig {
    // ========== 上班时间 ==========
    public static final LocalTime CLOCK_IN_TIME  = LocalTime.of(9, 0);    // 09:00
    // ========== 下班时间 ==========
    public static final LocalTime CLOCK_OUT_TIME = LocalTime.of(18, 0);   // 18:00
    // ========== 午休时间（不计入缺勤） ==========
    public static final LocalTime LUNCH_START    = LocalTime.of(12, 0);   // 12:00
    public static final LocalTime LUNCH_END      = LocalTime.of(13, 0);   // 13:00

    // ========== 工号生成 ==========

    /** 工号前缀，如 "EM" */
    public static final String EMP_NO_PREFIX = "EM";
    /** 工号数字位数，如 4 位 → EM0001 */
    public static final int EMP_NO_DIGITS = 4;

    // ========== 养老保险 ==========

    /** 养老保险个人缴费比例（%） */
    public static final Double PENSION_PERSONAL = 8.0;
    /** 养老保险公司缴费比例（%） */
    public static final Double PENSION_COMPANY = 16.0;

    // ========== 医疗保险 ==========

    /** 医疗保险个人缴费比例（%） */
    public static final Double MEDICAL_PERSONAL = 2.0;
    /** 医疗保险公司缴费比例（%） */
    public static final Double MEDICAL_COMPANY = 8.0;

    // ========== 失业保险 ==========

    /** 失业保险个人缴费比例（%） */
    public static final Double UNEMPLOYMENT_PERSONAL = 0.5;
    /** 失业保险公司缴费比例（%） */
    public static final Double UNEMPLOYMENT_COMPANY = 0.5;

    // ========== 工伤保险 ==========

    /** 工伤保险公司缴费比例（%） */
    public static final Double INJURY_COMPANY = 0.5;

    // ========== 生育保险 ==========

    /** 生育保险公司缴费比例（%） */
    public static final Double MATERNITY_COMPANY = 1.0;

    // ========== 住房公积金 ==========

    /** 住房公积金个人缴费比例（%） */
    public static final Double HOUSING_FUND_PERSONAL = 12.0;
    /** 住房公积金公司缴费比例（%） */
    public static final Double HOUSING_FUND_COMPANY = 12.0;
}
