package com.sagiri.service.impl;

import com.sagiri.common.Result;
import com.sagiri.entity.Employee;
import com.sagiri.mapper.EmployeeMapper;
import com.sagiri.service.EmployeeService;
import com.sagiri.service.TokenListService;
import com.sagiri.utils.BcryptUtil;
import com.sagiri.utils.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeMapper employeeMapper;
    private final TokenListService tokenListService;

    public EmployeeServiceImpl(EmployeeMapper employeeMapper, TokenListService tokenListService) {
        this.employeeMapper = employeeMapper;
        this.tokenListService = tokenListService;
    }

    @Override
    public Result<?> login(String empNo, String password) {
        if(!StringUtils.hasText(empNo) || !StringUtils.hasText(password)){
            return Result.error(400, "请输入工号和密码！");
        }
        Employee employee = employeeMapper.getEmployeeByEmpNo(empNo);
        if(employee == null || !BcryptUtil.matches(password, employee.getPassword())){
            return Result.error(400, "工号或者用户名错误！");
        }
        String jwtToken = JwtUtil.generateToken(String.valueOf(employee.getId()), String.valueOf(employee.getRole()), employee.getRealName());

        tokenListService.setToken(jwtToken);

        return  Result.success(jwtToken);
    }
}
