package com.sagiri.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sagiri.vo.AttendancePageVO;
import com.sagiri.vo.AttendanceVO;
import com.sagiri.vo.Result;
import com.sagiri.mapper.AttendanceRecordMapper;
import com.sagiri.service.AttendanceRecordService;
import com.sagiri.utils.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 考勤记录服务实现
 *
 * <p>实现上下班打卡和考勤记录分页查询功能。
 * 打卡逻辑：凌晨5点为日期分界线，当天首次请求记上班、再次请求记下班。</p>
 *
 * @author sagiri
 */
@Service
public class AttendanceRecordServiceImpl implements AttendanceRecordService {

    private final AttendanceRecordMapper attendanceRecordMapper;

    /**
     * 构造器注入考勤Mapper
     *
     * @param attendanceRecordMapper 考勤记录Mapper
     */
    public AttendanceRecordServiceImpl(AttendanceRecordMapper attendanceRecordMapper) {
        this.attendanceRecordMapper = attendanceRecordMapper;
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
}
