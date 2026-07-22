package com.sagiri.entity;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.sql.Timestamp;

@Setter
@Getter
public class WorkCalendar {

    private Long id;
    private Date date;
    private Integer dayType;
    private Integer holiday;
    private String holidayName;
    private Integer wage;
    private Integer after;
    private String target;
    private Integer confirmed;
    private Long confirmedBy;
    private Timestamp confirmedAt;
    private String source;
    private Timestamp createdAt;
}
