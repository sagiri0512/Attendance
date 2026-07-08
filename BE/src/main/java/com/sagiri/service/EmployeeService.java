package com.sagiri.service;

import com.sagiri.common.Result;

public interface EmployeeService {
    public Result<?> login(String empNo, String password);

    public Result<?> logout(String header);
}
