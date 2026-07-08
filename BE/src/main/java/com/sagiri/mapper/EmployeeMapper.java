package com.sagiri.mapper;

import com.sagiri.entity.Employee;
import com.sagiri.vo.UserInfoVo;
import org.apache.ibatis.annotations.Param;

public interface EmployeeMapper {
    Employee getEmployeeByEmpNo(@Param("empNo") String empNo);

    UserInfoVo getEmployeeInfo(@Param("id") Long id);
}
