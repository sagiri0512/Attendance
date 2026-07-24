package com.sagiri.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sagiri.constant.SalaryConfig;
import com.sagiri.entity.AttendanceRecord;
import com.sagiri.mapper.AttendanceRecordMapper;
import com.sagiri.mapper.EmployeeMapper;
import com.sagiri.mapper.WorkCalendarMapper;
import com.sagiri.service.AttendanceRecordService;
import com.sagiri.utils.JwtUtil;
import com.sagiri.vo.AttendancePageVO;
import com.sagiri.vo.AttendanceVO;
import com.sagiri.vo.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 考勤记录服务实现
 *
 * <p>核心功能：
 * <ol>
 *   <li>上下班打卡 — 凌晨5点分界，当天首次记上班、再次记下班</li>
 *   <li>分页查询考勤记录 — 关联请假表、加班表、工作日历</li>
 *   <li>缺勤标记 — 定时任务触发，按迟到/早退/缺卡计算缺勤小时</li>
 * </ol></p>
 *
 * @author sagiri
 */
@Service
public class AttendanceRecordServiceImpl implements AttendanceRecordService {

    private final AttendanceRecordMapper attendanceRecordMapper;

    private final EmployeeMapper employeeMapper;

    private final WorkCalendarMapper workCalendarMapper;

    /**
     * 构造器注入 Mapper 依赖
     *
     * @param attendanceRecordMapper 考勤记录 Mapper
     * @param employeeMapper         员工 Mapper（用于获取所有员工 ID）
     * @param workCalendarMapper     工作日历 Mapper（用于判断是否工作日）
     */
    public AttendanceRecordServiceImpl(AttendanceRecordMapper attendanceRecordMapper,
                                       EmployeeMapper employeeMapper,
                                       WorkCalendarMapper workCalendarMapper) {
        this.attendanceRecordMapper = attendanceRecordMapper;
        this.employeeMapper = employeeMapper;
        this.workCalendarMapper = workCalendarMapper;
    }

    /**
     * 上下班打卡
     *
     * <p>业务规则：
     * <ol>
     *   <li>从JWT令牌解析员工ID</li>
     *   <li>以凌晨5点为分界线：5点前打卡归属前一天，5点后归属当天</li>
     *   <li>当天无记录 → 插入上班打卡</li>
     *   <li>当天有记录 → 更新下班打卡时间</li>
     * </ol></p>
     *
     * @param header Authorization请求头（Bearer xxx）
     * @return 打卡结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result<?> clock(String header) {
        String token = header.substring(7);
        Long empId = Long.valueOf(JwtUtil.getUserId(token));

        // 获取今天的日期
        LocalDate today = LocalDate.now();

        // 判断是否是凌晨5点之后打卡
        boolean after5 = LocalTime.now().getHour() >= 5;

        // 凌晨5点之前打卡，日期归属前一天
        if (!after5) {
            today = today.minusDays(1);
        }

        Long todayRecordId = attendanceRecordMapper.findRecordByDate(empId, today);
        if (todayRecordId == null) {
            // 当天无记录 → 插入上班打卡
            attendanceRecordMapper.insert(empId);
            return Result.success("打卡成功！");
        }
        // 当天有记录 → 更新下班打卡时间
        attendanceRecordMapper.updateClockOut(todayRecordId);
        return Result.success("打卡成功！");
    }

    /**
     * 分页查询我的考勤记录
     *
     * <p>使用PageHelper进行物理分页，前端传偏移量(start)和每页条数(size)，
     * 内部转换为PageHelper所需的页码（从1开始）。</p>
     *
     * @param eid   员工ID
     * @param start 偏移量（从0开始）
     * @param size  每页条数
     * @return 分页考勤记录（含关联请假信息）
     */
    @Override
    public Result<?> getMyAttendanceRecords(Long eid, Integer start, Integer size) {
        if (size == null || size <= 0) {
            return Result.error(400, "请求错误，缺少参数 size！");
        }
        if (start == null) {
            start = 0;
        }
        // 前端传的是偏移量 start，PageHelper 需要的是页码（从1开始）
        int pageNum = (start / size) + 1;
        PageHelper.startPage(pageNum, size);
        List<AttendanceVO> attendanceVOS = attendanceRecordMapper.selectAttendanceWithLeave(eid);

        PageInfo<AttendanceVO> page = new PageInfo<>(attendanceVOS);

        return Result.success(AttendancePageVO.from(page));
    }

