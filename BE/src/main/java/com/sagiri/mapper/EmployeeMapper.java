package com.sagiri.mapper;

import com.sagiri.entity.Employee;
import com.sagiri.vo.PL;
import com.sagiri.vo.PM;
import com.sagiri.vo.UserInfoVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 员工 Mapper
 *
 * <p>操作 employee 表，提供员工CRUD及组织架构查询功能。</p>
 *
 * @author sagiri
 */
public interface EmployeeMapper {

    /**
     * 根据工号查询员工
     *
     * @param empNo 工号
     * @return 员工实体，不存在返回null
     */
    Employee getEmployeeByEmpNo(@Param("empNo") String empNo);

    /**
     * 根据ID查询员工详情（含所属PM/PL信息）
     *
     * @param id 员工ID
     * @return 用户信息VO
     */
    UserInfoVo getEmployeeInfo(@Param("id") Long id);

    /**
     * 插入新员工（使用自增主键）
     *
     * @param employee 员工实体（id字段会被回填）
     * @return 影响行数
     */
    Integer insertEmployee(Employee employee);

    /**
     * 更新员工工号
     *
     * @param id    员工ID
     * @param empNo 新工号
     * @return 影响行数
     */
    Integer updateEmpNoById(@Param("id") Long id, @Param("empNo") String empNo);

    /**
     * 查询所有项目经理
     *
     * @return PM列表（id + 姓名）
     */
    List<PM> getAllPM();

    /**
     * 根据PM查询下属项目组长
     *
     * @param pmId 项目经理ID
     * @return PL列表（id + 姓名）
     */
    List<PL> getPLByPMId(@Param("pmId") Long pmId);

    List<Long> getAllEmpId();
}
