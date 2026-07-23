package com.sagiri.controller;

import com.sagiri.dto.HolidayConfirmDTO;
import com.sagiri.service.WorkCalendarService;
import com.sagiri.vo.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 节假日管理控制器
 *
 * <p>提供全年工作日/节假日日历的确认和查询功能。
 * 确认操作仅系统管理员（role=4）可调用，查询需登录即可。</p>
 *
 * @author sagiri
 */
@RestController
@RequestMapping("/holiday")
public class HolidayController {

    private final WorkCalendarService workCalendarService;

    /**
     * 构造器注入工作日历服务
     *
     * @param workCalendarService 工作日历服务
     */
    public HolidayController(WorkCalendarService workCalendarService) {
        this.workCalendarService = workCalendarService;
    }

    /**
     * 确认全年节假日数据
     *
     * <p>接收前端确认的全年节假日数据，写入 work_calendar 表。
     * 仅 role=4（系统管理员）可调用。</p>
     *
     * @param dto 节假日确认DTO（含全年日期列表）
     * @return 批量写入结果
     */
    @PreAuthorize("hasAuthority('4')")
    @PostMapping("/confirm")
    public ResponseEntity<Result<?>> confirm(@RequestBody HolidayConfirmDTO dto) {
        String userIdStr = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long confirmedBy = Long.valueOf(userIdStr);
        Result<?> result = workCalendarService.confirmCalendar(dto, confirmedBy);
        return ResponseEntity.status(result.getCode()).body(result);
    }

    /**
     * 查询指定年份的节假日日历
     *
     * @param year 年份（如2026）
     * @return 该年全部日期的工作/节假日标记列表
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/getHoliday")
    public ResponseEntity<Result<?>> getHoliday(@RequestParam Integer year) {
        Result<?> result = workCalendarService.getHoliday(year);
        return ResponseEntity.status(result.getCode()).body(result);
    }
}