    /**
     * 标记缺勤（定时任务：每天凌晨 5 点执行）。
     *
     * <h3>处理流程</h3>
     * <ol>
     *   <li>获取所有员工 ID</li>
     *   <li>查询每个员工<b>前一天</b>的考勤记录</li>
     *   <li>根据打卡时间计算缺勤小时：迟到 + 早退</li>
     *   <li>扣减请假小时数</li>
     *   <li>最终缺勤 > 0 则更新状态</li>
     * </ol>
     *
     * <h3>计算规则</h3>
     * <table>
     *   <tr><th>情况</th><th>缺勤小时</th></tr>
     *   <tr><td>无打卡记录</td><td>8（插入新记录）</td></tr>
     *   <tr><td>缺上班/下班打卡</td><td>8</td></tr>
     *   <tr><td>迟到</td><td>Duration.between(09:00, 上班时间) 按分钟向上取整</td></tr>
     *   <tr><td>早退</td><td>Duration.between(下班时间, 18:00) 按分钟向上取整</td></tr>
     *   <tr><td>请假扣减</td><td>absentHours - leaveHours（≥ 0 才更新）</td></tr>
     * </table>
     *
     * <h3>依赖常量</h3>
     * <ul>
     *   <li>{@link SalaryConfig#CLOCK_IN_TIME} = 09:00</li>
     *   <li>{@link SalaryConfig#CLOCK_OUT_TIME} = 18:00</li>
     * </ul>
     *
     * <p>踩过的坑见单元测试：{@code AttendanceRecordServiceImplTest}</p>
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void markAbsent() {
        // 获取要处理的日期
        LocalDate day = LocalDate.now().minusDays(1);
        // 非工作日(周末/节假日/调休)跳过缺勤标记
        if (!isWorkday(day)) {
            return;
        }
        List<Long> idList = employeeMapper.getAllEmpId();
        LocalTime start = SalaryConfig.CLOCK_IN_TIME;  // 09:00
        LocalTime end = SalaryConfig.CLOCK_OUT_TIME;
        for(Long id : idList){
            AttendanceRecord attendanceRecord = attendanceRecordMapper.getRecordInfoByDate(id, day);
            if(attendanceRecord == null){
                attendanceRecordMapper.insertAbsent(id, day);
                continue;
            }
            //缺勤时长
            Integer absentHours = 0;

            //打卡缺失
            if(attendanceRecord.getClockInTime() == null || attendanceRecord.getClockOutTime() == null){
                absentHours = 8;
            }else {
                LocalTime clockIn = attendanceRecord.getClockInTime()
                        .toLocalDateTime().toLocalTime();
                LocalDateTime clockOutLdt = attendanceRecord.getClockOutTime().toLocalDateTime();
                LocalTime clockOut = clockOutLdt.toLocalTime();
                // 迟到（缺勤区间 [start, clockIn)，扣除午休 12:00~13:00）
                if (clockIn.isAfter(start)) {
                    long lateMinutes = workMinutesBetween(start, clockIn);
                    int lateHours = (int) ((lateMinutes + 59) / 60);
                    absentHours += lateHours;
                }
                // 早退（必须有下班打卡才判，缺勤区间 [clockOut, end)，扣除午休 12:00~13:00）
                // 下班打卡是次日(加班到凌晨)则不判早退
                boolean isClockOutNextDay = clockOutLdt.toLocalDate().isAfter(day);
                if (!isClockOutNextDay && clockOut.isBefore(end)) {
                    long earlyMinutes = workMinutesBetween(clockOut, end);
                    int earlyHours = (int) ((earlyMinutes + 59) / 60);
                    absentHours += earlyHours;
                }
            }
            if (attendanceRecord.getLeaveHours() != null) {
                absentHours -= attendanceRecord.getLeaveHours().intValue();
            }
            //存在缺勤
            if(absentHours > 0){
                attendanceRecordMapper.updateStatusAndAbsentHoursByEidAndDay(absentHours, id, day);
            }
        }
    }

    /**
     * 判断指定日期是否为工作日
     *
     * <p>优先查 work_calendar 表的 day_type：0=工作日，1/2/3=休息日/节假日/调休。
     * 若日历表未配置该日期，按周一~周五为工作日兜底。</p>
     *
     * @param date 日期
     * @return true=工作日（需标记缺勤），false=非工作日（跳过）
     */
    private boolean isWorkday(LocalDate date) {
        Integer dayType = workCalendarMapper.getDayTypeByDate(date);
        if (dayType != null) {
            return dayType == 0;
        }
        // 日历未配置：周一~周五当工作日兜底
        DayOfWeek dow = date.getDayOfWeek();
        return dow != DayOfWeek.SATURDAY && dow != DayOfWeek.SUNDAY;
    }

    /**
     * 计算时间区间内的工作分钟数（扣除午休 12:00~13:00）
     *
     * <p>用于迟到/早退缺勤时长计算，避免把午休时段算进缺勤。
     * 例如 09:00 迟到到 14:30，区间 5.5 小时，扣除午休 1 小时 = 4.5 小时工作缺勤。</p>
     *
     * @param from 区间起点（含）
     * @param to   区间终点（不含）
     * @return 工作分钟数（已扣除午休）
     */
    private long workMinutesBetween(LocalTime from, LocalTime to) {
        long total = Duration.between(from, to).toMinutes();
        LocalTime lunchStart = SalaryConfig.LUNCH_START;
        LocalTime lunchEnd = SalaryConfig.LUNCH_END;
        // 缺勤区间 [from, to) 与午休 [lunchStart, lunchEnd) 的重叠部分
        LocalTime overlapStart = from.isAfter(lunchStart) ? from : lunchStart;  // max
        LocalTime overlapEnd   = to.isBefore(lunchEnd)     ? to   : lunchEnd;    // min
        long overlap = overlapStart.isBefore(overlapEnd)
                ? Duration.between(overlapStart, overlapEnd).toMinutes()
                : 0L;
        return total - overlap;
    }
}
