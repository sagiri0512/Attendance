package com.sagiri.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * 加班申请实体
 *
 * <p>对应 overtime_request 表，管理员工的加班申请与审批流程。
 * 审批流程：员工提交 → PL审核 → PM审核 → 最终结果。
 * 审批状态：0-待审核 1-通过 2-驳回。
 * 日期类型(dayType)：0-工作日 1-休息日 2-节假日，影响加班工资倍数。</p>
 *
 * @author sagiri
 */
@Setter
@Getter
public class OvertimeRequest {

    /** 主键ID */
    private Long id;
    /** 申请人ID */
    private Long empId;
    /** 加班日期 */
    private java.sql.Date overtimeDate;
    /** 申请加班小时数 */
    private Double applyHours;
    /** 实际加班小时数（审批确认后） */
    private Double actualHours;
    /** 加班原因 */
    private String reason;
    /** 是否为特殊加班：0-正常加班 1-特殊加班 */
    private Long isSpecial;
    /** 日期类型：0-工作日 1-休息日 2-节假日 */
    private Integer dayType;
    /** 加班工资倍数 */
    private Double wage;
    /** 当前审批步骤 */
    private Long step;
    /** PL审核状态：0-待审核 1-通过 2-驳回 */
    private Long plStatus;
    /** PL审批意见 */
    private String plRemark;
    /** PL审批时间 */
    private java.sql.Timestamp plApproveTime;
    /** PM审核状态：0-待审核 1-通过 2-驳回 */
    private Long pmStatus;
    /** PM审批意见 */
    private String pmRemark;
    /** PM审批时间 */
    private java.sql.Timestamp pmApproveTime;
    /** 最终审批结果：0-待定 1-通过 2-驳回 */
    private Long finalStatus;
    /** 创建时间 */
    private java.sql.Timestamp createdAt;
    /** 更新时间 */
    private java.sql.Timestamp updatedAt;
}
