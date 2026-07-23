package com.sagiri.service;

import com.sagiri.vo.Result;

/**
 * 考勤记录服务接口
 *
 * @author sagiri
 */
public interface AttendanceRecordService {

    /**
     * 上下班打卡
     *
     * <p>从JWT令牌解析员工身份，判断当前为上班还是下班打卡。
     * 当天首次打卡记上班，再次打卡记下班。凌晨5点前打卡归属前一天。</p>
     *
     * @param header Authorization请求头（Bearer xxx）
     * @return 打卡结果
     */
    Result<?> clock(String header);

    /**
     * 分页查询我的考勤记录
     *
     * <p>LEFT JOIN请假表，同时返回考勤与请假信息。</p>
     *
     * @param eid   员工ID
     * @param start 偏移量（从0开始）
     * @param size  每页条数
     * @return 分页考勤记录
     */
    Result<?> getMyAttendanceRecords(Long eid, Integer start, Integer size);
}
