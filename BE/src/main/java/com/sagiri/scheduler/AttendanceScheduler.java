package com.sagiri.scheduler;

import com.sagiri.service.AttendanceRecordService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AttendanceScheduler {

    private final AttendanceRecordService attendanceRecordService;

    AttendanceScheduler(AttendanceRecordService attendanceRecordService){
        this.attendanceRecordService = attendanceRecordService;
    }

    @Scheduled(cron = "0 0 5 * * ?")  // 每天凌晨5点
    public void syncAbsentStatus() {
        // 标记当天未打卡的员工为缺勤
        attendanceRecordService.markAbsent();
    }
}
