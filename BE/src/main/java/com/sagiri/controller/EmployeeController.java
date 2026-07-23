package com.sagiri.controller;

import com.sagiri.dto.EmployeeSaveDTO;
import com.sagiri.entity.Employee;
import com.sagiri.service.EmployeeService;
import com.sagiri.vo.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 员工管理控制器（HR专用）
 *
 * <p>提供员工新增、PM/PL列表查询等HR管理功能。
 * 所有接口要求角色为3（HR）方可访问。</p>
 *
 * @author sagiri
 */
@RestController
@RequestMapping("/employee")
@PreAuthorize("hasAuthority('3')")
public class EmployeeController {

    private final EmployeeService employeeService;

    /**
     * 构造器注入员工服务
     *
     * @param employeeService 员工业务服务
     */
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * 添加新员工
     *
     * <p>接收前端提交的完整员工信息（含社保配置），
     * 自动生成工号、填充默认值，同时写入employee和social_insurance_config表。
     * 返回完整的员工信息。</p>
     *
     * @param employee 员工保存DTO（含嵌套社保配置）
     * @return 新增员工完整信息
     */
    @PostMapping("/add")
    public ResponseEntity<Result<?>> add(@RequestBody EmployeeSaveDTO employee) {
        Result<?> result = employeeService.addNewEmployee(employee);
        return ResponseEntity.status(result.getCode()).body(result);
    }

    /**
     * 获取所有项目经理列表
     *
     * <p>用于前端添加员工时选择上级PM的下拉框数据源。</p>
     *
     * @return 项目经理列表（id + 姓名）
     */
    @GetMapping("/pm-list")
    public ResponseEntity<Result<?>> getAllPM() {
        Result<?> result = employeeService.getAllPM();
        return ResponseEntity.status(result.getCode()).body(result);
    }

    /**
     * 根据项目经理查询项目组长列表
     *
     * <p>用于前端添加员工时选择上级PL的下拉框数据源，按PM过滤。</p>
     *
     * @param pmId 项目经理ID
     * @return 该PM下属的项目组长列表（id + 姓名）
     */
    @GetMapping("/pl-list")
    public ResponseEntity<Result<?>> getPLByPMId(@RequestParam("pmId") Long pmId) {
        Result<?> result = employeeService.getPLByPMId(pmId);
        return ResponseEntity.status(result.getCode()).body(result);
    }
}
