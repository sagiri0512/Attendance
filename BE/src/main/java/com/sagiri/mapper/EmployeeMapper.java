package com.sagiri.mapper;

import com.sagiri.entity.Employee;
import org.apache.ibatis.annotations.Param;

public interface EmployeeMapper {
    Employee getEmployeeByEmpNo(@Param("empNo") String empNo);
}
