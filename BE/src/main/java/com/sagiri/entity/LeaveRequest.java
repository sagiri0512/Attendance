package com.sagiri.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * 请假申请实体
 *
 * <p>对应 leave_request 表，管理员工的请假申请与审批流程。
 * 请假类型：1-年假 2-事假 3-病假 4-婚假 5-产假 6-丧假 7-调休 8-其他
 * 审批流程：员工提交 → PL审核 → PM审核 → 最终结果。
 * 审批状态：0-待审核 1-通过 2-驳回</p>
 *
 * @author sagiri
 */
@Setter
@Getter
public class LeaveRequest {

    /** 主键ID */
    private Long id;
    /** 申请人ID */
    private Long empId;
    /** 请假类型：1-年假 2-事假 3-病假 4-婚假 5-产假 6-丧假 7-调休 8-其他 */
    private Long leaveType;
    /** 请假开始时间 */
    private java.sql.Timestamp startTime;
    /** 请假结束时间 */
    private java.sql.Timestamp endTime;
    /** 请假天数 */
    private Double days;
    /** 请假原因 */
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
    /** 创建时间 */
    private java.sql.Timestamp createdAt;
    /** 更新时间 */
    private java.sql.Timestamp updatedAt;
}
