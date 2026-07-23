package com.sagiri.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * 团队绩效实体
 *
 * <p>对应 group_performance 表，记录每个PM团队每月的绩效奖金额度。
 * 由上级（如HR或管理员）设定，用作该团队员工绩效分配的基准。</p>
 *
 * @author sagiri
 */
@Setter
@Getter
public class GroupPerformance {

    /** 主键ID */
    private Long id;
    /** 项目经理ID */
    private Long pmId;
    /** 月份，格式 YYYY-MM */
    private String month;
    /** 绩效奖金额度 */
    private Double performanceAmount;
    /** 设定人ID */
    private Long setBy;
    /** 创建时间 */
    private java.sql.Timestamp createdAt;
    /** 更新时间 */
    private java.sql.Timestamp updatedAt;
}
