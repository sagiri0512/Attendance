package com.sagiri.dto;

import lombok.Data;
import java.util.List;

/**
 * 前端提交的节假日确认数据
 */
@Data
public class HolidayConfirmDTO {

    private Integer year;

    private Integer total;

    private List<DayItem> days;

    @Data
    public static class DayItem {
        /** 日期，格式 yyyy-MM-dd */
        private String date;
        /** 类型：workday / weekend / legal / adjusted / makeup */
        private String category;
        /** 节假日名称，普通日期为空 */
        private String name;
        /** 确认人ID，后端注入 */
        private Long confirmedBy;
    }
}
