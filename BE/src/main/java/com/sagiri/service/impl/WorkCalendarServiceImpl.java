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

/**
 * 工作日历服务实现
 *
 * <p>实现节假日日历的确认（批量写入）和查询功能。
 * 确认操作会先删除旧年份数据，再批量插入新确认数据，保证幂等性。</p>
 *
 * @author sagiri
 */
@Service
public class WorkCalendarServiceImpl implements WorkCalendarService {

    private final WorkCalendarMapper workCalendarMapper;

    /**
     * 构造器注入工作日历Mapper
     *
     * @param workCalendarMapper 工作日历Mapper
     */
    public WorkCalendarServiceImpl(WorkCalendarMapper workCalendarMapper) {
        this.workCalendarMapper = workCalendarMapper;
    }

    /**
     * 确认全年节假日数据
     *
     * <p>业务规则：
     * <ol>
     *   <li>校验请求参数（year和days不可为空）</li>
     *   <li>过滤无效日期数据（日期或类型为空）</li>
     *   <li>注入确认人ID到每条数据</li>
     *   <li>先删除旧年份数据，再批量插入</li>
     * </ol></p>
     *
     * @param dto         节假日确认DTO
     * @param confirmedBy 确认人ID
     * @return 批量写入结果
     */
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

        // 先删后插，保证幂等性
        workCalendarMapper.deleteByYear(dto.getYear());
        workCalendarMapper.batchInsert(validDays);

        return Result.success("确认成功，共 " + validDays.size() + " 天");
    }

    /**
     * 查询指定年份的节假日日历
     *
     * @param year 年份
     * @return 该年全部日期的工作/节假日标记列表
     */
    @Override
    public Result<?> getHoliday(Integer year) {
        if (year == null) {
            return Result.error(400, "请输入查询年份");
        }
        List<HolidayVo> holidayVos = workCalendarMapper.selectByYear(year);

        if (holidayVos == null) {
            return Result.error(400, "数据不存在！");
        }

        return Result.success(holidayVos);
    }
}
