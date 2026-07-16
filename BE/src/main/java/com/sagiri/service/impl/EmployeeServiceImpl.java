package com.sagiri.service.impl;

import com.sagiri.common.Result;
import com.sagiri.entity.Employee;
import com.sagiri.entity.LoginUser;
import com.sagiri.mapper.EmployeeMapper;
import com.sagiri.service.EmployeeService;
import com.sagiri.service.TokenListService;
import com.sagiri.utils.BcryptUtil;
import com.sagiri.utils.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeMapper employeeMapper;
    private final TokenListService tokenListService;
    private final AuthenticationManager authenticationManager;

    public EmployeeServiceImpl(EmployeeMapper employeeMapper, TokenListService tokenListService, AuthenticationManager authenticationManager) {
        this.employeeMapper = employeeMapper;
        this.tokenListService = tokenListService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Result<?> login(String empNo, String password) {
        if (!StringUtils.hasText(empNo) || !StringUtils.hasText(password)) {
            return Result.error(400, "请输入工号和密码！");
        }
        //AuthenticationManager 进行用户认证
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(empNo, password);
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        //如果认证通过了，生成一个JWT并返回
        LoginUser loginUser = (LoginUser)authenticate.getPrincipal();
        String jwtToken = JwtUtil.generateToken(String.valueOf(loginUser.getEmployee().getId()), String.valueOf(loginUser.getEmployee().getRole()), loginUser.getEmployee().getRealName());

        //将JWT保存到Redis中
        tokenListService.setToken(jwtToken);

        return  Result.success(jwtToken);
    }

    @Override
    public Result<?> logout(String header) {
        String token = header.substring(7);
        tokenListService.removeToken(token);
        return Result.success("已登出！");
    }

    @Override
    public Result<?> getUserInfoByJWT(String header) {
        String token = header.substring(7);
        Long id = Long.valueOf(JwtUtil.getUserId(token));
        return Result.success(employeeMapper.getEmployeeInfo(id));
    }
}
