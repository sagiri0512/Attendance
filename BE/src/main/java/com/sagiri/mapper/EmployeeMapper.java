package com.sagiri.mapper;

import com.sagiri.entity.Employee;
import com.sagiri.vo.PL;
import com.sagiri.vo.PM;
import com.sagiri.vo.UserInfoVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EmployeeMapper {
    Employee getEmployeeByEmpNo(@Param("empNo") String empNo);

    UserInfoVo getEmployeeInfo(@Param("id") Long id);

    Integer insertEmployee(Employee employee);

    Integer updateEmpNoById(@Param("id") Long id, @Param("empNo") String empNo);

    List<PM> getAllPM();

    List<PL> getPLByPMId(@Param("pmId") Long pmId);
}
