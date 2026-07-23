package com.sagiri.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * 员工实体
 *
 * <p>对应 employee 表，存储员工基本信息、薪资结构与组织关系。
 * 角色定义：0-普通员工 1-项目组长(PL) 2-项目经理(PM) 3-HR 4-系统管理员</p>
 *
 * @author sagiri
 */
@Setter
@Getter
public class Employee {

    /** 主键ID */
    private Long id;
    /** 工号，格式 EM0001 */
    private String empNo;
    /** 密码（BCrypt加密存储） */
    private String password;
    /** 真实姓名 */
    private String realName;
    /** 直属项目经理ID */
    private Long pmId;
    /** 直属项目组长ID */
    private Long plId;
    /** 角色：0-员工 1-PL 2-PM 3-HR 4-管理员 */
    private Byte role;
    /** 手机号 */
    private String phone;
    /** 基本工资 */
    private Double baseSalary;
    /** 岗位工资 */
    private Double positionSalary;
    /** 绩效奖金 */
    private Double performanceBonus;
    /** 住房补贴 */
    private Double housingSubsidy;
    /** 交通补贴 */
    private Double carSubsidy;
    /** 餐费补贴 */
    private Double mealSubsidy;
    /** 其他补贴 */
    private Double otherSubsidy;
    /** 状态：0-在职 1-离职 */
    private Byte status;
    /** 创建时间 */
    private java.sql.Timestamp createdAt;
    /** 更新时间 */
    private java.sql.Timestamp updatedAt;
    /** 逻辑删除：0-未删除 1-已删除 */
    private Byte deleted;
}
