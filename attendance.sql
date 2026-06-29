CREATE TABLE department (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '部门ID',
  dept_name VARCHAR(50) NOT NULL COMMENT '部门名称',
  parent_id BIGINT DEFAULT 0 COMMENT '上级部门ID，0表示顶级部门',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT DEFAULT 0 COMMENT '0未删除 1已删除',
  UNIQUE KEY uk_dept_name (dept_name)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

CREATE TABLE employee (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '员工ID',
  emp_no VARCHAR(20) NOT NULL COMMENT '工号（自动生成，如EMP0001）',
  username VARCHAR(50) NOT NULL COMMENT '登录用户名',
  PASSWORD VARCHAR(100) NOT NULL COMMENT '登录密码（BCrypt加密存储）',
  real_name VARCHAR(50) COMMENT '员工姓名',
  dept_id BIGINT COMMENT '所属部门ID（关联department.id）',
  pm_id BIGINT COMMENT '所属PM员工ID（自关联employee.id）',
  pl_id BIGINT COMMENT '所属PL员工ID（自关联employee.id）',
  ROLE TINYINT NOT NULL DEFAULT 0 COMMENT '角色：0员工(PG) 1PL 2PM 3HR/管理部 4系统管理员',
  phone VARCHAR(20) COMMENT '联系电话',

  /* 薪资组成（管理部设置） */
  base_salary DECIMAL(10,2) DEFAULT 0 COMMENT '基本工资',
  position_salary DECIMAL(10,2) DEFAULT 0 COMMENT '岗位工资',
  performance_bonus DECIMAL(10,2) DEFAULT 0 COMMENT '绩效奖金（PM设置，组共享）',
  housing_subsidy DECIMAL(10,2) DEFAULT 0 COMMENT '房补',
  car_subsidy DECIMAL(10,2) DEFAULT 0 COMMENT '车补',
  meal_subsidy DECIMAL(10,2) DEFAULT 0 COMMENT '餐补',
  other_subsidy DECIMAL(10,2) DEFAULT 0 COMMENT '其他补贴',

  /* 社保/公积金基数与比例（管理部设置，员工级可单独调整） */
  social_insurance_base DECIMAL(10,2) DEFAULT 0 COMMENT '社保缴费基数（为0则按基本工资）',
  housing_fund_base DECIMAL(10,2) DEFAULT 0 COMMENT '公积金缴费基数（为0则按基本工资）',
  housing_fund_ratio_personal DECIMAL(5,2) DEFAULT NULL COMMENT '公积金个人缴费比例(%)，为NULL则按全局配置',
  housing_fund_ratio_company DECIMAL(5,2) DEFAULT NULL COMMENT '公积金公司缴费比例(%)，为NULL则按全局配置',

  STATUS TINYINT DEFAULT 1 COMMENT '账号状态：0禁用 1正常',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT DEFAULT 0 COMMENT '0未删除 1已删除',
  UNIQUE KEY uk_emp_no (emp_no),
  UNIQUE KEY uk_username (username),
  INDEX idx_dept_id (dept_id),
  INDEX idx_pm_id (pm_id),
  INDEX idx_pl_id (pl_id)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COMMENT='员工表';

CREATE TABLE attendance_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '打卡记录ID',
  emp_id BIGINT NOT NULL COMMENT '员工ID（关联employee.id）',
  record_date DATE NOT NULL COMMENT '打卡日期',
  clock_in_time DATETIME COMMENT '当天最早打卡时间（上班时间）',
  clock_out_time DATETIME COMMENT '当天最晚打卡时间（下班时间）',
  STATUS TINYINT DEFAULT 0 COMMENT '打卡状态：0正常 1迟到 2早退 3缺勤',
  absent_hours DECIMAL(4,1) DEFAULT 0 COMMENT '缺勤小时数（迟到/早退/缺勤均在此体现）',
  remark VARCHAR(200) COMMENT '备注信息',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_emp_date (emp_id, record_date),
  INDEX idx_record_date (record_date),
  INDEX idx_emp_id (emp_id)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COMMENT='打卡记录表';

CREATE TABLE leave_request (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '请假申请ID',
  emp_id BIGINT NOT NULL COMMENT '申请人ID（关联employee.id）',
  leave_type TINYINT NOT NULL COMMENT '请假类型：1事假 2病假 3年假 4调休',
  start_time DATETIME NOT NULL COMMENT '请假开始时间',
  end_time DATETIME NOT NULL COMMENT '请假结束时间',
  days DECIMAL(5,1) COMMENT '请假天数（扣除周末）',
  reason VARCHAR(500) COMMENT '请假原因',

  /* 审批阶段 */
  step TINYINT DEFAULT 1 COMMENT '当前审批阶段：1等PL审批 2等PM审批 3审批完成',

  /* PL 审批 */
  pl_status TINYINT DEFAULT 0 COMMENT 'PL审批状态：0待审 1通过 2驳回',
  pl_remark VARCHAR(200) COMMENT 'PL审批备注',
  pl_approve_time DATETIME COMMENT 'PL审批时间',

  /* PM 审批 */
  pm_status TINYINT DEFAULT 0 COMMENT 'PM审批状态：0待审 1通过 2驳回',
  pm_remark VARCHAR(200) COMMENT 'PM审批备注',
  pm_approve_time DATETIME COMMENT 'PM审批时间',

  /* 最终结果 */
  final_status TINYINT DEFAULT 0 COMMENT '最终状态：0审批中 1已通过 2已驳回',

  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '申请提交时间',
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_emp_id (emp_id),
  INDEX idx_final_status (final_status)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COMMENT='请假申请表';

CREATE TABLE overtime_request (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '加班申请ID',
  emp_id BIGINT NOT NULL COMMENT '申请人ID（关联employee.id）',
  overtime_date DATE NOT NULL COMMENT '加班日期',
  apply_hours DECIMAL(4,1) NOT NULL COMMENT '申请加班小时数',
  actual_hours DECIMAL(4,1) DEFAULT 0 COMMENT '实际加班小时数（系统根据打卡记录自动计算）',
  salary_multiplier DECIMAL(2,1) DEFAULT 1.5 COMMENT '工资倍数：1.5平时 2.0周末 3.0节假日',
  reason VARCHAR(500) COMMENT '加班原因',
  is_special TINYINT DEFAULT 0 COMMENT '是否特殊情况：0普通 1特殊情况',
  is_cross_day TINYINT DEFAULT 0 COMMENT '是否跨天：0不跨天 1跨天',
  step TINYINT DEFAULT 1 COMMENT '当前审批阶段：1等PL审批 2等PM审批 3审批完成',

  /* PL 审批 */
  pl_status TINYINT DEFAULT 0 COMMENT 'PL审批状态：0待审 1通过 2驳回',
  pl_remark VARCHAR(200) COMMENT 'PL审批备注',
  pl_approve_time DATETIME COMMENT 'PL审批时间',

  /* PM 审批 */
  pm_status TINYINT DEFAULT 0 COMMENT 'PM审批状态：0待审 1通过 2驳回',
  pm_remark VARCHAR(200) COMMENT 'PM审批备注',
  pm_approve_time DATETIME COMMENT 'PM审批时间',

  /* 最终结果 */
  final_status TINYINT DEFAULT 0 COMMENT '最终状态：0审批中 1已通过 2已驳回',

  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '申请提交时间',
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

  INDEX idx_emp_id (emp_id),
  INDEX idx_overtime_date (overtime_date),
  INDEX idx_final_status (final_status)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COMMENT='加班申请表';

CREATE TABLE attendance_correction (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '打卡变更申请ID',
  emp_id BIGINT NOT NULL COMMENT '申请人ID（关联employee.id）',
  record_date DATE NOT NULL COMMENT '需要变更的打卡日期',

  /* 原始值（快照） */
  old_clock_in DATETIME COMMENT '原上班打卡时间',
  old_clock_out DATETIME COMMENT '原下班打卡时间',

  /* 目标值 */
  new_clock_in DATETIME NOT NULL COMMENT '变更后上班打卡时间',
  new_clock_out DATETIME COMMENT '变更后下班打卡时间',

  reason VARCHAR(500) COMMENT '变更原因',
  step TINYINT DEFAULT 1 COMMENT '当前审批阶段：1等PL审批 2等PM审批 3审批完成',

  /* PL 审批 */
  pl_status TINYINT DEFAULT 0 COMMENT 'PL审批状态：0待审 1通过 2驳回',
  pl_remark VARCHAR(200) COMMENT 'PL审批备注',
  pl_approve_time DATETIME COMMENT 'PL审批时间',

  /* PM 审批 */
  pm_status TINYINT DEFAULT 0 COMMENT 'PM审批状态：0待审 1通过 2驳回',
  pm_remark VARCHAR(200) COMMENT 'PM审批备注',
  pm_approve_time DATETIME COMMENT 'PM审批时间',

  /* 最终 & 执行 */
  final_status TINYINT DEFAULT 0 COMMENT '最终状态：0审批中 1已通过 2已驳回',
  executed TINYINT DEFAULT 0 COMMENT '是否已在打卡记录中执行：0未执行 1已执行',
  executed_time DATETIME COMMENT '变更执行时间',

  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '申请提交时间',
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

  INDEX idx_emp_id (emp_id),
  INDEX idx_final_status (final_status),
  INDEX idx_executed (executed)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COMMENT='打卡变更申请表';

CREATE TABLE holiday_config (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '配置ID',
  holiday_date DATE NOT NULL COMMENT '节假日日期',
  holiday_name VARCHAR(50) COMMENT '节假日名称（如：春节、国庆节）',
  multiplier DECIMAL(2,1) DEFAULT 3.0 COMMENT '工资倍数：2.0周末 3.0法定节假日',
  is_workday TINYINT DEFAULT 0 COMMENT '是否调休工作日：0休息日 1调休工作日',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY uk_holiday_date (holiday_date)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COMMENT='节假日配置表';

CREATE TABLE social_insurance_config (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '配置ID',
  config_year INT NOT NULL COMMENT '适用年份（每年更新一次）',
  pension_personal DECIMAL(5,2) DEFAULT 8.00 COMMENT '养老保险个人缴费比例(%)',
  pension_company DECIMAL(5,2) DEFAULT 16.00 COMMENT '养老保险公司缴费比例(%)',
  medical_personal DECIMAL(5,2) DEFAULT 2.00 COMMENT '医疗保险个人缴费比例(%)',
  medical_company DECIMAL(5,2) DEFAULT 8.00 COMMENT '医疗保险公司缴费比例(%)',
  unemployment_personal DECIMAL(5,2) DEFAULT 0.50 COMMENT '失业保险个人缴费比例(%)',
  unemployment_company DECIMAL(5,2) DEFAULT 0.50 COMMENT '失业保险公司缴费比例(%)',
  injury_company DECIMAL(5,2) DEFAULT 0.50 COMMENT '工伤保险公司缴费比例(%)',
  maternity_company DECIMAL(5,2) DEFAULT 1.00 COMMENT '生育保险公司缴费比例(%)',
  housing_fund_personal DECIMAL(5,2) DEFAULT 12.00 COMMENT '住房公积金个人缴费比例(%)',
  housing_fund_company DECIMAL(5,2) DEFAULT 12.00 COMMENT '住房公积金公司缴费比例(%)',
  min_base DECIMAL(10,2) COMMENT '社保缴费基数下限（社平工资60%）',
  max_base DECIMAL(10,2) COMMENT '社保缴费基数上限（社平工资300%）',
  remark VARCHAR(200) COMMENT '备注说明',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY uk_config_year (config_year)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COMMENT='社保参数配置表';

CREATE TABLE group_performance (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '绩效配置ID',
  pm_id BIGINT NOT NULL COMMENT 'PM员工ID（关联employee.id）',
  MONTH VARCHAR(7) NOT NULL COMMENT '绩效月份，格式：2026-06',
  performance_amount DECIMAL(10,2) NOT NULL COMMENT '组绩效金额（组内所有员工共享此金额）',
  set_by TINYINT DEFAULT 0 COMMENT '设置人：0PM 1HR',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_pm_month (pm_id, MONTH),
  INDEX idx_pm_id (pm_id),
  INDEX idx_month (MONTH)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COMMENT='组绩效表';

