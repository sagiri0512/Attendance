package com.sagiri.service.impl;

import com.sagiri.dto.HolidayConfirmDTO;
import com.sagiri.mapper.WorkCalendarMapper;
import com.sagiri.service.WorkCalendarService;
import com.sagiri.vo.HolidayVo;
import com.sagiri.vo.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class WorkCalendarServiceImpl implements WorkCalendarService {

    private final WorkCalendarMapper workCalendarMapper;

    public WorkCalendarServiceImpl(WorkCalendarMapper workCalendarMapper) {
        this.workCalendarMapper = workCalendarMapper;
    }

    @Override
    @Transactional
    public Result<?> confirmCalendar(HolidayConfirmDTO dto, Long confirmedBy) {
        if (dto == null || dto.getYear() == null) {
            return Result.error(400, "参数缺失：year 不能为空");
        }
        if (dto.getDays() == null || dto.getDays().isEmpty()) {
            return Result.error(400, "参数缺失：days 不能为空");
        }

        List<HolidayConfirmDTO.DayItem> validDays = dto.getDays().stream()
                .filter(d -> StringUtils.hasText(d.getDate()) && StringUtils.hasText(d.getCategory()))
                .toList();

        if (validDays.isEmpty()) {
            return Result.error(400, "没有有效的日期数据");
        }

        // 注入确认人ID
        validDays.forEach(d -> d.setConfirmedBy(confirmedBy));

        workCalendarMapper.deleteByYear(dto.getYear());
        workCalendarMapper.batchInsert(validDays);

        return Result.success("确认成功，共 " + validDays.size() + " 天");
    }

    @Override
    public Result<?> getHoliday(Integer year) {
        if(year == null){
            return Result.error(400, "请输入查询年份");
        }
        List<HolidayVo> holidayVos = workCalendarMapper.selectByYear(year);

        if(holidayVos == null){
            return Result.error(400, "数据不存在！");
        }

        return Result.success(holidayVos);
    }
}
