package com.sagiri.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * 项目经理(PM)下拉选项视图对象
 *
 * <p>用于前端添加/编辑员工时选择直属PM的下拉框数据源。</p>
 *
 * @author sagiri
 */
@Getter
@Setter
public class PM {

    /** 项目经理ID */
    private Long id;
    /** 项目经理姓名 */
    private String PMName;
}
