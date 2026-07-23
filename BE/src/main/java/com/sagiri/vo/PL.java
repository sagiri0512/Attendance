package com.sagiri.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * 项目组长(PL)下拉选项视图对象
 *
 * <p>用于前端添加/编辑员工时选择直属PL的下拉框数据源。</p>
 *
 * @author sagiri
 */
@Getter
@Setter
public class PL {

    /** 项目组长ID */
    private Long id;
    /** 项目组长姓名 */
    private String PLName;
}
