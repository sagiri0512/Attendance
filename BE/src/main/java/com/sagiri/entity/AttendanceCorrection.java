package com.sagiri.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * 考勤修正申请实体
 *
 * <p>对应 attendance_correction 表，用于员工提交打卡记录修正申请。
 * 审批流程：员工提交 → PL审核 → PM审核 → 最终生效。
 * 审批状态：0-待审核 1-通过 2-驳回</p>
 *
 * @author sagiri
 */
@Setter
@Getter
public class AttendanceCorrection {

    /** 主键ID */
    private Long id;
    /** 申请人ID */
    private Long empId;
    /** 原始考勤记录ID */
    private Long attendanceId;
    /** 考勤日期 */
    private java.sql.Date recordDate;
    /** 原上班时间 */
    private java.sql.Timestamp oldClockIn;
    /** 原下班时间 */
    private java.sql.Timestamp oldClockOut;
    /** 新上班时间 */
    private java.sql.Timestamp newClockIn;
    /** 新下班时间 */
    private java.sql.Timestamp newClockOut;
    /** 修正原因 */
    private String reason;
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
    /** 是否已执行：0-未执行 1-已执行 */
    private Long executed;
    /** 执行时间 */
    private java.sql.Timestamp executedTime;
    /** 创建时间 */
    private java.sql.Timestamp createdAt;
    /** 更新时间 */
    private java.sql.Timestamp updatedAt;
}
