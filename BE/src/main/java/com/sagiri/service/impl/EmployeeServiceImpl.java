package com.sagiri.service.impl;

import com.sagiri.constant.SalaryConfig;
import com.sagiri.dto.EmployeeSaveDTO;
import com.sagiri.dto.EmployeeSaveDTO.SocialInsuranceConfigDTO;
import com.sagiri.entity.Employee;
import com.sagiri.entity.SocialInsuranceConfig;
import com.sagiri.vo.PL;
import com.sagiri.vo.Result;
import com.sagiri.entity.LoginUser;
import com.sagiri.mapper.EmployeeMapper;
import com.sagiri.mapper.SocialInsuranceConfigMapper;
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

/**
 * 员工服务实现
 * <p>负责登录认证、员工增删改查、角色校验、社保配置初始化等核心业务。
 */
@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeMapper employeeMapper;
    private final SocialInsuranceConfigMapper socialInsuranceConfigMapper;
    private final TokenListService tokenListService;
    private final AuthenticationManager authenticationManager;

    /**
     * 构造器注入所有依赖
     */
    public EmployeeServiceImpl(EmployeeMapper employeeMapper,
                               SocialInsuranceConfigMapper socialInsuranceConfigMapper,
                               TokenListService tokenListService,
                               AuthenticationManager authenticationManager) {
        this.employeeMapper = employeeMapper;
        this.socialInsuranceConfigMapper = socialInsuranceConfigMapper;
        this.tokenListService = tokenListService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * 员工登录
     * <p>走 Spring Security 认证链 → 生成 JWT → 存 Redis
     *
     * @param empNo    工号
     * @param password 明文密码
     * @return JWT token 字符串
     */
    @Override
    public Result<?> login(String empNo, String password) {
        if (!StringUtils.hasText(empNo) || !StringUtils.hasText(password)) {
            return Result.error(400, "请输入工号和密码！");
        }
        // AuthenticationManager 进行用户认证
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(empNo, password);
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        // 认证通过 → 生成 JWT
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String jwtToken = JwtUtil.generateToken(String.valueOf(loginUser.getEmployee().getId()), String.valueOf(loginUser.getEmployee().getRole()), loginUser.getEmployee().getRealName());

        // JWT 存入 Redis 白名单
        tokenListService.setToken(jwtToken);

        return Result.success(jwtToken);
    }

    /**
     * 员工登出
     * <p>从 Redis 白名单移除 token，使 JWT 即时失效
     *
     * @param header 请求头（含 Bearer 前缀）
     */
    @Override
    public Result<?> logout(String header) {
        String token = header.substring(7);
        tokenListService.removeToken(token);
        return Result.success("已登出！");
    }

    /**
     * 根据 JWT 获取当前用户信息
     *
     * @param header 请求头（含 Bearer 前缀）
     * @return 用户基本信息 VO
     */
    @Override
    public Result<?> getUserInfoByJWT(String header) {
        String token = header.substring(7);
        Long id = Long.valueOf(JwtUtil.getUserId(token));
        return Result.success(employeeMapper.getEmployeeInfo(id));
    }

    /**
     * 新增员工 + 初始化社保配置
     * <p><b>事务内执行 6 步：</b>
     * <ol>
     *   <li>基础非空校验（姓名、角色）</li>
     *   <li>薪资非负校验</li>
     *   <li>角色与上下级关系校验</li>
     *   <li>手机号格式校验</li>
     *   <li>插入员工 → 修正工号（EM0001…）</li>
     *   <li>插入社保公积金配置（基数/比例缺省值处理）</li>
     * </ol>
     *
     * @param employee 前端提交的员工信息 + 社保配置
     * @return 操作结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> addNewEmployee(EmployeeSaveDTO employee) {

        // ========== 1. 基础非空校验 ==========
        if (employee.getRealName() == null || employee.getRealName().trim().isEmpty()) {
            return Result.error(400, "姓名不能为空");
        }
        if (employee.getRole() == null) {
            return Result.error(400, "角色不能为空");
        }
        Byte role = employee.getRole();
        if (role < 0 || role > 3) {
            return Result.error(400, "角色值非法（0PG/1PL/2PM/3HR）");
        }

        // ========== 2. 薪资非负校验 ==========
        if (isNegative(employee.getBaseSalary())
                || isNegative(employee.getPositionSalary())
                || isNegative(employee.getHousingSubsidy())
                || isNegative(employee.getCarSubsidy())
                || isNegative(employee.getMealSubsidy())
                || isNegative(employee.getOtherSubsidy())) {
            return Result.error(400, "薪资/补贴不能为负数");
        }

        // ========== 3. 角色与上级关系校验 ==========
        Result<?> roleCheck = validateRoleAndSuperior(role, employee.getPmId(), employee.getPlId());
        if (roleCheck != null) {
            return roleCheck;
        }

        // ========== 4. 手机号格式校验 ==========
        if (employee.getPhone() != null && !employee.getPhone().trim().isEmpty()) {
            if (!employee.getPhone().matches("^1[3-9]\\d{9}$")) {
                return Result.error(400, "手机号格式不正确");
            }
        }

        // ========== 5. DTO → Entity + 插入 + 修正工号 ==========
        Employee newEmployee = setNewEmployee(employee);
        employeeMapper.insertEmployee(newEmployee);
        String empNo = SalaryConfig.EMP_NO_PREFIX + String.format("%0" + SalaryConfig.EMP_NO_DIGITS + "d", newEmployee.getId());
        employeeMapper.updateEmpNoById(newEmployee.getId(), empNo);

        // ========== 6. 初始化社保公积金配置 ==========
        SocialInsuranceConfig config = setNewSocialInsuranceConfig(employee, newEmployee.getId());
        socialInsuranceConfigMapper.insert(config);

        return Result.success("添加成功！");
    }

    /**
     * 获取所有 PM 列表（用于前端下拉框）
     *
     * @return PM 列表
     */
    @Override
    public Result<?> getAllPM() {
        return Result.success(employeeMapper.getAllPM());
    }

    /**
     * 根据 PM 获取其下辖 PL 列表（用于前端级联下拉框）
     *
     * @param pmId 项目经理 ID
     * @return PL 列表，不存在则返回错误
     */
    @Override
    public Result<?> getPLByPMId(Long pmId) {
        if (pmId == null) {
            return Result.error(400, "请选择PM！");
        }
        List<PL> plList = employeeMapper.getPLByPMId(pmId);
        if (plList.isEmpty()) {
            return Result.error(400, "该PM下不存在PL，请先设置PL！");
        }
        return Result.success(plList);
    }

    /**
     * 将 DTO 转换为 Employee 实体（不含工号修正，先插后改）
     *
     * @param employeeSaveDTO 前端提交的员工数据
     * @return 待插入的 Employee 实体（密码为 BCrypt 默认值，工号为占位符）
     */
    private Employee setNewEmployee(EmployeeSaveDTO employeeSaveDTO) {
        Employee newEmp = new Employee();
        // 基本信息
        newEmp.setRealName(employeeSaveDTO.getRealName());
        newEmp.setRole(employeeSaveDTO.getRole());
        newEmp.setPhone(employeeSaveDTO.getPhone());
        newEmp.setPmId(employeeSaveDTO.getPmId());
        newEmp.setPlId(employeeSaveDTO.getPlId());
        // 薪资结构
        newEmp.setBaseSalary(employeeSaveDTO.getBaseSalary());
        newEmp.setPositionSalary(employeeSaveDTO.getPositionSalary());
        newEmp.setHousingSubsidy(employeeSaveDTO.getHousingSubsidy());
        newEmp.setCarSubsidy(employeeSaveDTO.getCarSubsidy());
        newEmp.setMealSubsidy(employeeSaveDTO.getMealSubsidy());
        newEmp.setOtherSubsidy(employeeSaveDTO.getOtherSubsidy());
        newEmp.setPerformanceBonus(employeeSaveDTO.getPerformanceBonus());
        // 系统字段（临时值，后续步骤修正）
        newEmp.setPassword("$2a$10$P/c1Kt6n8OiVvnRkztct4uR2.9J7h4L5JBfejX45qBJ57sl/y5Aea");
        newEmp.setEmpNo(SalaryConfig.EMP_NO_PREFIX + "0".repeat(SalaryConfig.EMP_NO_DIGITS));
        return newEmp;
    }

    /**
     * 角色与上下级关系校验
     * <table border="1">
     *   <tr><th>角色</th><th>必须有 PM</th><th>必须有 PL</th><th>说明</th></tr>
     *   <tr><td>PG(0)</td><td>是</td><td>是</td><td>基层员工，同时挂 PM 和 PL</td></tr>
     *   <tr><td>PL(1)</td><td>是</td><td>否</td><td>小组长，不能自设 PL 上级</td></tr>
     *   <tr><td>PM(2)</td><td>否</td><td>否</td><td>项目经理，无上级</td></tr>
     *   <tr><td>HR(3)</td><td>否</td><td>否</td><td>管理部，无上级</td></tr>
     * </table>
     *
     * @param role 角色值
     * @param pmId 项目经理 ID（可空）
     * @param plId 小组长 ID（可空）
     * @return 校验通过返回 null，否则返回错误 Result
     */
    private Result<?> validateRoleAndSuperior(Byte role, Long pmId, Long plId) {
        if (role == 0) {
            if (pmId == null || plId == null) {
                return Result.error(400, "PG必须设置PL和PM");
            }
        } else if (role == 1) {
            if (pmId == null) {
                return Result.error(400, "PL必须设置PM");
            }
            if (plId != null) {
                return Result.error(400, "PL不能设置PL上级");
            }
        } else if (role == 2 || role == 3) {
            if (pmId != null || plId != null) {
                return Result.error(400, "PM/HR不能设置上级");
            }
        }
        return null;
    }

    /**
     * 将 DTO 中的社保配置转换为实体
     * <p><b>缺省规则：</b>
     * <ul>
     *   <li>社保/公积金基数 = 0 或 null → 取基本工资</li>
     *   <li>各项保险比例 = null → 取 {@link SalaryConfig} 中预定义默认值</li>
     * </ul>
     *
     * @param employeeSaveDTO 包含嵌套 socialInsuranceConfig 的 DTO
     * @return 填充完成的 SocialInsuranceConfig 实体
     */
    private SocialInsuranceConfig setNewSocialInsuranceConfig(EmployeeSaveDTO employeeSaveDTO, Long employeeId) {
        SocialInsuranceConfig config = new SocialInsuranceConfig();
        config.setEmpId(employeeId);

        SocialInsuranceConfigDTO dto = employeeSaveDTO.getSocialInsuranceConfig();

        Double baseSalary = employeeSaveDTO.getBaseSalary() != null ? employeeSaveDTO.getBaseSalary() : 0.0;

        // 基数处理：0 或 null → 基本工资
        Double siBase = (dto == null || dto.getSocialInsuranceBase() == null || dto.getSocialInsuranceBase() == 0)
                ? baseSalary : dto.getSocialInsuranceBase();
        Double hfBase = (dto == null || dto.getHousingFundBase() == null || dto.getHousingFundBase() == 0)
                ? baseSalary : dto.getHousingFundBase();

        config.setSocialInsuranceBase(siBase);
        config.setHousingFundBase(hfBase);

        // 五险一金比例：null → 默认值
        config.setPensionPersonal(getOrDefault(dto, SocialInsuranceConfigDTO::getPensionPersonal, SalaryConfig.PENSION_PERSONAL));
        config.setPensionCompany(getOrDefault(dto, SocialInsuranceConfigDTO::getPensionCompany, SalaryConfig.PENSION_COMPANY));
        config.setMedicalPersonal(getOrDefault(dto, SocialInsuranceConfigDTO::getMedicalPersonal, SalaryConfig.MEDICAL_PERSONAL));
        config.setMedicalCompany(getOrDefault(dto, SocialInsuranceConfigDTO::getMedicalCompany, SalaryConfig.MEDICAL_COMPANY));
        config.setUnemploymentPersonal(getOrDefault(dto, SocialInsuranceConfigDTO::getUnemploymentPersonal, SalaryConfig.UNEMPLOYMENT_PERSONAL));
        config.setUnemploymentCompany(getOrDefault(dto, SocialInsuranceConfigDTO::getUnemploymentCompany, SalaryConfig.UNEMPLOYMENT_COMPANY));
        config.setInjuryCompany(getOrDefault(dto, SocialInsuranceConfigDTO::getInjuryCompany, SalaryConfig.INJURY_COMPANY));
        config.setMaternityCompany(getOrDefault(dto, SocialInsuranceConfigDTO::getMaternityCompany, SalaryConfig.MATERNITY_COMPANY));
        config.setHousingFundPersonal(getOrDefault(dto, SocialInsuranceConfigDTO::getHousingFundPersonal, SalaryConfig.HOUSING_FUND_PERSONAL));
        config.setHousingFundCompany(getOrDefault(dto, SocialInsuranceConfigDTO::getHousingFundCompany, SalaryConfig.HOUSING_FUND_COMPANY));

        // 备注
        config.setRemark(dto != null ? dto.getRemark() : null);

        return config;
    }

    /**
     * 从 DTO 取值，null 时返回默认值
     *
     * @param dto          社保配置 DTO（可为 null）
     * @param getter       DTO 字段的 getter 方法引用
     * @param defaultValue 兜底默认值
     * @return 取值结果，不会为 null
     */
    private Double getOrDefault(SocialInsuranceConfigDTO dto,
                                java.util.function.Function<SocialInsuranceConfigDTO, Double> getter,
                                Double defaultValue) {
        if (dto == null) return defaultValue;
        Double value = getter.apply(dto);
        return value != null ? value : defaultValue;
    }

    /**
     * 判断 Double 值是否为负数（null 视为非负）
     *
     * @param value 待判断的值
     * @return true 表示为负数
     */
    private boolean isNegative(Double value) {
        return value != null && value < 0;
    }
}
