package com.sagiri.vo;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
public class HolidayVo {
    private Long id;
    private Date date;
    private Integer dayType;
}
