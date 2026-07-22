package com.sagiri.controller;

import com.sagiri.dto.HolidayConfirmDTO;
import com.sagiri.service.WorkCalendarService;
import com.sagiri.vo.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/holiday")
public class HolidayController {

    private final WorkCalendarService workCalendarService;

    public HolidayController(WorkCalendarService workCalendarService) {
        this.workCalendarService = workCalendarService;
    }

    /**
     * 接收前端确认的全年节假日数据，写入 work_calendar 表
     * 仅 role=4（系统管理员）可调用
     */
    @PreAuthorize("hasAuthority('4')")
    @PostMapping("/confirm")
    public ResponseEntity<Result<?>> confirm(@RequestBody HolidayConfirmDTO dto) {
        String userIdStr = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long confirmedBy = Long.valueOf(userIdStr);
        Result<?> result = workCalendarService.confirmCalendar(dto, confirmedBy);
        return ResponseEntity.status(result.getCode()).body(result);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/getHoliday")
    public ResponseEntity<Result<?>> getHoliday(@RequestParam Integer year){
        Result<?> result = workCalendarService.getHoliday(year);
        return ResponseEntity.status(result.getCode()).body(result);
    }
}
