package com.sagiri.service;

import com.sagiri.entity.Employee;
import com.sagiri.vo.Result;

public interface EmployeeService {
    public Result<?> login(String empNo, String password);

    public Result<?> logout(String header);

    public Result<?> getUserInfoByJWT(String header);

    public Result<?> addNewEmployee(Employee employee);

    public Result<?> getAllPM();

    public Result<?> getPLByPMId(Long pmId);
}
