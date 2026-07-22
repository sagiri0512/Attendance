package com.sagiri.service;

import com.sagiri.dto.HolidayConfirmDTO;
import com.sagiri.vo.Result;

public interface WorkCalendarService {

    /**
     * 接收前端确认的全年日历数据，写入 work_calendar 表
     */
    Result<?> confirmCalendar(HolidayConfirmDTO dto, Long confirmedBy);

    Result<?> getHoliday(Integer year);
}
