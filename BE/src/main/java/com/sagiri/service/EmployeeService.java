package com.sagiri.service;

import com.sagiri.vo.Result;

public interface EmployeeService {
    public Result<?> login(String empNo, String password);

    public Result<?> logout(String header);

    public Result<?> getUserInfoByJWT(String header);
}
