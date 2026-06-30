package com.sagiri.mapper;

import com.sagiri.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface EmployeeMapper {
    Employee getEmployeeByEmpNo(@Param("empNo") String empNo);
}
