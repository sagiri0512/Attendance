package com.sagiri.vo;

import com.github.pagehelper.PageInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 考勤分页视图对象
 *
 * <p>封装分页查询考勤记录的返回结构，
 * 提供静态方法从PageHelper的{@link PageInfo}直接转换。</p>
 *
 * @author sagiri
 */
@Setter
@Getter
public class AttendancePageVO {

    /** 分页数据列表 */
    private List<AttendanceVO> records;
    /** 总记录条数 */
    private Long total;

    /**
     * 从 PageHelper 分页对象转换
     *
     * @param pageInfo PageHelper分页结果
     * @return 考勤分页VO
     */
    public static AttendancePageVO from(PageInfo<AttendanceVO> pageInfo) {
        AttendancePageVO vo = new AttendancePageVO();
        vo.setRecords(pageInfo.getList());
        vo.setTotal(pageInfo.getTotal());
        return vo;
    }
}
