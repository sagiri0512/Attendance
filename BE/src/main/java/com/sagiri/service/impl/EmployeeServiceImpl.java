package com.sagiri.service.impl;

import com.sagiri.constant.SalaryConfig;
import com.sagiri.entity.Employee;
import com.sagiri.vo.PL;
import com.sagiri.vo.Result;
import com.sagiri.entity.LoginUser;
import com.sagiri.mapper.EmployeeMapper;
import com.sagiri.service.EmployeeService;
import com.sagiri.service.TokenListService;
import com.sagiri.utils.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> addNewEmployee(Employee employee) {

        // ========== 1. 基础非空校验 ==========
        if (employee.getRealName() == null || employee.getRealName().trim().isEmpty()) {
            return Result.error(401, "姓名不能为空");
        }
        if (employee.getRole() == null) {
            return Result.error(401, "角色不能为空");
        }
        Byte role = employee.getRole();
        if (role < 0 || role > 3) {
            return Result.error(401, "角色值非法（0PG/1PL/2PM/3HR）");
        }

        // ========== 2. 薪资非负校验 ==========
        if (isNegative(employee.getBaseSalary())
                || isNegative(employee.getPositionSalary())
                || isNegative(employee.getHousingSubsidy())
                || isNegative(employee.getCarSubsidy())
                || isNegative(employee.getMealSubsidy())
                || isNegative(employee.getOtherSubsidy())) {
            return Result.error(401, "薪资/补贴不能为负数");
        }

        // ========== 3. 角色与上级关系校验 ==========
        Result<?> roleCheck = validateRoleAndSuperior(role, employee.getPmId(), employee.getPlId());
        if (roleCheck != null) {
            return roleCheck;
        }

        // ========== 4. 手机号格式校验 ==========
        if (employee.getPhone() != null && !employee.getPhone().trim().isEmpty()) {
            if (!employee.getPhone().matches("^1[3-9]\\d{9}$")) {
                return Result.error(401, "手机号格式不正确");
            }
        }

        // ========== 5. 社保/公积金基数与比例兜底 ==========
        fillDefaultValues(employee);

        // ========== 6. 设置系统字段 ==========
        employee.setPassword("$2a$10$P/c1Kt6n8OiVvnRkztct4uR2.9J7h4L5JBfejX45qBJ57sl/y5Aea");
        employee.setEmpNo("NO00000");

        // ========== 7. 插入 + 修正工号 ==========
        employeeMapper.insertEmployee(employee);
        String empNo = "NO" + String.format("%05d", employee.getId());
        employeeMapper.updateEmpNoById(employee.getId(), empNo);

        return Result.success("添加成功！");
    }

    @Override
    public Result<?> getAllPM() {
        return Result.success(employeeMapper.getAllPM());
    }

    @Override
    public Result<?> getPLByPMId(Long pmId) {
        if(pmId == null){
            return Result.error(401, "请选择PM！");
        }
        List<PL> plList = employeeMapper.getPLByPMId(pmId);
        if(plList.isEmpty()){
            return Result.error(401, "该PM下不存在PL，请先设置PL！");
        }
        return Result.success(plList);
    }

    /**
     * 角色与上级关系校验
     * PG(0): 必须有 PL 和 PM
     * PL(1): 必须有 PM，不能有 PL
     * PM(2)/HR(3): 不能有 PM 和 PL
     */
    private Result<?> validateRoleAndSuperior(Byte role, Long pmId, Long plId) {
        if (role == 0) {
            if (pmId == null || plId == null) {
                return Result.error(401, "PG必须设置PL和PM");
            }
        } else if (role == 1) {
            if (pmId == null) {
                return Result.error(401, "PL必须设置PM");
            }
            if (plId != null) {
                return Result.error(401, "PL不能设置PL上级");
            }
        } else if (role == 2 || role == 3) {
            if (pmId != null || plId != null) {
                return Result.error(401, "PM/HR不能设置上级");
            }
        }
        return null;  // 校验通过
    }

    /**
     * 社保/公积金基数与比例兜底
     * - 基数为0 → 用基本工资
     * - 比例为NULL → 用全局配置
     */
    private void fillDefaultValues(Employee employee) {
        Double baseSalary = employee.getBaseSalary() != null ? employee.getBaseSalary() : 0.0;

        // 社保基数 = 0 → 用基本工资
        if (employee.getSocialInsuranceBase() == null
                || employee.getSocialInsuranceBase() == 0) {
            employee.setSocialInsuranceBase(baseSalary);
        }

        // 公积金基数 = 0 → 用基本工资
        if (employee.getHousingFundBase() == null
                || employee.getHousingFundBase() == 0) {
            employee.setHousingFundBase(baseSalary);
        }

        // 个人比例 = NULL → 8%
        if (employee.getHousingFundRatioPersonal() == null) {
            employee.setHousingFundRatioPersonal(SalaryConfig.HOUSING_FUND_RATIO_PERSONAL);
        }

        // 公司比例 = NULL → 8%
        if (employee.getHousingFundRatioCompany() == null) {
            employee.setHousingFundRatioCompany(SalaryConfig.HOUSING_FUND_RATIO_COMPANY);
        }
    }

    /**
     * 判断薪资是否为负数
     */
    private boolean isNegative(Double value) {
        return value != null && value < 0;
    }
}
