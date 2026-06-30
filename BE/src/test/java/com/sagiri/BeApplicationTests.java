package com.sagiri;

import com.sagiri.mapper.EmployeeMapper;
import com.sagiri.service.EmployeeService;
import com.sagiri.utils.BcryptUtil;
import com.sagiri.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BeApplicationTests {

    @Autowired
    EmployeeService employeeService;
    @Test
    public void test(){
        String enpNo = "NO00001";
        String pws = "123456";

        String jwt = (String) employeeService.login(enpNo, pws).getData();

        System.out.println(jwt);

        System.out.println(JwtUtil.getRealName(jwt));

    }

}
