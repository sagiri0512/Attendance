package com.sagiri.mapper;

import com.sagiri.dto.HolidayConfirmDTO;
import com.sagiri.vo.HolidayVo;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

public interface WorkCalendarMapper {

    /** 删除某年所有日历数据 */
    Integer deleteByYear(@Param("year") Integer year);

    /** 批量插入日历数据（category 在 SQL 层映射到 day_type/wage/holiday/after） */
    Integer batchInsert(@Param("list") List<HolidayConfirmDTO.DayItem> list);

    /** 按年份查询日历数据（走索引） */
    List<HolidayVo> selectByYear(@Param("year") Integer year);

    /**
     * 查询指定日期的类型
     *
     * <p>day_type：0-工作日 1-休息日(周末) 2-节假日 3-调休。
     * 返回 null 表示该日期未配置。</p>
     *
     * @param date 日期
     * @return 日期类型，未配置返回 null
     */
    Integer getDayTypeByDate(@Param("date") LocalDate date);
}
