package com.sagiri.service;

import com.sagiri.dto.HolidayConfirmDTO;
import com.sagiri.vo.Result;

/**
 * 工作日历服务接口
 *
 * @author sagiri
 */
public interface WorkCalendarService {

    /**
     * 接收前端确认的全年日历数据，写入 work_calendar 表
     *
     * <p>先删除旧年份数据，再批量插入新确认数据。</p>
     *
     * @param dto         节假日确认DTO（含年份和日期列表）
     * @param confirmedBy 确认人ID
     * @return 批量写入结果
     */
    Result<?> confirmCalendar(HolidayConfirmDTO dto, Long confirmedBy);

    /**
     * 查询指定年份的节假日日历
     *
     * @param year 年份
     * @return 该年全部日期的工作/节假日标记列表
     */
    Result<?> getHoliday(Integer year);
}
