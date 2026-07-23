package com.sagiri.controller;

import com.sagiri.vo.Result;
import com.sagiri.service.AttendanceRecordService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 考勤打卡控制器
 *
 * <p>提供员工上下班打卡和考勤记录查询功能。
 * 所有接口要求用户已认证且角色为0/1/2/3之一。</p>
 *
 * @author sagiri
 */
@RestController
@RequestMapping("/attendance")
@PreAuthorize("hasAnyAuthority('0','1','2','3')")
public class AttendanceController {

    private final AttendanceRecordService attendanceRecordService;

    /**
     * 构造器注入考勤服务
     *
     * @param attendanceRecordService 考勤记录服务
     */
    public AttendanceController(AttendanceRecordService attendanceRecordService) {
        this.attendanceRecordService = attendanceRecordService;
    }

    /**
     * 上班/下班打卡
     *
     * <p>从请求头提取JWT令牌，调用打卡逻辑。
     * 同一天首次请求记上班卡，再次请求记下班卡。</p>
     *
     * @param req HTTP请求（用于提取Authorization头）
     * @return 打卡结果
     */
    @PostMapping("/clock")
    public ResponseEntity<Result<?>> clock(HttpServletRequest req) {
        String header = req.getHeader("Authorization");
        Result<?> result = attendanceRecordService.clock(header);
        return ResponseEntity.status(result.getCode()).body(result);
    }

    /**
     * 分页查询我的考勤记录
     *
     * @param eid   员工ID（路径参数）
     * @param start 起始页（从0开始）
     * @param size  每页条数
     * @return 分页考勤记录，包含请假关联信息
     */
    @GetMapping("/my/{eid}")
    public ResponseEntity<Result<?>> getMyAttendanceRecords(
            @PathVariable Long eid,
            @RequestParam("start") Integer start,
            @RequestParam("size") Integer size) {
        Result<?> result = attendanceRecordService.getMyAttendanceRecords(eid, start, size);
        return ResponseEntity.status(result.getCode()).body(result);
    }
}
