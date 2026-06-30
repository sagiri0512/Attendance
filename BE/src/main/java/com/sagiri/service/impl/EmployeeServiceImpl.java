package com.sagiri.service.impl;

import com.sagiri.common.Result;
import com.sagiri.entity.Employee;
import com.sagiri.mapper.EmployeeMapper;
import com.sagiri.service.EmployeeService;
import com.sagiri.utils.BcryptUtil;
import com.sagiri.utils.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeMapper employeeMapper;

    public EmployeeServiceImpl(EmployeeMapper employeeMapper) {
        this.employeeMapper = employeeMapper;
    }

    @Override
    public Result<?> login(String empNo, String password) {
        if(!StringUtils.hasText(empNo) || !StringUtils.hasText(password)){
            return Result.error(400, "请输入用户名和密码！");
        }
        Employee employee = employeeMapper.getEmployeeByEmpNo(empNo);
        if(employee == null){
            return Result.error(400, "用户名不存在！");
        }
        if(!BcryptUtil.matches(password, employee.getPassword())){
            return Result.error(400, "密码输入错误！");
        }
        return  Result.success(JwtUtil.generateToken(String.valueOf(employee.getId()), String.valueOf(employee.getRole()), employee.getRealName()));
    }
}
