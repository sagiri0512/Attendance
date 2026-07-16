package com.sagiri.service.impl;

import com.sagiri.entity.Employee;
import com.sagiri.entity.LoginUser;
import com.sagiri.mapper.EmployeeMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final EmployeeMapper employeeMapper;

    public UserDetailsServiceImpl(EmployeeMapper employeeMapper) {
        this.employeeMapper = employeeMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee employee = employeeMapper.getEmployeeByEmpNo(username);

        //TODO 查询对应权限信息
        return new LoginUser(employee);
    }
}
