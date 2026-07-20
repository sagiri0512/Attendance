package com.sagiri.vo;

import com.github.pagehelper.PageInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class AttendancePageVO {
    private List<AttendanceVO> records;   // 分页数据列表
    private Long total;                    // 总记录条数

    // ========== 从 PageInfo 直接转换的静态方法 ==========
    public static AttendancePageVO from(PageInfo<AttendanceVO> pageInfo) {
        AttendancePageVO vo = new AttendancePageVO();
        vo.setRecords(pageInfo.getList());
        vo.setTotal(pageInfo.getTotal());
        return vo;
    }
}
