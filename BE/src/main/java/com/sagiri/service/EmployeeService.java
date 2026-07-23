package com.sagiri.service;

import com.sagiri.dto.EmployeeSaveDTO;
import com.sagiri.entity.Employee;
import com.sagiri.vo.Result;

/**
 * 员工业务服务接口
 *
 * <p>提供认证、员工CRUD及组织架构查询功能。</p>
 *
 * @author sagiri
 */
public interface EmployeeService {

    /**
     * 用户登录
     *
     * @param empNo    工号
     * @param password 密码（明文，内部BCrypt比对）
     * @return 登录结果，成功时data包含JWT令牌和用户信息
     */
    Result<?> login(String empNo, String password);

    /**
     * 用户登出（移除Redis令牌白名单）
     *
     * @param header Authorization请求头
     * @return 登出结果
     */
    Result<?> logout(String header);

    /**
     * 通过JWT获取当前用户信息
     *
     * @param header Authorization请求头
     * @return 用户基本信息（姓名、工号、角色、所属PM/PL）
     */
    Result<?> getUserInfoByJWT(String header);

    /**
     * 新增员工
     *
     * <p>自动生成工号、填充默认值，同时写入employee和social_insurance_config表。</p>
     *
     * @param employee 员工保存DTO（含嵌套社保配置）
     * @return 新增员工完整信息
     */
    Result<?> addNewEmployee(EmployeeSaveDTO employee);

    /**
     * 获取所有项目经理列表
     *
     * @return PM下拉列表（id + 姓名）
     */
    Result<?> getAllPM();

    /**
     * 根据项目经理ID查询项目组长列表
     *
     * @param pmId 项目经理ID
     * @return PL下拉列表（id + 姓名）
     */
    Result<?> getPLByPMId(Long pmId);
}
