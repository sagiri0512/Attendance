package com.sagiri.service.impl;

import com.sagiri.entity.Employee;
import com.sagiri.entity.LoginUser;
import com.sagiri.mapper.EmployeeMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Spring Security 用户详情服务实现
 *
 * <p>实现Security的{@link UserDetailsService}接口，
 * 通过工号加载用户信息，将{@link Employee}适配为{@link LoginUser}。</p>
 *
 * @author sagiri
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final EmployeeMapper employeeMapper;

    /**
     * 构造器注入员工Mapper
     *
     * @param employeeMapper 员工Mapper
     */
    public UserDetailsServiceImpl(EmployeeMapper employeeMapper) {
        this.employeeMapper = employeeMapper;
    }

    /**
     * 根据工号加载用户
     *
     * <p>用于Spring Security认证流程，通过工号查询员工信息后
     * 封装为LoginUser返回给认证管理器进行密码比对。</p>
     *
     * @param username 工号（empNo）
     * @return Spring Security用户主体
     * @throws UsernameNotFoundException 工号不存在时抛出
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee employee = employeeMapper.getEmployeeByEmpNo(username);
        if (employee == null) {
            throw new UsernameNotFoundException("账号密码错误！");
        }
        return new LoginUser(employee);
    }
}
