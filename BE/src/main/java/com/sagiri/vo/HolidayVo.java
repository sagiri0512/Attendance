package com.sagiri.vo;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

/**
 * 节假日视图对象
 *
 * <p>用于返回指定年份的工作日/节假日列表，
 * 供前端日历组件渲染。</p>
 *
 * @author sagiri
 */
@Getter
@Setter
public class HolidayVo {

    /** 日历记录ID */
    private Long id;
    /** 日期 */
    private Date date;
    /** 日期类型：0-工作日 1-休息日 2-节假日 */
    private Integer dayType;
}
